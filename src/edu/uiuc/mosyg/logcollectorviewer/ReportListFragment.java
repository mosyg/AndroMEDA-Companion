package edu.uiuc.mosyg.logcollectorviewer;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ReportListFragment extends ListFragment {
	public final static String ARG_PACKAGENAME = "packagename";
	public final static String ARG_APPNAME = "appname";
	String packagename;
	String appname;
	List<UserReport> events;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		packagename = getArguments().getString(ARG_PACKAGENAME);
		appname = getArguments().getString(ARG_APPNAME);
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

		setEmptyText("No Reports for "+appname);
		new ReportTask().execute();

	}
	
	
	public class ReportTask extends AsyncTask<Void, Void, List<UserReport>> {

		@Override
		protected List<UserReport> doInBackground(Void... params) {
			try {
				//return LogManager.grabAppEvents(ReportListFragment.this.getActivity(), packagename);
				URL url = new URL("http://srgnhl.cs.illinois.edu/hb/appreports.php?packagename="+packagename);
				Log.d("InstallReceiver", "Asking "+url+" about "+packagename);
				JSONArray reply = new JSONArray(convertStreamToString(url.openConnection().getInputStream()));
				Log.d("InstallReceiver", "Just asked "+url+" about "+packagename+", result was :"+reply);
				List<UserReport> list = new ArrayList<UserReport>();
				for (int i=0; i<reply.length(); i++) {
					list.add(UserReport.fromJSON(reply.getJSONObject(i)));
				}
				
				return list;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(List<UserReport> result) {
			super.onPostExecute(result);
			setNewReportList(result);
		}
	}
	
	public static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	
	public void setNewReportList(List<UserReport> events) {
		this.events = events;
		if (events == null || events.size() == 0) {
			//setListShown(false);
			setListAdapter(null);
		} else {
			//setListShown(true);
			setListAdapter(new EventAdapter(getActivity(), events));
		}
	}
	
	public class EventAdapter extends ArrayAdapter<UserReport> {
		public EventAdapter(Context context, List<UserReport> objects) {
			super(context, R.layout.eventlist_item, R.id.itemtext, objects);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			UserReport event = getItem(position);
			ViewGroup v = (ViewGroup)super.getView(position, convertView, parent);
			TextView text = (TextView)v.findViewById(R.id.itemtext);
			text.setText(event.message+ ", "+event.severity);
			return v;
		}
	}

}
