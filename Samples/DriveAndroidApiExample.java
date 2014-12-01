
package com.innovationWaves.cryptocloud.test;

import java.io.IOException;
import java.io.OutputStream;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Contents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.innovationWaves.cryptocloud.R;
import com.innovationWaves.cryptocloud.controller.utils.GoogleDriveUtils;
import com.innovationWaves.cryptocloud.controller.utils.Picker;
import com.innovationWaves.cryptocloud.controller.utils.Utilities;

public class DriveAndroidApiExample extends DriveAndroidApiBaseActivity
{

	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_content);

	}

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data)
	{

		if (requestCode == 0 && resultCode == RESULT_OK)
		{
			String SelectedPath = Picker.getPickedPath(this, data);

			String mime = Utilities.getMimeType(SelectedPath);

			byte [] buffer = Utilities.readFileFromOffsetToOffset(SelectedPath,
					0, 0);
			
			DriveId driveId = DriveId.decodeFromString("DriveId:CAESABgOINbWh-GjUQ==");
			updateFile(getGoogleApiClient(), driveId,buffer);

			uploadFile(getGoogleApiClient(), mime, buffer);
		}
	}

	@Override
	public void onConnected(Bundle connectionHint)
	{

		super.onConnected(connectionHint);

		Intent intent = new Intent();
		intent.setType("*/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select File"), 0);

	}

	public void uploadFile(final GoogleApiClient client, final String mimeType,
			final byte[] buffer)
	{

		new AsyncTask<Void, Void, Void>()
		{

			@Override
			protected Void doInBackground(Void... params)
			{

				try
				{
					DriveFile file = GoogleDriveUtils.AndroidAPI.createFile(client, "file_api",
							mimeType, buffer);
					
					DriveId id = file.getDriveId();
					String driveIdEncoded = id.encodeToString();
					Utilities.log(driveIdEncoded);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}

				return null;
			}
		}.execute();
	}
	
	
	public void updateFile(final GoogleApiClient client, final DriveId driveId,
			final byte[] buffer )
	{

		new AsyncTask<Void, Void, Void>()
		{

			@Override
			protected Void doInBackground(Void... params)
			{

				DriveFile file = GoogleDriveUtils.AndroidAPI.getFileByDriveId(client, driveId);
				Contents contents = GoogleDriveUtils.AndroidAPI.writeTo(client, file, buffer);
				file.commitAndCloseContents(client, contents);

				return null;
			}
		}.execute();
	}

}
