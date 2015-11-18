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
import org.egov.android.R;
import org.egov.android.controller.ApiController;
import org.egov.android.controller.ServiceController;
import org.egov.android.AndroidLibrary;
import org.egov.android.api.ApiResponse;
import org.egov.android.api.SSLTrustManager;
import org.egov.android.common.StorageManager;
import org.egov.android.data.SQLiteHelper;
import org.egov.android.listener.Event;
import org.egov.android.model.Complaint;
import org.egov.android.view.adapter.ComplaintAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SearchActivity extends BaseActivity implements OnItemClickListener,
        OnEditorActionListener {
    private ArrayList<Complaint> listItem = null;
    private ComplaintAdapter adapter;
    private int apiLevel = 0;
    private JSONArray downloadThumbImages = new JSONArray();

    /**
     * To set the layout for the SearchActivity .Set click listener to the search icon and editor
     * action listener to search EditText.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ((EditText) findViewById(R.id.search)).setOnEditorActionListener(this);
        ((ImageView) findViewById(R.id.search_icon)).setOnClickListener(this);

        apiLevel = AndroidLibrary.getInstance().getSession().getInt("api_level", 0);
    }

    /**
     * Event triggered when clicking on the item having click listener.On Clicking search icon
     * _getSearchList() function get called.
     */
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.search_icon:
                _getSearchList();
                break;
        }
    }

    /**
     * Function called after getting response from search complaint api to display the search list
     */
    private void _displayListView() {
        ListView list = (ListView) findViewById(R.id.search_list);
        adapter = new ComplaintAdapter(this, listItem, false, "search", apiLevel, null);
        list.setOnItemClickListener(this);
        list.setAdapter(adapter);
    }

    /**
     * Function called after getting search complaint api response to download the image of the
     * complaint. After downloading the images, the images will be updated in the list
     * 
     * @param path
     *            => complaint folder path
     * @param crn
     *            => contain complaint crn number
     */
    private void _addDownloadJobs(String path, String crn) {
        JSONObject jo = null;
        try {
        	
        	if(!new File(path + "/.thumb_photo_" + crn + ".jpg").exists())
        	{
	            jo = new JSONObject();
	            jo.put("url", AndroidLibrary.getInstance().getConfig().getString("api.baseUrl")
	                    + "/api/v1.0/complaint/" + crn + "/downloadSupportDocument");
	            jo.put("type", "complaintSearch");
	            jo.put("destPath", path + "/.thumb_photo_" + crn + ".jpg");
	            jo.put("isThumbnail", true);
	            /*SQLiteHelper.getInstance().execSQL(
	                    "INSERT INTO tbl_jobs(data, status, type, triedCount) values ('" + jo.toString()
	                            + "', 'waiting', 'download', 0)");*/
	            Log.d("org.egov.android", jo.getString("url"));
	            downloadThumbImages.put(jo);
        	}
            
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    class ImageDownloaderTask extends AsyncTask<JSONArray, Void, String> {
    	
    	private ProgressBar loader=null;
    	
    	public ImageDownloaderTask(ProgressBar loader) {
			// TODO Auto-generated constructor stub
    		this.loader=loader;
    		//this.mDialog = new ProgressDialog(context);
		}
    	
    	@Override
    	protected void onPreExecute() {
    		// TODO Auto-generated method stub
    		super.onPreExecute();
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
            _displayListView();
            adapter.notifyDataSetChanged();
            
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
	                if(!jobj.isNull("isThumbnail"))
	                {
                  	   params.add(new BasicNameValuePair("isThumbnail", String.valueOf(jobj.getBoolean("isThumbnail"))));
	                }
	                params.add(new BasicNameValuePair("access_token", accessToken));
	            	
	                /* Protocal Switch Condition Whether sending https request or http request */
	    			if (url.startsWith("https://")) {
	    			   new SSLTrustManager();
	    			   con = (HttpsURLConnection) new URL(generateDownloadImageURL(url, params)).openConnection();
	    			}
	    			else
	    			{
	    			   con = (HttpURLConnection) new URL(generateDownloadImageURL(url, params)).openConnection();
	    			}
	            	new SSLTrustManager();
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
     * Search complaint api response handler. Here we have checked the invalid access token error to
     * redirect to login page.
     */
    @Override
    public void onResponse(Event<ApiResponse> event) {
        String status = event.getData().getApiStatus().getStatus();
        String msg = event.getData().getApiStatus().getMessage();
        if (status.equalsIgnoreCase("success")) {
            try {
                JSONArray ja = new JSONArray(event.getData().getResponse().toString());
                listItem = new ArrayList<Complaint>();
                Complaint item = null;
                downloadThumbImages = new JSONArray();
                if (ja.length() > 0) {
                    StorageManager sm = new StorageManager();
                    Object[] obj = sm.getStorageInfo(SearchActivity.this);
                    ((TextView) findViewById(R.id.search_errMsg)).setVisibility(View.GONE);
                    for (int i = 0; i < ja.length(); i++) {
                        String complaintNo = "";
                        JSONObject data = ja.getJSONObject(i).getJSONObject("resource");
                        JSONObject searchObj = data.getJSONObject("searchable");
                        JSONObject commonObj = data.getJSONObject("common")
                                .getJSONObject("citizen");
                        JSONObject statusObj = data.getJSONObject("clauses")
                                .getJSONObject("status");

                        if (data.getJSONObject("clauses").has("crn")) {
                            complaintNo = data.getJSONObject("clauses").getString("crn");
                        } else {
                            complaintNo = searchObj.getString("crn");
                        }
                        item = new Complaint();
                        item.setCreatedDate(data.getJSONObject("common").getString("createdDate"));
                        item.setDetails(searchObj.getString("details"));
                        item.setComplaintId(complaintNo);
                        if (commonObj.has("name")) {
                            item.setCreatedBy(commonObj.getString("name"));
                        } else {
                            item.setCreatedBy("");
                        }
                        item.setStatus(statusObj.getString("name"));
                        String complaintFolderName = obj[0].toString()
                                + "/egovernments/search/complaints/" + complaintNo;
                        item.setImagePath(complaintFolderName + File.separator + ".thumb_photo_"
                                + complaintNo + ".jpg");
                        if(!new File(complaintFolderName).exists())
                        {
                         sm.mkdirs(complaintFolderName);
                        }
                        _addDownloadJobs(complaintFolderName, complaintNo);
                        listItem.add(item);
                    }
                    ServiceController.getInstance().startJobs();
                } else {
                    ((TextView) findViewById(R.id.search_errMsg)).setVisibility(View.VISIBLE);
                }
                
                if(downloadThumbImages.length()>0)
                {
                	new ImageDownloaderTask((ProgressBar)findViewById(R.id.searchimgloader)).execute(downloadThumbImages);
                }
                else{
                 _displayListView();
                 adapter.notifyDataSetChanged();
                }
                
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if (msg.matches(".*Invalid access token.*")) {
                showMessage("Session expired");
                startLoginActivity();
            } else {
                showMessage(msg);
            }
        }
    }

    /**
     * Event triggered when clicking on an item in listview. Clicking on list item redirect to
     * detail page
     */
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        Intent intent = new Intent(this, ComplaintDetailActivity.class);
        intent.putExtra("complaintId", listItem.get(position).getComplaintId());
        intent.putExtra("name", listItem.get(position).getCreatedBy());
        intent.putExtra("complaint_status", listItem.get(position).getStatus());
        startActivity(intent);
    }

    /**
     * Event triggered when pressing enter/done key,call _getSearchList() function
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                || (actionId == EditorInfo.IME_ACTION_DONE)) {
            _getSearchList();
        }
        return false;
    }

    /**
     * Function called when searching the complaints. If the search text field is empty or less than
     * 3 characters then show the error message. Otherwise call the search list api by search text.
     */
    private void _getSearchList() {
        String searchText = ((EditText) findViewById(R.id.search)).getText().toString().trim();

        if (searchText.equals("")) {
            showMessage(getMessage(R.string.search_empty));
        } else if (searchText.length() < 3) {
            showMessage(getMessage(R.string.search_length));
        } else {
            ApiController.getInstance().getSearchComplaints(this, searchText);
        }
    }
}
