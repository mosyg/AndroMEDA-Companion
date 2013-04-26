package edu.uiuc.mosyg.logcollectorviewer;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;

public class PermissionEvent {
    public String permission;
    public String message;
    public int uid;
    public boolean selfToo;
    public int resultOfCheck;
    public long time;
    public String data;
    public String[] packagenames;

    public PermissionEvent(String permission, String message, int uid,
            boolean selfToo, int resultOfCheck, long time,
            String[] packagenames, String data) {
        super();
        this.permission = permission;
        this.message = message;
        this.uid = uid;
        this.selfToo = selfToo;
        this.resultOfCheck = resultOfCheck;
        this.time = time;
        this.packagenames = packagenames;
                this.data = data;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject out = new JSONObject();
        out.put("permission", permission);
        out.put("message", message);
        out.put("uid", uid);
        out.put("selfToo", selfToo);
        out.put("resultOfCheck", resultOfCheck);
        out.put("time", time);
        if (permission.equals("internet.http.client"))
            out.put("data", data == null ? "" : Base64.encodeToString(data.getBytes(), Base64.NO_WRAP));
        else
        	out.put("data", data == null ? "" : data);
        if (packagenames == null || packagenames.length == 0) {
            out.put("package-names", new JSONArray());
        } else {
            out.put("package-names", new JSONArray(Arrays.asList(packagenames)));
        }
        return out;
    }
    
    public static PermissionEvent fromJSON(String jsonString) throws JSONException {
        JSONObject obj = new JSONObject(jsonString);
        PermissionEvent evt = new PermissionEvent(null, null, 0, false, 0, 0, null, null);
        evt.permission = obj.getString("permission");
        try {
            evt.message = obj.getString("message");
        } catch (Exception e) {
            evt.message= "";
        }
        evt.uid = obj.getInt("uid");
        evt.selfToo = obj.getBoolean("selfToo");
        evt.resultOfCheck = obj.getInt("resultOfCheck");
        evt.time = obj.getLong("time");
        try {
            evt.data = obj.getString("data");
        } catch (Exception e) {
            evt.data = "";
        }

        try {
            ArrayList<String>packages = new ArrayList<String>();
            JSONArray jpackages = obj.getJSONArray("package-names");
            for (int i=0; i<jpackages.length(); i++) {
                packages.add(jpackages.getString(i));
            }
            evt.packagenames = packages.toArray(new String[0]);
        } catch (Exception e) {
            evt.packagenames = new String[0];
        }
        return evt;
    }
}
