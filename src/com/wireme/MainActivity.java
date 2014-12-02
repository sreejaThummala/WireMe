package com.wireme;

import com.wireme.activity.StreamActivity;
import com.wireme.samba.SambaActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	
	private Button addFilesButton;
	private Button streamFilesButton;
	

	protected  void startAddFiles(){
		Intent addFiles = new Intent(this, SambaActivity.class);
		addFiles.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(addFiles);
	}
	
	protected  void startStreamFiles(){
		Intent addFiles = new Intent(this, StreamActivity.class);
		addFiles.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(addFiles);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		addFilesButton = (Button) findViewById(R.id.button1);
		streamFilesButton = (Button) findViewById(R.id.button2);
		
		addFilesButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
			startAddFiles();
				
				
			}
		});
		
		
		streamFilesButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
			
				startStreamFiles();
					
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
