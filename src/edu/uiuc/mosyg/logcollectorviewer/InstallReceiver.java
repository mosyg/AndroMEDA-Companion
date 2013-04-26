package edu.uiuc.mosyg.logcollectorviewer;

import java.io.IOException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.AndroidException;
import android.util.Log;
import android.widget.Toast;

public class InstallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		final String packagename = arg1.getDataString().replace("package:","");
		runCheckPackage(packagename, arg0);
	}
	
	public void runCheckPackage(final String packagename, final Context context) {
		new Thread() {
			int retrycount = 0;
			public void run() {
				try {
					checkPackage(packagename, context);
				} catch (IOException e) {
					e.printStackTrace();
					//most likely a network error.
					if (retrycount < 10) {
						try { Thread.sleep(retrycount * 500L); } catch (InterruptedException e1) { e1.printStackTrace(); }
						retrycount += 1;
						run();
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.d("InstallReceiver", "Definitely an error talking to the server.");
				}
			}
		}.start();
	}
	
	public void checkPackage(String packagename, Context context) throws IOException, JSONException, AndroidException {
		URL url = new URL("http://srgnhl.cs.illinois.edu/hb/isthisappsafe.php?packagename="+packagename);
		Log.d("InstallReceiver", "Asking "+url+" about "+packagename);
		JSONObject reply = new JSONObject(convertStreamToString(url.openConnection().getInputStream()));
		Log.d("InstallReceiver", "Just asked "+url+" about "+packagename+", result was :"+reply);
		int level = reply.getInt("severity");
		if (level == 2) 
			alertSuspicious(packagename, reply, context);
		if (level == 3)
			alertMalware(packagename, reply, context);
	}
	
	public void alertMalware(String packagename, JSONObject report, Context context) throws JSONException, AndroidException {
		Log.d("InstallReceiver", "Malware!");
		
		Intent action = new Intent(Intent.ACTION_VIEW);
		action.setData(Uri.parse("permissions://"+packagename));
		action.putExtra(ViewerActivity.EXTRA_SEVERITY, ViewerActivity.SEVERITY_DANGEROUS);
		action.putExtra(ViewerActivity.EXTRA_FROMINSTALL, true);
		PendingIntent pi = PendingIntent.getActivity(context, (int)System.currentTimeMillis(), action, 0);
		
		int count = report.getInt("num_reports");
		String appname = appNameFromPackage(context, packagename);
		 Notification noti = new Notification.Builder(context)
         .setContentTitle("Malicious App Detected")
         .setContentText(appname+" has been reported as suspicious "+count+" times")
         .setStyle(new Notification.BigTextStyle().bigText(appname+" ["+packagename+"] has been reported as malicious "+count+" times"))
         .setSmallIcon(R.drawable.ic_stat_danger)
         .setContentIntent(pi)
         .setTicker("Malicious App Detected")
         .build();
		 NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		 nm.notify(ViewerActivity.NOTIFICATION_ID, noti);
		
		//Toast.makeText(context, "Malware detected! Check notifications", Toast.LENGTH_LONG).show();

		
	}
	
	public void alertSuspicious(String packagename, JSONObject report, Context context) throws JSONException, AndroidException {
		Log.d("InstallReceiver", "Suspicious!");
		
		Intent action = new Intent(Intent.ACTION_VIEW);
		action.setData(Uri.parse("permissions://"+packagename));
		action.putExtra(ViewerActivity.EXTRA_SEVERITY, ViewerActivity.SEVERITY_SUSPICIOUS);
		action.putExtra(ViewerActivity.EXTRA_FROMINSTALL, true);
		PendingIntent pi = PendingIntent.getActivity(context, (int)System.currentTimeMillis(), action, 0);
		
		int count = report.getInt("num_reports");
		String appname = appNameFromPackage(context, packagename);
		 Notification noti = new Notification.Builder(context)
         .setContentTitle("Suspicious App Detected")
         .setContentText(appname+" has been reported as suspicious "+count+" times")
         .setStyle(new Notification.BigTextStyle().bigText(appname+" ["+packagename+"] has been reported as suspicious "+count+" times"))
         .setSmallIcon(R.drawable.ic_stat_danger)
         .setContentIntent(pi)
         .setTicker("Suspicious App Detected")
         .build();
		 NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		 nm.notify(ViewerActivity.NOTIFICATION_ID, noti);
	}
	
	public static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	
	private String appNameFromPackage(Context context, String packagename) throws AndroidException {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo appinfo = pm.getApplicationInfo(packagename, 0);
        return pm.getApplicationLabel(appinfo).toString();
	}

}
