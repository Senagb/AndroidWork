package com.sengab.downloadmanager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.Messenger;
import android.os.ResultReceiver;
import android.util.Log;
import android.webkit.MimeTypeMap;

public class DownloadService extends IntentService {

	private int result = Activity.RESULT_CANCELED;
	public static final int UPDATE_PROGRESS = 2000;

	public DownloadService() {
		super("DownloadService");
	}

	@SuppressLint("NewApi")
	protected void onHandleIntent(Intent intent) {
		Uri data = intent.getData();
		String urlPath = intent.getStringExtra("urlpath");
		String fileName = data.getLastPathSegment();
		int id = Integer.parseInt(intent.getStringExtra("id"));
		ResultReceiver receiver = (ResultReceiver) intent
				.getParcelableExtra("receiver");
		File output = new File(Environment.getExternalStorageDirectory(),
				fileName);
		if (output.exists()) {
			output.delete();
		}

		InputStream stream = null;
		FileOutputStream fos = null;
		try {
			URL url = new URL(urlPath);
			URLConnection connection = url.openConnection();
			connection.connect();
			stream = new BufferedInputStream(url.openStream());
			int fileLength = connection.getContentLength();
			if (fileLength == -1)
				fileLength = 2048;

			if (!output.exists()) {
				output.createNewFile();
			}
			fos = new FileOutputStream(output);

			byte data1[] = new byte[1024];
			long total = 0;
			int count;
			while ((count = stream.read(data1)) != -1) {
				total += count;
				Bundle resultData = new Bundle();
				resultData.putInt("progress",
						(int) ((total * 100) / (fileLength)));
				receiver.send(UPDATE_PROGRESS, resultData);
				fos.write(data1, 0, count);
			}

			fos.flush();
			fos.close();
			stream.close();

			// while ((next = reader.read()) != -1) {
			// fos.write(next);
			// }
			// Sucessful finished
			result = Activity.RESULT_OK;
			Bundle resultData = new Bundle();
			resultData.putInt("progress", 100);
			receiver.send(UPDATE_PROGRESS, resultData);
			// Prepare intent which is triggered if the
			// notification is selected
			Intent notificationintent = new Intent();
			// intent.putExtra("filelocation", path);
			notificationintent.setAction(Intent.ACTION_VIEW);
			String filePath = "file://" + output.getAbsolutePath();
			MimeTypeMap myMime = MimeTypeMap.getSingleton();
			String mimeType = myMime.getMimeTypeFromExtension(fileExt(
					output.getAbsolutePath()).substring(1));
			notificationintent.setDataAndType(Uri.parse(filePath), mimeType);
			PendingIntent pIntent = PendingIntent.getActivity(this, 0,
					notificationintent, 0);
			// Build notification
			// Actions are just fake
			Notification noti = new Notification.Builder(this)
					.setContentTitle(
							"file " + fileName + " has been downloaded")
					.setContentText("Turbo download")
					.setSmallIcon(android.R.drawable.btn_star)
					.setContentIntent(pIntent).build();
			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			// Hide the notification after its selected
			noti.flags |= Notification.FLAG_AUTO_CANCEL;

			notificationManager.notify(id, noti);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		Bundle extras = intent.getExtras();
		if (extras != null) {
			Messenger messenger = (Messenger) extras.get("MESSENGER");
			Message msg = Message.obtain();
			msg.arg1 = result;
			msg.obj = output.getAbsolutePath();
			try {
				messenger.send(msg);
			} catch (android.os.RemoteException e1) {
				Log.w(getClass().getName(), "Exception sending message", e1);
			}

		}

	}

	private String fileExt(String url) {
		if (url.indexOf("?") > -1) {
			url = url.substring(0, url.indexOf("?"));
		}
		if (url.lastIndexOf(".") == -1) {
			return null;
		} else {
			String ext = url.substring(url.lastIndexOf("."));
			if (ext.indexOf("%") > -1) {
				ext = ext.substring(0, ext.indexOf("%"));
			}
			if (ext.indexOf("/") > -1) {
				ext = ext.substring(0, ext.indexOf("/"));
			}
			return ext.toLowerCase();

		}
	}

}