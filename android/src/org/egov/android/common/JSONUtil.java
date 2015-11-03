package org.egov.android.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.egov.android.view.activity.RegisterActivity;
import org.json.JSONArray;

import android.util.Log;

public class JSONUtil{
	
	public static JSONArray sort(JSONArray array, Comparator c){
	    List asList = new ArrayList(array.length());
	    for (int i=0; i<array.length(); i++){
	      asList.add(array.opt(i));
	    }
	    Collections.sort(asList, c);
	    JSONArray  res = new JSONArray();
	    for (Object o : asList){
	      res.put(o);
	    }
	    return res;
	}
	
    public static String getJSON(String address){
    	StringBuilder builder = new StringBuilder();
    	HttpClient client = new DefaultHttpClient();
    	HttpGet httpGet = new HttpGet(address);
    	try{
    		
    		HttpParams params=client.getParams();
    		HttpConnectionParams.setConnectionTimeout(params, (60*1000));
    		HttpResponse response = client.execute(httpGet);
    		StatusLine statusLine = response.getStatusLine();
    		int statusCode = statusLine.getStatusCode();
    		if(statusCode == 200){
    			HttpEntity entity = response.getEntity();
    			InputStream content = entity.getContent();
    			BufferedReader reader = new BufferedReader(new InputStreamReader(content));
    			String line;
    			while((line = reader.readLine()) != null){
    				builder.append(line);
    			}
    		} else {
    			Log.e(RegisterActivity.class.toString(),"ERROR OCCURED IN HTTP REQUEST :"+statusCode);
    		}
    	}catch(ClientProtocolException e){
    		e.printStackTrace();
    		return "ERROR";
    	} catch (IOException e){
    		e.printStackTrace();
    		return "ERROR";
    	}
    	return builder.toString();
    }
	
}