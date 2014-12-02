package com.wireme.samba;

import java.util.ArrayList;

import com.wireme.R;
import com.wireme.activity.DeviceItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DeviceStylishAdapter extends ArrayAdapter<DeviceItem>{
	  

	    public DeviceStylishAdapter(Context context, ArrayList<DeviceItem> data) {
	        super(context, R.layout.rowlayout,data);
	        
	       
	    }
	    
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	// Get the data item for this position
	        DeviceItem item = getItem(position);    
	        // Check if an existing view is being reused, otherwise inflate the view
	        if (convertView == null) {
	           convertView = LayoutInflater.from(getContext()).inflate(R.layout.rowlayout, parent, false);
	        }
	        // Lookup view for data population
	        TextView textView = (TextView) convertView.findViewById(R.id.label);
	       
	        // Populate the data into the template view using the data object
	        textView.setText(item.toString());
	        // Return the completed view to render on screen
	        return convertView;

	    }

}
