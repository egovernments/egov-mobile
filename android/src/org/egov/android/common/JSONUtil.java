package org.egov.android.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;

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
	
}