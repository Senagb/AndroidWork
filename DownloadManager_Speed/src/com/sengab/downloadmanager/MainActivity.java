package com.sengab.downloadmanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.ResultReceiver;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	private EditText url, name;
	private Button start;
	ProgressDialog mProgressDialog;
	private int counter = 0;
	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			String path = (String) message.obj;
			if (message.arg1 == RESULT_OK && path != null) {
				Toast.makeText(MainActivity.this, "Downloaded" + path,
						Toast.LENGTH_LONG).show();

			} else {
				Toast.makeText(MainActivity.this, "Download failed.",
						Toast.LENGTH_LONG).show();
			}

		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		url = (EditText) findViewById(R.id.editText1);
		name = (EditText) findViewById(R.id.editText2);
		start = (Button) findViewById(R.id.button1);
		start.setOnClickListener(this);
	}

	public void onClick(View view) {
		Intent intent = new Intent(MainActivity.this, DownloadService.class);
		// Create a new Messenger for the communication back
		String url_text = url.getText().toString().trim();
		String name_text = name.getText().toString().trim();
		try {
			MimeTypeMap myMime = MimeTypeMap.getSingleton();
			String mimeType = myMime.getMimeTypeFromExtension(fileExt(url_text)
					.substring(1));
			String type = mimeType.substring(0, mimeType.lastIndexOf("/"));

			if (type.equalsIgnoreCase("image") || type.equalsIgnoreCase("text")) {
				mProgressDialog = new ProgressDialog(MainActivity.this);
				mProgressDialog.setMessage("Downloading .......");
				mProgressDialog.setIndeterminate(false);
				mProgressDialog.setProgress(0);
				mProgressDialog.setMax(100);
				mProgressDialog
						.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

				Messenger messenger = new Messenger(handler);
				intent.putExtra("MESSENGER", messenger);
				intent.setData(Uri.parse(url_text));
				intent.putExtra("urlpath", url_text);
				intent.putExtra("filename", name_text);
				intent.putExtra("id", "" + counter);
				counter++;
				intent.putExtra("receiver", new DownloadReceiver(new Handler()));
				startService(intent);
			} else {
				Toast.makeText(MainActivity.this,
						"Don't support this type download image or txt only",
						Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			Toast.makeText(MainActivity.this,
					"Don't support this type download image or txt only",
					Toast.LENGTH_LONG).show();
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

	private class DownloadReceiver extends ResultReceiver {
		public DownloadReceiver(Handler handler) {
			super(handler);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			super.onReceiveResult(resultCode, resultData);
			if (resultCode == DownloadService.UPDATE_PROGRESS) {
				if (!mProgressDialog.isShowing())
					mProgressDialog.show();
				int progress = resultData.getInt("progress");
				mProgressDialog.setProgress(progress);
				if (progress == 100) {
					mProgressDialog.dismiss();
				}
			}
		}
	}
}