
package com.innovationWaves.cryptocloud.controller.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.innovationWaves.cryptocloud.model.FileModel;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

public class Utilities
{

	public final static long MILLISECONDS_PER_DAY = 1000L * 60 * 60 * 24;

	private static String DEBUG = "DEBUG";

	// --------------------------------------------------

	public static void log(String msg)
	{

		Log.i(DEBUG, msg);
	}

	public static void showToast(Context context, String msg)
	{

		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public static boolean isStringEmpty(String input)
	{

		if (input != null && !input.isEmpty())
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	public static String removeWhiteSpaces(String input)
	{

		return input.replaceAll("\\s", "");
	}

	public static List<String> spiltStringByComma(String string)
	{

		List<String> stringList = null;

		if (string != null && !string.isEmpty())
		{
			String[] fields = string.split(",");
			stringList = Arrays.asList(fields);
		}
		return stringList;
	}

	public static String getExtension(File file)
	{

		String extension = null;
		String fileName = file.getName();
		try
		{
			if (fileName.contains("\\."))
			{
				String filenameArray[] = fileName.split("\\.");
				extension = filenameArray[filenameArray.length - 1];
			}
			else
			{
				extension = "dir";
			}
		}
		catch (Exception e)
		{
			extension = "err";
		}
		return extension;
	}

	public static String getMimeType(String url)
	{

		String type = null;
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		if (extension != null)
		{
			MimeTypeMap mime = MimeTypeMap.getSingleton();
			type = mime.getMimeTypeFromExtension(extension);
		}
		return type;
	}

	private static int getImageResource(Context context, String name)
	{

		String[] namestr = name.split("\\.");
		int imageResource = context.getResources().getIdentifier(namestr[0],
				"drawable", context.getPackageName());

		return imageResource;
	}

	// -------------------------------------

	public static void DeleteRecursive(File fileOrDirectory, String ext)
	{

		if (fileOrDirectory.isDirectory())
		{
			for (File child : fileOrDirectory.listFiles())
			{
				DeleteRecursive(child, ext);
			}

		}

		if (ext == null)
		{
			fileOrDirectory.delete();
		}
		else
		{
			String path = fileOrDirectory.getAbsolutePath();
			if (path.endsWith(ext))
			{
				fileOrDirectory.delete();
			}
		}

	}

	public static void showMessage(Context context, String title,
			String message, boolean isQuestion,
			final DialogInterfaceListener dialogListenerInterface)
	{

		AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
		builder1.setMessage(message);
		builder1.setCancelable(true);

		builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener()
		{

			public void onClick(DialogInterface dialog, int id)
			{

				dialog.cancel();
				if (dialogListenerInterface != null)
				{
					dialogListenerInterface.onYesPressed();
				}
			}
		});

		if (isQuestion)
		{
			builder1.setNegativeButton("No",
					new DialogInterface.OnClickListener()
					{

						public void onClick(DialogInterface dialog, int id)
						{

							dialog.cancel();
							if (dialogListenerInterface != null)
							{
								dialogListenerInterface.onNoPressed();
							}
						}
					});
		}

		AlertDialog alert11 = builder1.create();
		alert11.show();
	}

	public interface DialogInterfaceListener
	{

		public void onYesPressed();

		public void onNoPressed();

	}

	// --------------------------------------------------

	public static String getRealPathFromURI(Context context, Uri contentUri)
	{

		String path = null;
		String[] proj = { MediaStore.Images.Media.DATA };
		CursorLoader loader = new CursorLoader(context, contentUri, proj, null,
				null, null);
		Cursor cursor = loader.loadInBackground();
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		if (cursor != null)
		{
			cursor.moveToFirst();
			path = cursor.getString(column_index);
		}
		return path;
	}

	public static byte[] getBytesFromInputStream(InputStream is)
	{

		try
		{
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			byte[] buffer = new byte[0xFFFF];

			for (int len; (len = is.read(buffer)) != -1;)
			{
				os.write(buffer, 0, len);
			}
			os.flush();

			return os.toByteArray();
		}
		catch (IOException e)
		{
			return null;
		}
	}

	public static byte[] readFileFromOffsetToOffset(String path,
			int fromOffset, int toOffset)
	{

		ByteBuffer buf = null;
		try
		{
			File file = new File(path);
			int size = (int) file.length();

			FileInputStream fs = new FileInputStream(file);
			FileChannel inChannel = fs.getChannel();
			if (toOffset == 0)
			{
				buf = ByteBuffer.allocate(size - fromOffset);
			}
			else
			{
				buf = ByteBuffer.allocate(toOffset - fromOffset);
			}

			inChannel.position(fromOffset);
			int bytesRead = inChannel.read(buf);
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}

		return buf.array();
	}

	public static void writeAt(byte[] buffer, String outPath1, int startFrom)
	{

		try
		{
			RandomAccessFile raf = new RandomAccessFile(outPath1, "rw");
			if (startFrom > 0)
			{
				raf.seek(startFrom);
			}
			raf.write(buffer);
			raf.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static String[] pathToNameAndExt(String path)
	{

		File tempFile = new File(path);
		String fileName = tempFile.getName();
		String[] fileNameAndExt = fileName.split("\\.");
		System.out.println("fileNameAndExt" + fileNameAndExt.length);
		return fileNameAndExt;
	}

	// 3 convert milliseconds to date
	public static String convertEpochmilliSecondsToFormatedDate(
			long epochMilliSeconds)
	{

		Date date = new Date(epochMilliSeconds);
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		String StringDate = sf.format(date);
		return StringDate;
	}

	public  static void copyFileStream(Context context , Uri uri)
	{

		try
		{
			// Read
			InputStream is = context.getContentResolver().openInputStream(uri);
			byte[] fileContent = Utilities.getBytesFromInputStream(is);

			// Type
			String type = context.getContentResolver().getType(uri);
			String[] array = type.split("/");
			String filePath = FileModel.getDecryptionFolder(context) + "/file"
					+ "." + array[1];

			// Write
			Utilities.writeAt(fileContent, filePath, 0);

			// Encrypt
			//encryptFile(filePath);

		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	// --------------------------------------------------

	public static String getAccount(Context context)
	{

		String acc = null;
		AccountManager accountManager = AccountManager.get(context);
		Account[] accounts = accountManager.getAccounts();

		for (Account account : accounts)
		{
			acc = account.name;
			break ;
		}

		return acc;
	}
}
