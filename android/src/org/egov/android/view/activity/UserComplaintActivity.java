/**
 * eGov suite of products aim to improve the internal efficiency,transparency, accountability and the service delivery of the
 * government organizations.
 * 
 * Copyright (C) <2015> eGovernments Foundation
 * 
 * The updated version of eGov suite of products as by eGovernments Foundation is available at http://www.egovernments.org
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * http://www.gnu.org/licenses/ or http://www.gnu.org/licenses/gpl.html .
 * 
 * In addition to the terms of the GPL license to be adhered to in using this program, the following additional terms are to be
 * complied with:
 * 
 * 1) All versions of this program, verbatim or modified must carry this Legal Notice.
 * 
 * 2) Any misrepresentation of the origin of the material is prohibited. It is required that all modified versions of this
 * material be marked in reasonable ways as different from the original version.
 * 
 * 3) This license does not grant any rights to any user of the program with regards to rights under trademark law for use of the
 * trade names or trademarks of eGovernments Foundation.
 * 
 * In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.android.view.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.egov.android.AndroidLibrary;
import org.egov.android.R;
import org.egov.android.api.ApiResponse;
import org.egov.android.api.IApiListener;
import org.egov.android.api.SSLTrustManager;
import org.egov.android.common.StorageManager;
import org.egov.android.controller.ApiController;
import org.egov.android.listener.Event;
import org.egov.android.listener.IActionListener;
import org.egov.android.model.Complaint;
import org.egov.android.view.adapter.ComplaintAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserComplaintActivity extends Fragment implements IApiListener, OnItemClickListener,
        IActionListener {

	private static final String TAG = UserComplaintActivity.class.getName();
    private ArrayList<Complaint> listItem = new ArrayList<Complaint>();
    private ComplaintAdapter adapter;
    private boolean isApiLoaded = false;
    private int apiLevel = 0;
    private int page = 1;
    private JSONArray downloadThumbImages = new JSONArray();
    SwipeRefreshLayout mSwipeRefreshLayout;
    private Handler handler = new Handler();
    private boolean isRefresh=false;
    ListView lvcomplaint;
    View lvcomplaintloaderview;

    /**
     * The onActivityCreated() is called after the onCreateView() method when activity is created.
     * Get the api level from the session api level denotes the api versions of the android device
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        apiLevel = AndroidLibrary.getInstance().getSession().getInt("api_level", 0);
        lvcomplaint = (ListView) getActivity().findViewById(R.id.user_complaint_list);
        //refresh list operations
        mSwipeRefreshLayout=(SwipeRefreshLayout)getActivity().findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				refreshComplaints();
			}
		});
        
        mSwipeRefreshLayout.setColorSchemeResources(R.color.progressblue, R.color.progressorange, R.color.progressred);
        
        lvcomplaintloaderview = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.complaint_list_loader, null, false);
        //lvcomplaint.addFooterView(lvcomplaintloaderview);
    }
    
    private int _dpToPix(float value) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				value, getResources().getDisplayMetrics());
	}
    
    private void refreshComplaints()
    {
    	new Handler().postDelayed(new Runnable() {
	          @Override
	          public void run() {
	        	  page=1;
				  isRefresh=true;
				  new Handler().postDelayed(new Runnable() {
			          @Override
			          public void run() {
			        	  ApiController.getInstance().getUserComplaints(new IApiListener() {
							
							@Override
							public void onResponse(Event<ApiResponse> event) {
								// TODO Auto-generated method stub
								isRefresh=false;
								String pagination = event.getData().getApiStatus().isPagination();
								listComplaints(event);
								if(pagination!=null)
								{
									adapter.setListItem(listItem);
									adapter.notifyDataSetChanged();
									lvcomplaint.setSelection(0);
								}
							}
						}, page, false);
			          }
				  }, 100);
				  handler.post(refreshing);
	          }
	    }, 500);
    }
        
    private final Runnable refreshing = new Runnable(){
        public void run(){
            try {
                // TODO : isRefreshing should be attached to your data request status 
                if(isRefresh){
                    // re run the verification after 1 second
                    handler.postDelayed(this, 1000);   
                }else{
                    // stop the animation after the data is fully loaded
                    mSwipeRefreshLayout.setRefreshing(false);
                    // TODO : update your list with the new data 
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }   
        }
    };

    /**
     * This is used to call the api respect to the visible fragment.
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && listItem.size() == 0 && !isApiLoaded) {
            ApiController.getInstance().getUserComplaints(this, page, true);
        } else if (isVisibleToUser && listItem.size() != 0) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * To set the layout for the UserComplaintActivity.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_user_complaints, container, false);
    }

    /**
     * Function called after getting response from api call to display the list
     * 
     * @param isPagination
     *            => flag to inform the adapter to show load more button
     */
    private void _displayListView(boolean isPagination) {
        if(adapter==null)
        {
          lvcomplaint.setOnItemClickListener(this);
          adapter = new ComplaintAdapter(getActivity(), listItem, isPagination, "me", apiLevel, this);
          lvcomplaint.setAdapter(adapter);
        }
        else
        {
        	adapter.setPagination(isPagination);
        }
        adapter.notifyDataSetChanged();
        //lvcomplaintloaderview.setVisibility(View.GONE);
        //lvcomplaint.setSelectionFromTop(lastviewpos, topOffset);
    }

    /**
     * Function called after getting success api response to download the images under the
     * complaints. After downloading the images, the images will be updated in list
     * 
     * @param path
     *            => complaint folder path
     * @param jsonObj
     *            => contain complaint information
     */
    private void _addDownloadJobs(String path, JSONObject jsonObj) {
        JSONObject jo = null;
        try {
            int totalFiles = jsonObj.getInt("supportDocsSize");
            if (totalFiles == 0) {
            	
            	/*if(!new File(path + "/.thumb_photo_complaint_type.jpg").exists())
            	{
	                jo = new JSONObject();
	                jo.put("url",
	                        AndroidLibrary.getInstance().getConfig().getString("api.baseUrl")
	                                + "/pgr/resources/images/complaintType/"
	                                + jsonObj.getString("complaintTypeImage"));
	                jo.put("type", "complaintType");
	                jo.put("destPath", path + "/.thumb_photo_complaint_type.jpg");
	                jo.put("isThumbnail", true);
            	}*/
                /*SQLiteHelper.getInstance().execSQL(
                        "INSERT INTO tbl_jobs(data, status, type, triedCount) values ('"
                                + jo.toString() + "', 'waiting', 'download', 0)");*/
            } else {
                //for (int i = 1; i <= totalFiles; i++) {
            	if(!new File(path + "/.thumb_photo_" + totalFiles + ".jpg").exists())
            	{
                    jo = new JSONObject();
                    jo.put("url", AndroidLibrary.getInstance().getConfig().getString("api.baseUrl")
                            + "/api/v1.0/complaint/" + jsonObj.getString("crn")
                            + "/downloadSupportDocument");
                    jo.put("fileNo", totalFiles);
                    jo.put("type", "complaint");
                    jo.put("isThumbnail", true);
                    jo.put("destPath", path + "/.thumb_photo_" + totalFiles + ".jpg");
            	}
                /*SQLiteHelper.getInstance().execSQL(
                        "INSERT INTO tbl_jobs(data, status, type, triedCount) values ('"
                                + jo.toString() + "', 'waiting', 'download', 0)");*/
               //}
            }
            
            if(jo!=null){ downloadThumbImages.put(jo); }
            
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    
    class ImageDownloaderTask extends AsyncTask<JSONArray, Void, String> {
    	
    	private boolean isPagination=false;
    	private ProgressBar loader=null;
    	
    	public ImageDownloaderTask(ProgressBar loader, boolean isPagination) {
			// TODO Auto-generated constructor stub
    		this.isPagination=isPagination;
    		this.loader=loader;
    		//this.mDialog = new ProgressDialog(context);
		}
    	
    	@Override
    	protected void onPreExecute() {
    		// TODO Auto-generated method stub
    		super.onPreExecute();
    		if(loader!=null)
    		loader.setVisibility(View.VISIBLE);
            /*mDialog.setMessage("Loading...");
            mDialog.setCancelable(false);
            mDialog.show();*/
    	}

        @Override
        protected String doInBackground(JSONArray... params) {
            downloadBitmap(params[0]);
            return "";
        }

        @Override
        protected void onPostExecute(String downloadedImagePath) {
        	
        	if(loader!=null)
        	{
        	   loader.setVisibility(View.GONE);
        	}
            _displayListView(isPagination);
            
        }
        
        private String generateDownloadImageURL(String downImgURL, List<NameValuePair> params)
        {
        	try {
                if(!downImgURL.endsWith("?")){
                	downImgURL += "?";
                }
                String paramString = URLEncodedUtils.format(params, "utf-8");
                downImgURL += paramString;
            } catch (Exception e) {
                e.printStackTrace();
            }
        	return downImgURL;
        }
        
        private String downloadBitmap(JSONArray jsonarry) {
        	HttpURLConnection con = null;
        	
            try {
            	String accessToken = AndroidLibrary.getInstance().getSession()
                        .getString("access_token", "");
            	
            	String requestMethod = "GET";
            	
            	for(int i=0; i<jsonarry.length(); i++)
            	{
            		JSONObject jobj=jsonarry.getJSONObject(i);
	            	
	            	String url = jobj.getString("url");
	            	String filePath = jobj.getString("destPath");
	            	
	            	List<NameValuePair> params=new LinkedList<NameValuePair>();
	            	if (jobj.getString("type").equals("complaint")) {
	            	   params.add(new BasicNameValuePair("fileNo", jobj.getString("fileNo")));
	                }
	                if(!jobj.isNull("isThumbnail"))
	                {
                  	   params.add(new BasicNameValuePair("isThumbnail", String.valueOf(jobj.getBoolean("isThumbnail"))));
	                }
	                params.add(new BasicNameValuePair("access_token", accessToken));
	            	
	                if (url.startsWith("https://")) {
	    			   new SSLTrustManager();
	    			   con = (HttpsURLConnection) new URL(generateDownloadImageURL(url, params)).openConnection();
	    			}
	    			else
	    			{
	    			   con = (HttpURLConnection) new URL(generateDownloadImageURL(url, params)).openConnection();
	    			}
	                
	                con.setRequestMethod(requestMethod);
	                con.setUseCaches(false);
	                con.setDoInput(true);
	
	                InputStream inputStream = con.getInputStream();
	                if (inputStream != null) {
	                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
	                    saveImage(bitmap, filePath);
	                }
            	}
            	return "SUCCESS";
            } catch (Exception e) {
                e.printStackTrace();
                Log.w(TAG, "Error downloading image from server!");
            } finally {
                con.disconnect();
            }
            return null;
        }
        
        
        private void saveImage(Bitmap image, String filePath) {
            File pictureFile = new File(filePath);
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                image.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }  
        }
    }
    

    /**
     * Function used to check whether the key value exist in the given json object.If the key exists
     * return the value from the json object else return empty string
     * 
     * @param jo
     *            => json object to check the key existence
     * @param key
     *            => name of the key to check
     * @return string
     */
    
    private String _getValue(JSONObject jo, String key) {
        String result = "";
        try {
            result = (jo.has(key)) ? jo.getString(key) : "";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * The onResponse method will be invoked after the user complaints API call . onResponse methods
     * will contain the response. If the response has a status as 'success' then, we have checked
     * whether the access token is valid or not. If the access token is invalid, redirect to login
     * page. If the access token is valid createdDate,complainantName,detail,crn,status values are
     * retrieved from the response object and store it to the variable then these values are set to
     * the all complaint layout. then call the _addDownloadJobs method to display the complaint
     * photo from the complaint photos directory on the storage device. displays the user complaints
     * list with the corresponding complaint image. we have checked the pagination value.This value
     * is retrieved from the api response if the value is true then load more option will be
     * displayed below the user complaint list view.
     * 
     */
    @Override
    public void onResponse(Event<ApiResponse> event) {
    	listComplaints(event);
    }
    
    int lastviewpos=0;
    int topOffset=0;
    private void listComplaints(Event<ApiResponse> event)
    {
    	lastviewpos=lvcomplaint.getFirstVisiblePosition();
    	//get offset of first visible view
    	View v = lvcomplaint.getChildAt(0);
    	topOffset = (v == null) ? 0 : v.getTop();
    	
    	String status = event.getData().getApiStatus().getStatus();
        String pagination = event.getData().getApiStatus().isPagination();
        String msg = event.getData().getApiStatus().getMessage();

        if (page == 1) {
            listItem = new ArrayList<Complaint>();
        }

        if (status.equalsIgnoreCase("success")) {
        	
        	isApiLoaded = true;

            if (listItem.size() > 5) {
                listItem.remove(listItem.size() - 1);
            }
        	
            try {
                JSONArray ja = new JSONArray(event.getData().getResponse().toString());
               
                if (ja.length() > 0) {
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);
                        Complaint item = new Complaint();
                        item.setCreatedDate(_getValue(jo, "createdDate"));
                        item.setDetails(_getValue(jo, "detail"));
                        item.setComplaintId(_getValue(jo, "crn"));
                        item.setStatus(jo.getString("status"));
                        
                        StorageManager sm = new StorageManager();
                        Object[] obj = sm.getStorageInfo(UserComplaintActivity.this.getActivity());
                        String complaintFolderName = obj[0].toString()
                                +"/complaints/" + jo.getString("crn");
                        File complaintFolder = new File(complaintFolderName);
                        if (jo.getInt("supportDocsSize") == 0) {
                            /*item.setImagePath(complaintFolderName + File.separator
                                    + ".thumb_photo_complaint_type.jpg");*/
                        	item.setImagePath("");
                            
                        } else {
                            item.setImagePath(complaintFolderName + File.separator + ".thumb_photo_"
                                    + jo.getInt("supportDocsSize") + ".jpg");
                            _addDownloadJobs(complaintFolderName, jo);
                        }
                        
                        if (!complaintFolder.exists()) {   
                            sm.mkdirs(complaintFolderName);
                        }
                        
                        listItem.add(item);
                    }
                    
                    if (listItem.size() > 5 && !isRefresh) {
                        lvcomplaint.postDelayed(new Runnable() {
                            public void run() {
                            	lvcomplaint.setStackFromBottom(true);
                            	lvcomplaint.setSelectionFromTop(lastviewpos, topOffset);
                            }
                        }, 100);
                    }

                    if (pagination.equals("true")) {
                    	Complaint item = new Complaint();
                        listItem.add(item);
                    }
                    
                    if(downloadThumbImages.length()>0)
                    {
                      new ImageDownloaderTask((!isRefresh?null:getProgressBar()) , pagination.equals("true")).execute(downloadThumbImages);
                    }
                    else
                    {
                      if(!isRefresh)
                      {
                        _displayListView(pagination.equals("true"));
                      }
                    }
                    
                    //ServiceController.getInstance().startJobs();
                    //_displayListView(pagination.equals("true"));

                } else if (listItem.size() == 0) {
                    ((TextView) getActivity().findViewById(R.id.user_errMsg))
                            .setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if (msg.matches(".*Invalid access token.*")) {
                _showMsg("Session expired");
                AndroidLibrary.getInstance().getSession().edit().putString("access_token", "")
                        .commit();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
                getActivity().finish();
            } else {
                page = (page > 1) ? page - 1 : 1;
                _showMsg(msg);
            }
        }
    }
    
    private ProgressBar getProgressBar()
    {
    	ProgressBar pb=(ProgressBar)getActivity().findViewById(R.id.imagelistloader);
    	return pb;
    }

    /**
     * Function used to show a message in toast.
     * 
     * @param message
     */
    private void _showMsg(String message) {
        if (message != null && !message.equals("")) {
            Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 120);
            toast.show();
        }
    }

    /**
     * Event triggered when clicking on an item in listview. Clicking on list item redirect to
     * detail page
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Complaint complaint = listItem.get(position);
        Intent intent = new Intent(getActivity(), ComplaintDetailActivity.class);
        intent.putExtra("complaintId", complaint.getComplaintId());
        intent.putExtra("name", complaint.getCreatedBy());
        intent.putExtra("complaint_status", complaint.getStatus());
        startActivity(intent);
    }

    /**
     * Event triggered when clicking on load more in ComplaintAdapter to call api.
     */
    @Override
    public void actionPerformed(String tag, Object... value) {
        if (tag.equals("LOAD_MORE")) {
        	lvcomplaint.setSelectionFromTop(lvcomplaint.getFirstVisiblePosition(),lvcomplaint.getChildAt(0).getTop() + 10);
            page = page + 1;
            ApiController.getInstance().getUserComplaints(this, page, true);
        }
    }
    
}
