package edu.uiuc.mosyg.logcollectorviewer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.util.Log;

public class Uploader {

	public String upload(String host, JSONObject json, Context c) throws IOException {
		HttpClient httpclient = AndroidHttpClient.newInstance("asdf");
	    HttpPost httppost = new HttpPost(host);

	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("asdf", "asdfing"));
	        nameValuePairs.add(new BasicNameValuePair("report", json.toString()));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        Log.d("UPloader", "json: "+json.toString());
//	        File out = new File(c.getCacheDir(),"example.json");
//	        FileOutputStream fos = c.openFileOutput("example.json", Context.MODE_APPEND);
//	        Log.d("Uploader", "File is at: "+out.getAbsolutePath());
//	        fos.write(json.toString(4).getBytes());
//	        fos.close();
	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        String retstr = inputStreamToString(response.getEntity().getContent()).toString();
	       
	        Log.d("Uploader", "Uploaded: "+retstr);
	    } catch (Exception e) {
	        e.printStackTrace();
	        Log.d("Uploader", "failed: ");
	    }
	    return "";
	    
	    
	}
	
	// Fast Implementation
	private StringBuilder inputStreamToString(InputStream is) throws IOException {
	    String line = "";
	    StringBuilder total = new StringBuilder();
	    
	    // Wrap a BufferedReader around the InputStream
	    BufferedReader rd = new BufferedReader(new InputStreamReader(is));

	    // Read response until the end
	    while ((line = rd.readLine()) != null) { 
	        total.append(line); 
	        total.append("\n"); 
	    }
	    
	    // Return full string
	    return total;
	}
	
	public String upload2(String host, JSONObject json) throws IOException {
		URL url = new URL(host);
		HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
		urlConn.setDoInput(true);
		urlConn.setDoOutput(true);
		urlConn.setUseCaches(false);
		urlConn.setRequestMethod("POST");
		urlConn.setRequestProperty("Content-Type", "application/json");
		urlConn.connect();
		DataOutputStream printout = new DataOutputStream(urlConn.getOutputStream());
		printout.writeUTF("report=");
		printout.writeUTF(URLEncoder.encode("Hello world", "UTF-8"));
		printout.flush();
		printout.close();
		StringBuilder sb = new StringBuilder();
		int HttpResult = urlConn.getResponseCode();  
        if (HttpResult == HttpURLConnection.HTTP_OK){  
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"utf-8"));  
            String line = null;  
            while ((line = br.readLine()) != null) {  
                sb.append(line + "\n");  
            }  
            br.close();  

            Log.d("Uploader", "Success: \n"+sb.toString());  
            return sb.toString();

        }else{  
           Log.d("Uploader", "Error: "+urlConn.getResponseMessage());  
           return urlConn.getResponseMessage();
        }  


	}
	
	
}
