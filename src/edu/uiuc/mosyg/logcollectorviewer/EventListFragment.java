package edu.uiuc.mosyg.logcollectorviewer;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class EventListFragment extends ListFragment {
	public final static String ARG_PACKAGENAME = "packagename";
	public final static String ARG_APPNAME = "appname";
	String packagename;
	String appname;
	List<PermissionEvent> events;
	MenuItem reportitem;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		packagename = getArguments().getString(ARG_PACKAGENAME);
		appname = getArguments().getString(ARG_APPNAME);
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		//return inflater.inflate(R.layout.fragment_eventlist, container, false);
		return v;
	}
	
	
	@Override
	public void onStart() {
		super.onStart();

		setEmptyText("Your device does not have any logs for "+appname);
		new EventTask().execute();

	}
	
	
	public class EventTask extends AsyncTask<Void, Void, List<PermissionEvent>> {

		@Override
		protected List<PermissionEvent> doInBackground(Void... params) {
			try {
				List<PermissionEvent> events =  LogManager.grabAppEvents(EventListFragment.this.getActivity(), packagename);
				Collections.reverse(events);
				return events;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(List<PermissionEvent> result) {
			super.onPostExecute(result);
			setNewEventList(result);
		}
	}
	
	public void setNewEventList(List<PermissionEvent> events) {
		this.events = events;
		if (events == null || events.size() == 0) {
			//setListShown(false);
			setListAdapter(null);
			if (reportitem != null) reportitem.setEnabled(false);
		} else {
			//setListShown(true);
			setListAdapter(new EventAdapter(getActivity(), events));
			if (reportitem != null) reportitem.setEnabled(true);
		}

	}
	
	public class EventAdapter extends ArrayAdapter<PermissionEvent> {
		public EventAdapter(Context context, List<PermissionEvent> objects) {
			super(context, R.layout.eventlist_item, R.id.itemtext, objects);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			PermissionEvent event = getItem(position);
			ViewGroup v = (ViewGroup)super.getView(position, convertView, parent);
			TextView text = (TextView)v.findViewById(R.id.itemtext);
			TextView perm = (TextView)v.findViewById(R.id.permissiontext);
			if (permissionNameToTitle.containsKey(event.permission)) {
				PermissionInfo info = permissionNameToTitle.get(event.permission);
				perm.setText(info.description);
				v.setBackgroundColor(severityToColor.get(info.severity));
			} else {
				perm.setText(event.permission);
				v.setBackgroundColor(severityToColor.get(-1));
			}
			text.setText(event.message+ ", "+event.data);
			return v;
		}
	}
	
	
	public Map<String,PermissionInfo> permissionNameToTitle = new HashMap<String,PermissionInfo>() {{
		put("android.permission.READ_PHONE_STATE",					new PermissionInfo("Read Phone Info", 1));
		put("android.permission.ACCESS_NETWORK_STATE",				new PermissionInfo("Read Network State", 1));
		put("android.permission.GET_ACCOUNTS", 						new PermissionInfo("Read Accounts", 2));
		put("android.permission.READ_CONTACTS", 					new PermissionInfo("Read Contacts", 3));
		put("android.permission.READ_CALENDAR", 					new PermissionInfo("Read Calendar", 3));
		put("android.permission.READ_SMS", 							new PermissionInfo("Read SMS", 3));
		put("android.permission.WRITE_CONTACTS", 					new PermissionInfo("Write Contacts", 4));
		put("android.permission.WRITE_CALENDAR", 					new PermissionInfo("Write Calendar", 4));
		put("android.permission.WRITE_SMS", 						new PermissionInfo("Write SMS", 4));
		put("android.permission.SEND_SMS", 							new PermissionInfo("Send SMS", 3));
		put("android.permission.RECORD_AUDIO", 						new PermissionInfo("Record Audio", 3));
		put("android.permission.CAMERA", 							new PermissionInfo("Use Camera", 3));
		put("com.android.browser.permission.READ_HISTORY_BOOKMARKS",new PermissionInfo("Read Browser", 3));
		put("android.permission.ACCESS_FINE_LOCATION", 				new PermissionInfo("GPS Location", 3));
		put("android.permission.ACCESS_COARSE_LOCATION", 			new PermissionInfo("Network Location", 3));
		
		
		put("android.activity.ACTION", 								new PermissionInfo("App Action", -1));
		put("internet.http.client", 								new PermissionInfo("Internet Access", 2));
		put("internet.http.connection", 							new PermissionInfo("Internet Access", 2));
	}};
	
	public Map<Integer, Integer> severityToColor = new HashMap<Integer,Integer>() {{
		put(-1, Color.HSVToColor(new float[] {0, 0f, 1f}));
		put(0, Color.HSVToColor(new float[] {270, 0.1f, 0.95f}));
		put(1, Color.HSVToColor(new float[] {180, 0.2f, 0.90f}));
		put(2, Color.HSVToColor(new float[] {90, 0.3f, 0.85f}));
		put(3, Color.HSVToColor(new float[] {0, 0.3f, 0.80f}));
		put(4, Color.HSVToColor(new float[] {0, 0.6f, 0.70f}));
		
	}};
	
	public class PermissionInfo {
		String description;
		int severity;
		public PermissionInfo(String description, int severity) {
			this.description = description;
			this.severity = severity;
		}
	}
	
	
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.eventlist_menu, menu);
		reportitem = menu.findItem(R.id.action_report);
		if (events != null && events.size() == 0) {
			reportitem.setEnabled(false);
		} else {
			reportitem.setEnabled(true);
		}
	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_report:
	            report();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void report() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Report");

		// Set up the input
		final EditText input = new EditText(getActivity());
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		input.setHint("Describe the behavior");
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        upload(events, packagename, input.getText().toString());
		    }
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.cancel();
		    }
		});

		builder.show();
	}
	
	
	public void upload(final List<PermissionEvent> events, final String packagename, final String message) {
		new Thread() {
			@Override
			public void run() {
				try {
					uploadAction(events, packagename, message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public void uploadAction(List<PermissionEvent> events, String packagename, String message) throws IOException, Exception {
		UserReport r = new UserReport();
		r.packagename = packagename;
		r.appname = "";
		r.flag = "malicious";
		r.severity = 2;
		r.timestamp = System.currentTimeMillis();
		r.logs = events;
		
		Uploader u = new Uploader();
		u.upload("http://srgnhl.cs.illinois.edu/hb/parselogs.php", r.toJSON(), getActivity());
	}


}
