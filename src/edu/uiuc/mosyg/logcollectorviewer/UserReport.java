package edu.uiuc.mosyg.logcollectorviewer;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserReport {
	String packagename;
	String message;
	long timestamp;
	String appname;
	String flag;
	int severity;
	List<PermissionEvent> logs;
	
	
	public JSONObject toJSON() throws JSONException {
		JSONObject out = new JSONObject();
		out.put("packagename", packagename);
		out.put("comment", message);
		out.put("appname", appname);
		out.put("flag", flag);
		out.put("severity", severity+"");
		out.put("timestamp", timestamp);
		
		ArrayList<JSONObject> logsout = new ArrayList<JSONObject>();
		for (PermissionEvent e: logs) logsout.add(e.toJSON());
		out.put("logs", new JSONArray(logsout));
		
		return out;
	}
	
	public static UserReport fromJSON(JSONObject json) throws JSONException {
		UserReport o = new UserReport();
		o.packagename = json.getString("packagename");
		o.message = json.getString("comment");
		o.appname = json.getString("appname");
		o.flag = json.getString("flag");
		o.severity = json.getInt("severity");
		o.timestamp = json.getLong("timestamp");
		return o;
	}
}
