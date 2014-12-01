
package com.innovationWaves.cryptocloud.controller.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Contents;
import com.google.android.gms.drive.DriveApi.ContentsResult;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;

public class GoogleDriveUtils
{

	public static class WebService
	{

		/**
		 * Build and returns a Drive service object authorized with the service
		 * accounts that act on behalf of the given user.
		 * 
		 * @param userEmail
		 *            The email of the user.
		 * @return Drive service object that is ready to make requests.
		 */
		private static Drive getDriveService(Context context)
		{

			HttpTransport httpTransport = new NetHttpTransport();
			JacksonFactory jsonFactory = new JacksonFactory();

			GoogleAccountCredential googleAccountCredential = GoogleAccountCredential
					.usingOAuth2(context, Arrays.asList(DriveScopes.DRIVE));
			googleAccountCredential.setSelectedAccountName(Utilities
					.getAccount(context));

			Drive service = new Drive.Builder(httpTransport, jsonFactory,
					googleAccountCredential).build();
			return service;
		}

		/**
		 * Insert new file.
		 * 
		 * @param service
		 *            Drive API service instance.
		 * @param title
		 *            Title of the file to insert, including the extension.
		 * @param description
		 *            Description of the file to insert.
		 * @param parentId
		 *            Optional parent folder's ID.
		 * @param mimeType
		 *            MIME type of the file to insert.
		 * @param filePath
		 *            Filename of the file to insert.
		 * @return Inserted file metadata if successful, {@code null} otherwise.
		 * @throws IOException
		 */
		public static File insertFile(Context context, String title,
				String description, String mimeType, String filePath) throws IOException
		{

			Drive service = getDriveService(context);

			// File's metadata.
			File body = new File();
			body.setTitle(title);
			body.setDescription(description);
			body.setMimeType(mimeType);

			// Set the parent folder.
//			body.setParents(Arrays.asList(new ParentReference()
//					.setId("appdata")));

			// File's content.
			java.io.File fileContent = new java.io.File(filePath);
			FileContent mediaContent = new FileContent(mimeType, fileContent);

			File file = service.files().insert(body, mediaContent).execute();

			return file;
		}

		/**
		 * Update an existing file's metadata and content.
		 * 
		 * @param service
		 *            Drive API service instance.
		 * @param fileId
		 *            ID of the file to update.
		 * @param newTitle
		 *            New title for the file.
		 * @param newDescription
		 *            New description for the file.
		 * @param newMimeType
		 *            New MIME type for the file.
		 * @param path
		 *            Filename of the new content to upload.
		 * @param newRevision
		 *            Whether or not to create a new revision for this file.
		 * @return Updated file metadata if successful, {@code null} otherwise.
		 * @throws IOException 
		 */
		public static File updateFile(Context context, String fileId,
				String newTitle, String newDescription, String newMimeType,
				String path) throws IOException
		{

				Drive service = getDriveService(context);

				// First retrieve the file from the API.
				File file = service.files().get(fileId).execute();

				// File's new metadata.
				file.setTitle(newTitle);
				file.setDescription(newDescription);
				file.setMimeType(newMimeType);

				// File's new content.
				java.io.File fileContent = new java.io.File(path);
				FileContent mediaContent = new FileContent(newMimeType,
						fileContent);

				// Send the request to the API.
				File updatedFile = service.files()
						.update(fileId, file, mediaContent).execute();

				return updatedFile;
		}

		// Get

		/**
		 * Print a file's metadata.
		 * 
		 * @param service
		 *            Drive API service instance.
		 * @param fileId
		 *            ID of the file to print metadata for.
		 */
		public static void printFile(Context context, String fileId)
		{

			try
			{
				Drive service = getDriveService(context);

				File file = service.files().get(fileId).execute();

				System.out.println("Title: " + file.getTitle());
				System.out.println("Description: " + file.getDescription());
				System.out.println("MIME type: " + file.getMimeType());
			}
			catch (IOException e)
			{
				System.out.println("An error occured: " + e);
			}
		}

		/**
		 * Download a file's content.
		 * 
		 * @param service
		 *            Drive API service instance.
		 * @param file
		 *            Drive File instance.
		 * @return InputStream containing the file's content if successful,
		 *         {@code null} otherwise.
		 */
		public static InputStream downloadFile(Context context, File file)
		{

			Drive service = getDriveService(context);

			if (file.getDownloadUrl() != null
					&& file.getDownloadUrl().length() > 0)
			{
				try
				{
					HttpResponse resp = service
							.getRequestFactory()
							.buildGetRequest(
									new GenericUrl(file.getDownloadUrl()))
							.execute();
					return resp.getContent();
				}
				catch (IOException e)
				{
					// An error occurred.
					e.printStackTrace();
					return null;
				}
			}
			else
			{
				// The file doesn't have any content stored on Drive.
				return null;
			}
		}

	}

	public static class AndroidAPI
	{

		public static DriveFile createFile(
				final GoogleApiClient googleApiClient, final String name,
				final String mime, final byte[] buff) throws IOException
		{

			DriveFile driveFile = null;

			if (googleApiClient.isConnected())
			{
				DriveFolder uploadFolder = com.google.android.gms.drive.Drive.DriveApi
						.getRootFolder(googleApiClient);// getAppFolder(googleApiClient);

				ContentsResult rslt = com.google.android.gms.drive.Drive.DriveApi
						.newContents(googleApiClient).await();
				if (rslt.getStatus().isSuccess())
				{
					Contents cont = rslt.getContents();
					cont.getOutputStream().write(buff);
					MetadataChangeSet meta = new MetadataChangeSet.Builder()
							.setTitle(name).setMimeType(mime).build();
					driveFile = uploadFolder
							.createFile(googleApiClient, meta, cont).await()
							.getDriveFile();
				}
			}

			return driveFile;
		}

		public static DriveFile getFileByDriveId(
				final GoogleApiClient googleApiClient, final DriveId driveId)
		{

			DriveFile driveFile = com.google.android.gms.drive.Drive.DriveApi
					.getFile(googleApiClient, driveId);
			return driveFile;
		}

		public static Contents writeTo(final GoogleApiClient googleApiClient,
				DriveFile driveFile, byte[] buff)
		{

			Contents contents = null;

			ContentsResult rslt = driveFile.openContents(googleApiClient,
					DriveFile.MODE_WRITE_ONLY, null).await();

			contents = rslt.getContents();
			OutputStream os = contents.getOutputStream();
			try
			{
				os.write(buff);
				os.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			return contents ;

		}
	}

}
