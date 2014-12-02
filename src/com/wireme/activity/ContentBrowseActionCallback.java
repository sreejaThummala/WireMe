package com.wireme.activity;

import java.util.logging.Logger;

import org.teleal.cling.model.action.ActionException;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.ErrorCode;
import org.teleal.cling.support.contentdirectory.callback.Browse;
import org.teleal.cling.support.model.BrowseFlag;
import org.teleal.cling.support.model.DIDLContent;
import org.teleal.cling.support.model.SortCriterion;
import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.Item;

import android.app.Activity;
import android.util.Log;
import android.widget.ArrayAdapter;

public class ContentBrowseActionCallback extends Browse {
	private static Logger log = Logger
			.getLogger(ContentBrowseActionCallback.class.getName());
	private Service service;
	private Container container;
	private ArrayAdapter<ContentItem> listAdapter;
	private Activity activity;
	public ContentBrowseActionCallback(Activity activity, Service service,
			Container container, ArrayAdapter<ContentItem> listadapter) {
		super(service, container.getId(), BrowseFlag.DIRECT_CHILDREN,"*", 0, null, new SortCriterion(true, "dc:title"));
		this.activity = activity;
		this.service = service;
		this.container = container;
		this.listAdapter = listadapter;
	}

	public ContentBrowseActionCallback(Activity activity, Service service,
			String containerId, ArrayAdapter<ContentItem> listadapter) {
		super(service, containerId, BrowseFlag.DIRECT_CHILDREN, "*", 0, null,
				new SortCriterion(true, "dc:title"));
		this.activity = activity;
		this.service = service;
		this.container = null;
		this.listAdapter = listadapter;
	}

	/**
	 * callback receive event
	 */
	public void received(final ActionInvocation actionInvocation,
			final DIDLContent didl) {
		log.fine("Received browse action DIDL descriptor, creating tree nodes");
		activity.runOnUiThread(new Runnable() {
			public void run() {
				try {
					listAdapter.clear();
					// Containers first
					for (Container childContainer : didl.getContainers()) {
						Log.d("ContentBrowserActionCallback", "childContainer="
								+ childContainer);
						log.fine("add child container "
								+ childContainer.getTitle());
						listAdapter
								.add(new ContentItem(childContainer, service));
					}
					// Now items
					for (Item childItem : didl.getItems()) {
						Log.d("ContentBrowserActionCallback", "childItem="
								+ childItem);
						log.fine("add child item" + childItem.getTitle());
						listAdapter
								.add( new ContentItem(childItem, service));
					}
				} catch (Exception ex) {
					log.fine("Creating DIDL tree nodes failed: " + ex);
					actionInvocation.setFailure(new ActionException(
							ErrorCode.ACTION_FAILED,
							"Can't create list childs: " + ex, ex));
					failure(actionInvocation, null);
				}
			}
		});
	}

	public void updateStatus(final Status status) {
	}

	@Override
	public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * failure event
	 */
	
}