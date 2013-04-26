package edu.uiuc.mosyg.logcollectorviewer;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.content.Context;

public class LogManager {
	public static List<PermissionEvent> grabAppEvents(Context c, String packagename) {
		try {
			Object o = c.getSystemService("permissions");
			Object result = o.getClass().getMethod("getRawEvents", String.class).invoke(o, packagename);
			List<String> list = (List<String>)result;
			List<PermissionEvent> events = deJsonEvents(list);
			return events;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	public static List<PermissionEvent> deJsonEvents(List<String> strlist) throws JSONException {
		List<PermissionEvent> jsonevents = new ArrayList<PermissionEvent>();
		for (String s : strlist)  {
			jsonevents.add(PermissionEvent.fromJSON(s));
		}
		return jsonevents;
	}

}
