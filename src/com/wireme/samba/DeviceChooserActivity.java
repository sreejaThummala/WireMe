package com.wireme.samba;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.support.model.container.Container;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.wireme.R;
import com.wireme.activity.DeviceItem;
import com.wireme.activity.WireUpnpService;

public class DeviceChooserActivity extends Activity{

		private ListView deviceListView;
		private DeviceStylishAdapter deviceListAdapter;

		private AndroidUpnpService upnpService;

		private DeviceListRegistryListener deviceListRegistryListener;

		private final static String LOGTAG = "WireMe";

		private ServiceConnection serviceConnection = new ServiceConnection() {

			public void onServiceConnected(ComponentName className, IBinder service) {
				upnpService = (AndroidUpnpService) service;
				Log.v(LOGTAG, "Connected to UPnP Service");


				deviceListAdapter.clear();
				for (Device device : upnpService.getRegistry().getDevices()) {
					deviceListRegistryListener.deviceAdded(new DeviceItem(device));
				}

				// Getting ready for future device advertisements
				upnpService.getRegistry().addListener(deviceListRegistryListener);
				// Refresh device list
				upnpService.getControlPoint().search();
			}

			public void onServiceDisconnected(ComponentName className) {
				upnpService = null;
			}
		};


@SuppressWarnings({ "rawtypes", "unchecked" })
private void invokeFragmentManagerNoteStateNotSaved() {
    /**
     * For post-Honeycomb devices
     */
    if (Build.VERSION.SDK_INT < 11) {
        return;
    }
    try {
        Class cls = getClass();
        do {
            cls = cls.getSuperclass();
        } while (!"Activity".equals(cls.getSimpleName()));
        Field fragmentMgrField = cls.getDeclaredField("mFragments");
        fragmentMgrField.setAccessible(true);

        Object fragmentMgr = fragmentMgrField.get(this);
        cls = fragmentMgr.getClass();

        Method noteStateNotSavedMethod = cls.getDeclaredMethod("noteStateNotSaved", new Class[] {});
        noteStateNotSavedMethod.invoke(fragmentMgr, new Object[] {});
        Log.d("DLOutState", "Successful call for noteStateNotSaved!!!");
    } catch (Exception ex) {
        Log.e("DLOutState", "Exception on worka FM.noteStateNotSaved", ex);
    }
}
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			invokeFragmentManagerNoteStateNotSaved();

			setContentView(R.layout.activity_device_chooser);

			deviceListView = (ListView) findViewById(R.id.listdevice);

