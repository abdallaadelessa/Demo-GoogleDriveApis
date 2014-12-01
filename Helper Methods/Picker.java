
package com.innovationWaves.cryptocloud.controller.utils;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

public class Picker
{

	public static final String IMAGE_TYPE = "image/*";
	public static final String VIDEO_TYPE = "video/*";
	public static final String ALL_TYPE = "*/*";

	public static void pick(Fragment fragment, String type, String title,
			int requestCode)
	{

		Intent intent = new Intent();
		intent.setType(type);
		intent.setAction(Intent.ACTION_GET_CONTENT);
		fragment.startActivityForResult(Intent.createChooser(intent, title),
				requestCode);
	}

	public static String getPickedPath(Context context, Intent data)
	{
		Uri uri = data.getData();
		if (uri !=null &&"content".equalsIgnoreCase(uri.getScheme()))
		{
			String[] projection = { "_data" };
			Cursor cursor = null;

			try
			{
				cursor = context.getContentResolver().query(uri, projection,
						null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst())
				{
					return cursor.getString(column_index);
				}
			}
			catch (Exception e)
			{
				// Eat it
			}
		}
		else if (uri !=null && "file".equalsIgnoreCase(uri.getScheme()))
		{
			return uri.getPath();
		}

		return null;
	}

	public static void ViewMedia(Fragment fragment, String path, String type)
	{

		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" + path), type);
		fragment.startActivity(intent);
	}

	public static void ShareViaEmail(Activity activity, String type, String path)
			throws IOException
	{

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType(type);
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		Uri uri = Uri.parse("file://" + path);
		intent.putExtra(Intent.EXTRA_STREAM, uri);
		activity.startActivity(Intent.createChooser(intent, "Sending"));
	}

}
