package com.wireme.samba;

import java.io.ByteArrayInputStream;

import com.wireme.R;
import com.wireme.util.FileUtils;


import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class UploaderService extends Service implements FinishedTaskListener {
	/**
	 * Username that should be the intent
	 */
	public final static String USERNAME_KEY = "USERNAME";
	/**
	 * Password that should be the intent
	 */
	public final static String PASSWORD_KEY = "PASSWORD";
	/**
	 * ip name key
	 */
	public final static String IP_KEY = "IP";
	int taskRunning = 0;

	@Override
	public int onStartCommand(Intent i, int flags, int startId) {
		Log.i("UploaderService", "Received start id " + startId + ": " + i);
		Intent intent = i;
		if (intent == null) {
			if (taskRunning == 0) {
				stopSelf();
				return START_NOT_STICKY;
			} else {
				return START_STICKY;
			}
		}
		
		//TODO: make it a background service
		Bundle extras = intent.getExtras();
		final String username = intent.getStringExtra(USERNAME_KEY);
		final String password = intent.getStringExtra(PASSWORD_KEY);
		final String ip = intent.getStringExtra(IP_KEY);
		try {
			final InputStreamManaged is;
			final Uri uri;
			if (intent.getData() == null)
				uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
			else
				uri = intent.getData();
			CharSequence filename = null;
			if (uri == null) {
				filename = extras.getCharSequence(Intent.EXTRA_TEXT);
				if (filename == null) {
					Toast.makeText(this,
							getString(R.string.notification_unsupported),
							Toast.LENGTH_SHORT).show();
					if (taskRunning == 0) {
						stopSelf();
						return START_NOT_STICKY;
					} else {
						return START_STICKY;
					}
				}
				is = new InputStreamManaged(new ByteArrayInputStream(filename
						.toString().getBytes("UTF-8")));
				is.setLength(filename.length());
				filename = extras.getCharSequence(Intent.EXTRA_SUBJECT);
				if (filename == null) {
					filename = getString(R.string.app_name)
							+ System.currentTimeMillis();
				}
				filename = filename + ".txt";
			} else {
				ContentResolver cr = getContentResolver();
				is = new InputStreamManaged(cr.openInputStream(uri));
				final String path = FileUtils.getRealPathFromUri(this, uri);
				if (path != null) {
					is.setLength(new java.io.File(path).length());
					int offset = path.lastIndexOf('/');
					filename = path.substring(offset + 1);
				} else {
					is.setLength(0);
					filename = uri.toString();
					int offset = filename.toString().lastIndexOf('/');
					filename = filename.toString().substring(offset + 1);
				}
			}
			taskRunning++;
			final UploaderTask task = new UploaderTask(this, this, is,
					filename.toString());
			task.execute(username, password,ip);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}
	
	
	

	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class LoggerServiceBinder extends Binder {
		public UploaderService getService() {
			return UploaderService.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return new LoggerServiceBinder();
	}

	@Override
	public void finishedTask() {
		taskRunning--;
		if (taskRunning == 0)
			stopSelf();
	};
}