			deviceListAdapter = new DeviceStylishAdapter(this, new ArrayList<DeviceItem>());
			deviceListRegistryListener = new DeviceListRegistryListener();
			deviceListView.setAdapter(deviceListAdapter);
			deviceListView.setOnItemClickListener(deviceItemClickListener);

			
			
			
			getApplicationContext().bindService(
					new Intent(this, WireUpnpService.class), serviceConnection,
					Context.BIND_AUTO_CREATE);
		}

		@Override
		protected void onDestroy() {
			super.onDestroy();
			invokeFragmentManagerNoteStateNotSaved();
			if (upnpService != null) {
				upnpService.getRegistry()
						.removeListener(deviceListRegistryListener);
			}
			getApplicationContext().unbindService(serviceConnection);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
			case 0:
				searchNetwork();
				break;
			case 1:
				Logger logger = Logger.getLogger("org.teleal.cling");
				if (logger.getLevel().equals(Level.FINEST)) {
					Toast.makeText(this, R.string.disabling_debug_logging,
							Toast.LENGTH_SHORT).show();
					logger.setLevel(Level.INFO);
				} else {
					Toast.makeText(this, R.string.enabling_debug_logging,
							Toast.LENGTH_SHORT).show();
					logger.setLevel(Level.FINEST);
				}
				break;
			}
			return false;
		}

		protected void searchNetwork() {
			if (upnpService == null)
				return;
			Toast.makeText(this, R.string.searching_lan, Toast.LENGTH_SHORT).show();
			upnpService.getRegistry().removeAllRemoteDevices();
			upnpService.getControlPoint().search();
		}
		
		protected Intent getLoginIntent(){
			Intent intent = new Intent(this, LoginActivity.class);
			return intent;
		}

		OnItemClickListener deviceItemClickListener = new OnItemClickListener() {

			protected Container createRootContainer(Service service) {
				Container rootContainer = new Container();
				rootContainer.setId("0");
				rootContainer.setTitle("Content Directory on "
						+ service.getDevice().getDisplayString());
				return rootContainer;
			}

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				// TODO Auto-generated method stub
				Device device = deviceListAdapter.getItem(position).getDevice();
				//ask for login activity
				String ip = device.getDetails().getBaseURL().getHost();	
				goToLogin(ip);
			}

			private void goToLogin(String host) {
			    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
				intent.putExtra("IP", host);
				getApplication().startActivity(intent);
			}
		};


		public class DeviceListRegistryListener extends DefaultRegistryListener {

			/* Discovery performance optimization for very slow Android devices! */

			@Override
			public void remoteDeviceDiscoveryStarted(Registry registry,
					RemoteDevice device) {
			}

			@Override
			public void remoteDeviceDiscoveryFailed(Registry registry,
					final RemoteDevice device, final Exception ex) {
			}

			/*
			 * End of optimization, you can remove the whole block if your Android
			 * handset is fast (>= 600 Mhz)
			 */

			@Override
			public void remoteDeviceAdded(Registry registry, RemoteDevice device) {

				if (device.getType().getNamespace().equals("schemas-upnp-org")
						&& device.getType().getType().equals("MediaServer")) {
					final DeviceItem display = new DeviceItem(device, device
							.getDetails().getFriendlyName(),
							device.getDisplayString(), "(REMOTE) "
									+ device.getType().getDisplayString());
					deviceAdded(display);
				}
			}

			@Override
			public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
				final DeviceItem display = new DeviceItem(device,
						device.getDisplayString());
				deviceRemoved(display);
			}

			@Override
			public void localDeviceAdded(Registry registry, LocalDevice device) {
				final DeviceItem display = new DeviceItem(device, device
						.getDetails().getFriendlyName(), device.getDisplayString(),
						"(REMOTE) " + device.getType().getDisplayString());
				deviceAdded(display);
			}

			@Override
			public void localDeviceRemoved(Registry registry, LocalDevice device) {
				final DeviceItem display = new DeviceItem(device,
						device.getDisplayString());
				deviceRemoved(display);
			}

			public void deviceAdded(final DeviceItem di) {
				runOnUiThread(new Runnable() {
					public void run() {

						int position = deviceListAdapter.getPosition(di);
						if (position >= 0) {
							// Device already in the list, re-set new value at same
							// position
							deviceListAdapter.remove(di);
							deviceListAdapter.insert(di, position);
						} else {
							deviceListAdapter.add(di);
						}

						// Sort it?
						// listAdapter.sort(DISPLAY_COMPARATOR);
						// listAdapter.notifyDataSetChanged();
					}
				});
			}

			public void deviceRemoved(final DeviceItem di) {
				runOnUiThread(new Runnable() {
					public void run() {
						deviceListAdapter.remove(di);
					}
				});
			}
		}

		
		// FIXME: now only can get wifi address
		private InetAddress getLocalIpAddress() throws UnknownHostException {
			WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			int ipAddress = wifiInfo.getIpAddress();
			return InetAddress.getByName(String.format("%d.%d.%d.%d",
					(ipAddress & 0xff), (ipAddress >> 8 & 0xff),
					(ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff)));
		}
		
		@Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data)
	    {
	              // TODO Auto-generated method stub
	              super.onActivityResult(requestCode, resultCode, data);
	               // check if the request code is same as what is passed  here it is 2
	              if(requestCode==2)
	                      {
	                         // fetch the message String
	            	  		String username=data.getStringExtra("USER");
	            	  		String password = data.getStringExtra("PSWD");
	            	  		String ip = data.getStringExtra("IP");
	            	  		
	                        	  		
	            	  		Intent intent = new Intent();
	            	  		intent.putExtra("USER", username);
	            	  		intent.putExtra("PSWD", password);
	            	  		intent.putExtra("IP", ip);
	            	  		setResult(2, intent);
	            	  		this.finish();
	                      }
	           
	  }
	}


