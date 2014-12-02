package com.wireme.samba;

import com.ipaulpro.afilechooser.R;
import com.ipaulpro.afilechooser.utils.FileUtils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class SambaActivity extends Activity {
	private static final int REQUEST_CODE = 6384; // onActivityResult request
	// code

	private String userName;
	private String password;
	private String ip;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Use the GET_CONTENT intent from the utility class
		Intent target = FileUtils.createGetContentIntent();
		// Create the chooser Intent
		Intent intent = Intent.createChooser(target,
				getString(R.string.choose_file));
		try {
			startActivityForResult(intent, REQUEST_CODE);
		} catch (ActivityNotFoundException e) {
			// The reason for the existence of aFileChooser
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		
		case REQUEST_CODE:
			
			// If the file selection was successful
			if (resultCode == RESULT_OK) {
				Log.e("something", "the files is selected");
				if (data != null) {
					// Get the URI of the selected file
					final Uri uri = data.getData();
					try {
						
						//here two more things need to find the device it needs to upload
						//need to find the login it needs to get in
						//then get into get the upload file
						
						
						//things will go wrong here
						Intent intent = new Intent(this, DeviceChooserActivity.class);
						startActivity(intent);
								
						Intent i = new Intent(this, UploaderService.class);
						i.replaceExtras(data);
						i.setData(uri);
						i.putExtra(UploaderService.USERNAME_KEY, userName);
						i.putExtra(UploaderService.PASSWORD_KEY, password);
						i.putExtra(UploaderService.IP_KEY, ip);
						startService(i);
						
						finish();
					} catch (Exception e) {
						Log.e("FileSelectorTestActivity", "File select error",
								e);
					}
				}
			}
			
			
			break;
		}
		
		if(resultCode == 2){
			if(data!=null){
				Bundle bundle = data.getExtras();
				String username = bundle.getString("USER");
				String password = bundle.getString("PSWD");
				String ip = bundle.getString("IP");
				
				this.userName = username;
				this.password = password;
				this.ip = ip;
				return;
			} 
		}
		Log.e("something",requestCode+"");
		Log.e("soemthing",resultCode+"");
		Log.e("something", "the files is not  selected"+requestCode+RESULT_CANCELED);
		finish();
	}
}