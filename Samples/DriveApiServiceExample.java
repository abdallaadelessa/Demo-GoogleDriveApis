
package com.innovationWaves.cryptocloud.test;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import com.google.android.gms.drive.DriveId;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.innovationWaves.cryptocloud.R;
import com.innovationWaves.cryptocloud.controller.utils.GoogleDriveUtils;
import com.innovationWaves.cryptocloud.controller.utils.Picker;
import com.innovationWaves.cryptocloud.controller.utils.Utilities;

public class DriveApiServiceExample extends Activity
{

	String SelectedPath;
	private static final int COMPLETE_AUTHORIZATION_REQUEST_CODE = 22;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_content);

		Intent intent = new Intent();
		intent.setType("*/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select File"), 0);
	}

	public void makeRequestToGoogleDrive(final String path)
	{

		new AsyncTask<Void, Void, Void>()
		{

			@Override
			protected Void doInBackground(Void... params)
			{

				try
				{
					String mimeType = Utilities.getMimeType(path);
					/**
					 * Insert File
					 */
					
//					File file = GoogleDriveUtils.WebService.insertFile(
//							DriveApiWebServiceExample.this, "file1", "file1",
//							mimeType, path);
//					String id = file.getId();
//					Utilities.log(file.toString());

					/**
					 * Update File
					 */
					
					 String fileid = "0B2pBbt-nm80bMV9QSVVzRmR5QXM";
					 File updatedfile =
					 GoogleDriveUtils.WebService.updateFile(DriveApiServiceExample.this,fileid,
					 "file1","file1",mimeType,path);
					 String id = updatedfile.getId();
					 Utilities.log(updatedfile.toString());

				}
				catch (UserRecoverableAuthIOException e)
				{
					e.printStackTrace();
					startActivityForResult(e.getIntent(),
							COMPLETE_AUTHORIZATION_REQUEST_CODE);
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return null;
			}
		}.execute();

	}

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data)
	{

		switch (requestCode)
		{
			case COMPLETE_AUTHORIZATION_REQUEST_CODE:
			{
				if (resultCode == Activity.RESULT_OK)
				{
					// App is authorized, you can go back to sending the API
					// request
					makeRequestToGoogleDrive(SelectedPath);
				}
				else
				{
					// User denied access, show him the account chooser again
				}
				break;
			}
			case 0:
			{
				SelectedPath = Picker.getPickedPath(this, data);
				makeRequestToGoogleDrive(SelectedPath);
			}
		}
	}
	// --------------------------------------------------------------

}
