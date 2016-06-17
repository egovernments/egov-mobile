/*
 * ******************************************************************************
 *  eGov suite of products aim to improve the internal efficiency,transparency,
 *      accountability and the service delivery of the government  organizations.
 *
 *        Copyright (C) <2016>  eGovernments Foundation
 *
 *        The updated version of eGov suite of products as by eGovernments Foundation
 *        is available at http://www.egovernments.org
 *
 *        This program is free software: you can redistribute it and/or modify
 *        it under the terms of the GNU General Public License as published by
 *        the Free Software Foundation, either version 3 of the License, or
 *        any later version.
 *
 *        This program is distributed in the hope that it will be useful,
 *        but WITHOUT ANY WARRANTY; without even the implied warranty of
 *        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *        GNU General Public License for more details.
 *
 *        You should have received a copy of the GNU General Public License
 *        along with this program. If not, see http://www.gnu.org/licenses/ or
 *        http://www.gnu.org/licenses/gpl.html .
 *
 *        In addition to the terms of the GPL license to be adhered to in using this
 *        program, the following additional terms are to be complied with:
 *
 *    	1) All versions of this program, verbatim or modified must carry this
 *    	   Legal Notice.
 *
 *    	2) Any misrepresentation of the origin of the material is prohibited. It
 *    	   is required that all modified versions of this material be marked in
 *    	   reasonable ways as different from the original version.
 *
 *    	3) This license does not grant any rights to any user of the program
 *    	   with regards to rights under trademark law for use of the trade names
 *    	   or trademarks of eGovernments Foundation.
 *
 *      In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *  *****************************************************************************
 */

package org.egov.employee.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import org.egov.employee.adapter.ImageGridAdapter;
import org.egov.employee.adapter.UriImageGridAdapter;
import org.egov.employee.api.ApiController;
import org.egov.employee.api.ApiUrl;
import org.egov.employee.controls.AutoHeightGridView;
import org.egov.employee.data.ComplaintDetails;
import org.egov.employee.data.ComplaintHistory;
import org.egov.employee.data.ComplaintViewAPIResponse;
import org.egov.employee.data.Task;
import org.egov.employee.utils.ImageCompressionHelper;
import org.egov.employee.utils.LatLngAddressParser;
import org.egov.employee.utils.UriPathHelper;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import offices.org.egov.egovemployees.R;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ViewTask extends BaseActivity {

    private static final int REQUEST_CODE_ASK_PERMISSION_CALL = 115;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    Task viewTask;
    String OPERATION_LOAD_COMPLAINT_STATUS_DEPS="LOAD_STATUS_DEPS";
    String OPERATION_FORWARD_DESIGNATION="FORWARD_DESIGNATION";
    String OPERATION_FORWARD_USERS="FORWARD_USERS";
    String PGR_FORWARD_ACTION ="FORWARDED";
    JsonArray forwardDept;
    JsonArray forwardDesg;
    JsonArray forwardUser;

    private File cacheDir;

    private static final int PICK_PHOTO_FROM_CAMERA = 111;
    private static final int PICK_PHOTO_FROM_GALLERY = 222;


    final private int REQUEST_CODE_ASK_PERMISSIONS_CAMERA = 456;
    final private int REQUEST_CODE_ASK_PERMISSIONS_READ_ACCESS = 789;

    ArrayList<Uri> listUploadDocs=new ArrayList<>();


    public String GrievanceModuleName="GRIEVANCE";

    AutoHeightGridView imgUploadGridView;
    Button btnClearAttachments;

    private ArrayList<String> imageIdxForCamera = new ArrayList<>(Arrays.asList("1", "2", "3"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(viewTask.getTask());

        cacheDir = this.getExternalCacheDir() == null ? this.getCacheDir() : this.getExternalCacheDir();

        if(viewTask.getTask().toUpperCase().equals(GrievanceModuleName))
        {
            initializeListenersAndComponentsForComplaint();
        }
        else {
            showSnackBar("NO UI SUPPORT FOR THIS ITEM !");
        }

    }

    @Override
    protected int getLayoutResource() {
        viewTask=(Task) getIntent().getSerializableExtra("task");
        if(viewTask.getTask().toUpperCase().equals(GrievanceModuleName))
        {
            return R.layout.activity_view_complaint;
        }
        else{
            return R.layout.activity_view_task;
        }
    }

    public class ComplaintComponents{

        ProgressBar pb;
        ProgressBar pbCompHistory;
        ProgressBar pbCombActions;
        ScrollView svContent;
        LinearLayout layoutLandMark;
        LinearLayout layoutCompComments;
        LinearLayout layoutToggleComments;
        LinearLayout layoutForward;
        LinearLayout layoutCompAttachments;
        LinearLayout layoutCompHistory;
        LinearLayout layoutCompActions;

        TextView tvComplaintNo;
        TextView tvComplaintDate;
        TextView tvComplainantName;
        TextView tvComplainantMobileNo;
        TextView tvComplaintType;
        TextView tvComplaintLoc;
        TextView tvComplaintDesc;
        TextView tvComplaintLandmark;
        TextView tvComplaintStatus;
        TextView tvCall;
        TextView tvOpenMap;

        EditText etComments;

        Spinner spinnerCompStatus;
        Spinner spinnerForwardDept;
        Spinner spinnerForwardDesg;
        Spinner spinnerForwardEmp;

        Button btnAction;
        Button btnMoreComments;
        Button btnAttachPhotos;

        AutoHeightGridView girdCompImg;
    }

    public void initializeListenersAndComponentsForComplaint()
    {

        ComplaintComponents complaintComponents=new ComplaintComponents();
        complaintComponents.pb=(ProgressBar)findViewById(R.id.progressBar);
        complaintComponents.pbCompHistory=(ProgressBar)findViewById(R.id.pbCompHistory);
        complaintComponents.pbCombActions=(ProgressBar)findViewById(R.id.pbCompAction);
        complaintComponents.svContent=(ScrollView)findViewById(R.id.svcontent);
        complaintComponents.layoutLandMark=(LinearLayout)findViewById(R.id.layoutlandmark);
        complaintComponents.layoutCompComments =(LinearLayout)findViewById(R.id.complaintcommentscontainer);
        complaintComponents.layoutToggleComments = (LinearLayout) findViewById(R.id.complainttogglecomments);
        complaintComponents.layoutForward=(LinearLayout)findViewById(R.id.layoutforward);
        complaintComponents.layoutCompAttachments=(LinearLayout)findViewById(R.id.layoutcompAttachments);
        complaintComponents.layoutCompHistory=(LinearLayout)findViewById(R.id.layoutCompHistory);
        complaintComponents.layoutCompActions=(LinearLayout)findViewById(R.id.layoutCompActionControls);

        complaintComponents.tvComplaintNo=(TextView)findViewById(R.id.tvcomplaintno);
        complaintComponents.tvComplaintDate=(TextView)findViewById(R.id.tvcomplaintdate);
        complaintComponents.tvComplainantName=(TextView)findViewById(R.id.tvcomplainantname);
        complaintComponents.tvComplainantMobileNo=(TextView)findViewById(R.id.tvcomplainantmobno);
        complaintComponents.tvComplaintType=(TextView)findViewById(R.id.tvcomplainttype);
        complaintComponents.tvComplaintLoc=(TextView)findViewById(R.id.tvcomplaintloc);
        complaintComponents.tvComplaintDesc=(TextView)findViewById(R.id.tvcomplaintdesc);
        complaintComponents.tvComplaintLandmark=(TextView)findViewById(R.id.tvlandmark);
        complaintComponents.tvComplaintStatus=(TextView)findViewById(R.id.tvcomplaintstatus);
        complaintComponents.tvCall=(TextView)findViewById(R.id.tvCall);
        complaintComponents.tvOpenMap=(TextView)findViewById(R.id.tvOpenMap);

        complaintComponents.etComments=(EditText)findViewById(R.id.etcomplaintcomments);

        complaintComponents.spinnerCompStatus = (Spinner) findViewById(R.id.spinnercomplaintactions);
        complaintComponents.spinnerForwardDept = (Spinner) findViewById(R.id.spinnerdept);
        complaintComponents.spinnerForwardDesg = (Spinner) findViewById(R.id.spinnerdesg);
        complaintComponents.spinnerForwardEmp = (Spinner) findViewById(R.id.spinneremp);

        complaintComponents.btnAction=(Button)findViewById(R.id.btnaction);
        complaintComponents.btnMoreComments=(Button)findViewById(R.id.btnmorecomments);
        complaintComponents.btnAttachPhotos=(Button) findViewById(R.id.btnattachphoto);
        btnClearAttachments=(Button)findViewById(R.id.btnclearphoto);

        //get complaint no from task object
        complaintComponents.tvComplaintNo.setText(viewTask.getRefNum());
        complaintComponents.tvComplainantName.setText(viewTask.getCitizenName());
        complaintComponents.tvComplainantMobileNo.setText(viewTask.getCitizenPhoneno());

        complaintComponents.girdCompImg =(AutoHeightGridView)findViewById(R.id.gridviewDocs);

        imgUploadGridView=(AutoHeightGridView) findViewById(R.id.gridviewUploadDocs);

        getComplaintDetails(complaintComponents);

    }

    public void loadOrRefreshUploadImageGrid()
    {
        btnClearAttachments.setVisibility((listUploadDocs.size()>0?View.VISIBLE:View.GONE));
        if(imgUploadGridView.getAdapter()!=null)
        {
            UriImageGridAdapter adapter=(UriImageGridAdapter)imgUploadGridView.getAdapter();
            adapter.notifyDataSetChanged();
        }
        else{
            imgUploadGridView.setAdapter(new UriImageGridAdapter(this, listUploadDocs));
            imgUploadGridView.setExpanded(true);
        }
    }

    private void loadComplaintComments(List<ComplaintHistory> grievanceComments, final LinearLayout layoutToggleComments, final LinearLayout layoutCompComments,final Button btnMoreComments)
    {

        //more comments button show hide check
        if(grievanceComments.size()<=2)
        {
            btnMoreComments.setVisibility(View.GONE);
        }
        else{
            btnMoreComments.setVisibility(View.VISIBLE);
            String commentsText=(grievanceComments.size()-2)+" COMMENTS";
            btnMoreComments.setText(commentsText);
        }

        btnMoreComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layoutToggleComments.getVisibility() == View.VISIBLE)
                {
                    btnMoreComments.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_down_white_24dp, 0);
                    layoutToggleComments.setVisibility(View.GONE);
                }
                else
                {
                    btnMoreComments.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_keyboard_arrow_up_white_24dp,0);
                    layoutToggleComments.setVisibility(View.VISIBLE);
                }
            }
        });

        layoutToggleComments.removeAllViews();
        layoutCompComments.removeAllViews();

        for(int i=(grievanceComments.size()-1);i>=0;i--)
        {
            View commentItemTemplate=getLayoutInflater().inflate(R.layout.template_comment_item,null);
            ComplaintHistory comment=grievanceComments.get(i);
            TextView tvUserType=(TextView)commentItemTemplate.findViewById(R.id.tvUserType);
            TextView tvUserName=(TextView)commentItemTemplate.findViewById(R.id.commenter_name);
            tvUserName.setText(comment.getUpdatedBy());

            if (comment.getUpdatedUserType().equals("EMPLOYEE")) {
                tvUserType.setBackgroundResource(R.drawable.round_red_bg);
                tvUserType.setText("O");
            }
            else {
                tvUserType.setBackgroundResource(R.drawable.round_blue_bg);
                tvUserType.setText("C");
            }
            if(preference.getUserName().equals(comment.getUpdatedBy()))
                tvUserName.setText("Me");

            ((TextView)commentItemTemplate.findViewById(R.id.comment_datetime)).setText(formatDateString(comment.getDate(), "MMM dd, yyyy hh:mm:ss aa", "dd/MM/yyyy hh:mm aa"));
            if(!TextUtils.isEmpty(comment.getComments()))
            {
                ((TextView)commentItemTemplate.findViewById(R.id.comment_text)).setText(comment.getComments());
            }
            else
            {
                String commentText="Status has been changed into "+comment.getStatus();
                ((TextView)commentItemTemplate.findViewById(R.id.comment_text)).setText(commentText);
            }

            ((TextView)commentItemTemplate.findViewById(R.id.comment_status)).setText(comment.getStatus());

            if(i<=1)
            {
                layoutCompComments.addView(commentItemTemplate);
            }
            else {
                layoutToggleComments.addView(commentItemTemplate);
            }
        }
    }

    //return date format dd/MM/yyy hh:mm aa
    private String formatDateString(String dateText, String currentDateFormat, String outputDateFormat)
    {
        try {
            return new SimpleDateFormat(outputDateFormat, Locale.ENGLISH)
                    .format(new SimpleDateFormat(currentDateFormat, Locale.ENGLISH)
                            .parse(dateText));
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    //get complaint details
    public void getComplaintDetails(final ComplaintComponents complaintComponents)
    {
        String currentMethodName=Thread.currentThread().getStackTrace()[2].getMethodName();

        if(ViewTask.this.checkInternetConnectivity(ViewTask.this, currentMethodName)) {

            Call<ComplaintViewAPIResponse> getCompDetails = ApiController.getAPI(getApplicationContext(), ViewTask.this).getComplaintDetails(viewTask.getRefNum(), preference.getApiAccessToken());

            Callback<ComplaintViewAPIResponse> complaintDetailsCallBack = new Callback<ComplaintViewAPIResponse>() {

                @Override
                public void onResponse(Response<ComplaintViewAPIResponse> response, Retrofit retrofit) {
                    showComplaintDetails(response.body().getComplaintDetails(), complaintComponents);
                }

                @Override
                public void onFailure(Throwable t) {

                }
            };
            getCompDetails.enqueue(complaintDetailsCallBack);
            //get complaint history
            getCommentsHistory(complaintComponents);

            loadComplaintActions(complaintComponents);
        }
    }

    public void getCommentsHistory(final ComplaintComponents complaintComponents)
    {
        complaintComponents.pbCompHistory.setVisibility(View.VISIBLE);
        complaintComponents.layoutCompHistory.setVisibility(View.GONE);

        Call<ComplaintViewAPIResponse.HistoryAPIResponse> getCompHistory = ApiController.getAPI(getApplicationContext(), ViewTask.this).getComplaintHistory(viewTask.getRefNum(), preference.getApiAccessToken());

        Callback<ComplaintViewAPIResponse.HistoryAPIResponse> complaintHistoryCallBack = new Callback<ComplaintViewAPIResponse.HistoryAPIResponse>() {

            @Override
            public void onResponse(Response<ComplaintViewAPIResponse.HistoryAPIResponse> response, Retrofit retrofit) {
                complaintComponents.pbCompHistory.setVisibility(View.GONE);
                complaintComponents.layoutCompHistory.setVisibility(View.VISIBLE);
                loadComplaintComments(response.body().getResult().getComments(), complaintComponents.layoutToggleComments, complaintComponents.layoutCompComments, complaintComponents.btnMoreComments);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        };
        getCompHistory.enqueue(complaintHistoryCallBack);
    }

    public void showComplaintDetails(final ComplaintDetails details, final ComplaintComponents complaintComponents)
    {
        complaintComponents.pb.setVisibility(View.GONE);
        complaintComponents.svContent.setVisibility(View.VISIBLE);

        complaintComponents.tvComplaintDate.setText(formatDateString(details.getCreatedDate(),"yyyy-MM-dd hh:mm:ss.SSS","dd/MM/yyyy hh:mm aa"));
        complaintComponents.tvComplaintType.setText(details.getComplaintTypeName());

        if(TextUtils.isEmpty(details.getLandmarkDetails()))
        {
          complaintComponents.layoutLandMark.setVisibility(View.GONE);
        }
        else{
            complaintComponents.tvComplaintLandmark.setText(details.getLandmarkDetails());
        }

        complaintComponents.tvComplaintDesc.setText(details.getDetail());
        complaintComponents.tvComplaintStatus.setText(details.getStatus());

        final ArrayList<String> complaintImgsUrl=new ArrayList<>();

        for(ComplaintDetails.SupportDoc doc:details.getSupportDocs())
        {
            complaintImgsUrl.add(preference.getActiveCityUrl()+ApiUrl.PGR_DOWNLOAD_IMAGE+doc.getFileId()+"?access_token="+preference.getApiAccessToken());
        }

        if(complaintImgsUrl.size()==0)
        {
            complaintComponents.layoutCompAttachments.setVisibility(View.GONE);
        }
        else {
            complaintComponents.girdCompImg.setAdapter(new ImageGridAdapter(this, complaintImgsUrl));
            complaintComponents.girdCompImg.setExpanded(true);
        }

        complaintComponents.girdCompImg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intOpenImageView=new Intent(ViewTask.this, ImageViewerActivity.class);
                intOpenImageView.putExtra("position", position);
                ArrayList<String> imageUrls=new ArrayList<>();
                imageUrls.addAll(complaintImgsUrl);
                intOpenImageView.putStringArrayListExtra("imageUrls", imageUrls);
                startActivity(intOpenImageView);
            }
        });

        complaintComponents.tvCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callToCitizen();
            }
        });

        if(details.getLat()!=null)
        {
            complaintComponents.tvComplaintLoc.setText("Loading...");

            //getting address from lat, lng
            setComplaintAddressFromLatLng(complaintComponents.tvComplaintLoc, new Double[]{details.getLat(), details.getLng()});

            complaintComponents.tvOpenMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                openMap(details.getLat(), details.getLng(), 16);
                }
            });
        }
        else
        {
            complaintComponents.tvComplaintLoc.setText(details.getChildLocationName()+" - "+details.getLocationName());
            complaintComponents.tvOpenMap.setVisibility(View.GONE);
        }

        complaintComponents.btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateComplaintActionControls(complaintComponents))
                {
                    int approvalPosition=0;
                    String complaintStatus=complaintComponents.spinnerCompStatus.getSelectedItem().toString();
                    String complaintComments=complaintComponents.etComments.getText().toString().trim();
                    if(complaintStatus.equals(PGR_FORWARD_ACTION))
                    {
                        approvalPosition=forwardUser.get((complaintComponents.spinnerForwardEmp.getSelectedItemPosition()-1)).getAsJsonObject().get("id").getAsInt();
                    }

                    updateComplaint(complaintStatus, approvalPosition, complaintComments);

                }
            }
        });

    }


    public void updateComplaint(String complaintStatus, int approvalPosition, String complaintComments)
    {
        JsonObject jsonParams=new JsonObject();
        jsonParams.addProperty("action", complaintStatus);
        if(approvalPosition>0)
        {
            jsonParams.addProperty("approvalposition",approvalPosition);
        }
        jsonParams.addProperty("comment", complaintComments);

        Map<String, RequestBody> uploadPics = new HashMap<>();

        for(Uri uploadDoc:listUploadDocs)
        {
            String mimeType = getMimeType(uploadDoc);
            String path;
            path = UriPathHelper.getRealPathFromURI(uploadDoc, getApplicationContext());
            path = ImageCompressionHelper.compressImage(path, path);
            File uploadFile=new File(path);
            RequestBody fileBody=RequestBody.create(MediaType.parse(mimeType), new File(path));
            uploadPics.put("files\"; filename=\"" + uploadFile.getName(), fileBody);
        }

        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), jsonParams.toString());

        uploadPics.put("jsonParams", name);

        final ProgressDialog pb=new ProgressDialog(ViewTask.this);
        pb.setCancelable(false);
        pb.setMessage("Updating Complaint...");
        pb.show();

        Call<JsonObject> complaintUpdate=ApiController.getAPI(getApplicationContext(), ViewTask.this).updateComplaint(viewTask.getRefNum(), preference.getApiAccessToken(), uploadPics);

        Callback<JsonObject> complaintUpdateCallBack = new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response, Retrofit retrofit) {

                pb.dismiss();
                setResult(RESULT_OK, new Intent());
                finish();
            }

            @Override
            public void onFailure(Throwable t) {
                pb.dismiss();
            }
        };

        complaintUpdate.enqueue(complaintUpdateCallBack);
    }

    //Returns the mime type of file. If it cannot be resolved, assumed to be jpeg
    private String getMimeType(Uri uri) {
        String mimeType;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver contentResolver = ViewTask.this.getContentResolver();
            mimeType = contentResolver.getType(uri);
            return mimeType;
        }
        return "image/jpeg";
    }



    public boolean validateComplaintActionControls(ComplaintComponents complaintComponents)
    {

        boolean isValidationSuccess=true;
        String selectedStatus=complaintComponents.spinnerCompStatus.getSelectedItem().toString();

        if(selectedStatus.equals(PGR_FORWARD_ACTION))
        {
            if(complaintComponents.spinnerForwardEmp.getSelectedItemPosition()==0)
            {
                showSnackBar("Please select a user want to forward");
                isValidationSuccess=false;
            }
        }
        else if(TextUtils.isEmpty(complaintComponents.etComments.getText().toString().trim()))
        {
            showSnackBar("Please enter your comments");
            isValidationSuccess=false;
        }

        return isValidationSuccess;

    }

    public void openMap(Double latitude, Double longitude, int zoomLevel)
    {
        String label = "C.No : "+viewTask.getRefNum();
        String uriBegin = "geo:" + latitude + "," + longitude;
        String query = latitude + "," + longitude + "(" + label + ")";
        String encodedQuery = Uri.encode(query);
        String uriString = uriBegin + "?q=" + encodedQuery + "&z=" + zoomLevel;
        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void callToCitizen()
    {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + viewTask.getCitizenPhoneno()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (Build.VERSION.SDK_INT < 23) {
            getApplication().startActivity(intent);
        }
        else{
            if(checkCallPermision())
            {
                getApplication().startActivity(intent);
            }
        }
    }

    public void loadComplaintActions(final ComplaintComponents complaintComponents)
    {
        complaintComponents.layoutCompActions.setVisibility(View.GONE);
        complaintComponents.pbCombActions.setVisibility(View.VISIBLE);

        Call<JsonObject> loadCompStatusAndDeps=ApiController.getAPI(getApplicationContext(), ViewTask.this).getComplaintActions(viewTask.getRefNum(), preference.getApiAccessToken());
        fillComplaintActions(loadCompStatusAndDeps, complaintComponents, OPERATION_LOAD_COMPLAINT_STATUS_DEPS);

    }

    public void loadForwardDesignations(int departmentId, ComplaintComponents complaintComponents){
        setSpinnerOptionsFromJsonArray(complaintComponents.spinnerForwardDesg, forwardDesg, "name", "LOADING DESIGNATIONS", "");
        Call<JsonObject> loadForwardDesgn=ApiController.getAPI(getApplicationContext(), ViewTask.this).getForwardDetails(String.valueOf(departmentId),"", preference.getApiAccessToken());
        fillComplaintActions(loadForwardDesgn, complaintComponents, OPERATION_FORWARD_DESIGNATION);
    }

    public void loadForwardUsers(int departmentId, int designationId, ComplaintComponents complaintComponents){
        setSpinnerOptionsFromJsonArray(complaintComponents.spinnerForwardEmp, forwardUser, "name", "LOADING USERS", "");
        Call<JsonObject> loadForwardUsers=ApiController.getAPI(getApplicationContext(), ViewTask.this).getForwardDetails(String.valueOf(departmentId),String.valueOf(designationId), preference.getApiAccessToken());
        fillComplaintActions(loadForwardUsers, complaintComponents, OPERATION_FORWARD_USERS);
    }

    public void fillComplaintActions(Call<JsonObject> actionCall, final ComplaintComponents complaintComponents, final String operation){

        Callback<JsonObject> complaintDetailsCallBack = new Callback<JsonObject>() {

            @Override
            public void onResponse(Response<JsonObject> response, Retrofit retrofit) {
                JsonObject resultJObj=response.body().get("result").getAsJsonObject();
                if(operation.equals(OPERATION_LOAD_COMPLAINT_STATUS_DEPS))
                {
                    complaintComponents.pbCombActions.setVisibility(View.GONE);
                    complaintComponents.layoutCompActions.setVisibility(View.VISIBLE);
                    fillComplaintStatusAndDepartments(resultJObj, complaintComponents);
                }
                else if(operation.equals(OPERATION_FORWARD_DESIGNATION))
                {
                    forwardDesg=resultJObj.get("designations").getAsJsonArray();
                    String defaultOptionText=(forwardDesg.size()>0?"SELECT DESIGNATION":"NO DESIGNATIONS");
                    setSpinnerOptionsFromJsonArray(complaintComponents.spinnerForwardDesg, forwardDesg, "name", defaultOptionText, "");
                }
                else if(operation.equals(OPERATION_FORWARD_USERS))
                {
                    forwardUser=resultJObj.get("users").getAsJsonArray();
                    String defaultOptionText=(forwardUser.size()>0?"SELECT USER":"NO USERS");
                    setSpinnerOptionsFromJsonArray(complaintComponents.spinnerForwardEmp, forwardUser, "name", defaultOptionText, "");
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        };

        actionCall.enqueue(complaintDetailsCallBack);
    }

    public void fillComplaintStatusAndDepartments(JsonObject jsonObject, final ComplaintComponents complaintComponents)
    {

        forwardDept=new JsonArray();
        forwardDesg=new JsonArray();
        forwardUser=new JsonArray();

        forwardDept=jsonObject.get("department").getAsJsonArray();
        //load complaint available status
        setSpinnerOptionsFromJsonArray(complaintComponents.spinnerCompStatus, jsonObject.get("status").getAsJsonArray(), "name", "", viewTask.getStatus());

        //load complaint forward departments
        setSpinnerOptionsFromJsonArray(complaintComponents.spinnerForwardDept, forwardDept, "name", "SELECT DEPARTMENT", "");

        setSpinnerOptionsFromJsonArray(complaintComponents.spinnerForwardDesg, forwardDesg, "name", "NO DESIGNATION", "");

        setSpinnerOptionsFromJsonArray(complaintComponents.spinnerForwardEmp, forwardUser, "name", "NO USER", "");


        if(complaintComponents.spinnerCompStatus.getSelectedItem().toString().equals(PGR_FORWARD_ACTION))
        {
            complaintComponents.layoutForward.setVisibility(View.VISIBLE);
        }

        complaintComponents.spinnerCompStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(complaintComponents.spinnerCompStatus.getSelectedItem().toString().equals(PGR_FORWARD_ACTION))
                {
                    complaintComponents.layoutForward.setVisibility(View.VISIBLE);
                }
                else{
                    complaintComponents.layoutForward.setVisibility(View.GONE);
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        complaintComponents.spinnerForwardDept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                forwardDesg = new JsonArray();
                forwardUser = new JsonArray();
                setSpinnerOptionsFromJsonArray(complaintComponents.spinnerForwardDesg, forwardDesg, "name", "NO DESIGNATIONS", "");
                setSpinnerOptionsFromJsonArray(complaintComponents.spinnerForwardEmp, forwardUser, "name", "NO USERS", "");
                int selectedPos=complaintComponents.spinnerForwardDept.getSelectedItemPosition();
                if(selectedPos>0) {
                    JsonObject selJsonDept=forwardDept.get((selectedPos-1)).getAsJsonObject();
                    loadForwardDesignations(selJsonDept.get("id").getAsInt(), complaintComponents);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        complaintComponents.spinnerForwardDesg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                forwardUser=new JsonArray();
                setSpinnerOptionsFromJsonArray(complaintComponents.spinnerForwardEmp, forwardUser, "name", "NO USERS", "");

                int selectedPosDept=complaintComponents.spinnerForwardDept.getSelectedItemPosition();
                int selectedPosDesg=complaintComponents.spinnerForwardDesg.getSelectedItemPosition();
                if(selectedPosDept>0 && selectedPosDesg>0) {
                    JsonObject selJsonDept=forwardDept.get((selectedPosDept-1)).getAsJsonObject();
                    JsonObject selJsonDesg=forwardDesg.get((selectedPosDesg-1)).getAsJsonObject();
                    loadForwardUsers(selJsonDept.get("id").getAsInt(), selJsonDesg.get("id").getAsInt(), complaintComponents);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnClearAttachments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listUploadDocs.clear();
                imageIdxForCamera=new ArrayList<>(Arrays.asList("1", "2", "3"));
                loadOrRefreshUploadImageGrid();
            }
        });

        complaintComponents.btnAttachPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(listUploadDocs.size()<3)
                {
                    final Dialog imagePickerDialog = new Dialog(ViewTask.this);
                    imagePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    imagePickerDialog.setContentView(R.layout.image_chooser_dialog);
                    imagePickerDialog.setCanceledOnTouchOutside(true);


                    imagePickerDialog.findViewById(R.id.from_gallery).setOnClickListener(new View.OnClickListener() {
                        //Opens default gallery app
                        @Override
                        public void onClick(View v) {

                            if (Build.VERSION.SDK_INT < 23) {
                                openImagePicker();
                            } else {
                                if (checkReadAccessPermission()) {
                                    openImagePicker();
                                }
                            }

                            imagePickerDialog.dismiss();

                        }
                    });

                    imagePickerDialog.findViewById(R.id.from_camera).setOnClickListener(new View.OnClickListener() {
                        //Opens default camera app
                        @Override
                        public void onClick(View v) {
                            if (Build.VERSION.SDK_INT < 23) {
                                openCamera();
                            } else {
                                if (checkCameraPermission()) {
                                    openCamera();
                                }
                            }
                            imagePickerDialog.dismiss();
                        }
                    });

                    imagePickerDialog.show();
                }
                else {
                    showSnackBar("Attachments are max up to 3");
                }
            }
        });
    }

    //pick image from gallery
    public void openImagePicker()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FROM_GALLERY);
    }

    //capture photo from camera
    private void openCamera() {
        File file = new File(cacheDir, "POST_IMAGE_" + imageIdxForCamera.get(0) + ".jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, PICK_PHOTO_FROM_CAMERA);
    }

    public void setSpinnerOptionsFromJsonArray(Spinner spinner, JsonArray jsonArray, String keyToExport, String defaultOptionText, String defaultSelectedText)
    {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, getArrayListFromJsonArray(jsonArray, keyToExport, defaultOptionText));
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        if(!TextUtils.isEmpty(defaultSelectedText))
        spinner.setSelection(dataAdapter.getPosition(defaultSelectedText));
    }

    public ArrayList<String> getArrayListFromJsonArray(JsonArray jsonArray, String keyToExport, String defaultOptionText)
    {
        ArrayList<String> options=new ArrayList<>();

        if(!TextUtils.isEmpty(defaultOptionText))
        {
            options.add(defaultOptionText);
        }

        for(int idx=0;idx<jsonArray.size();idx++)
        {
            options.add(jsonArray.get(idx).getAsJsonObject().get(keyToExport).getAsString());
        }

        return options;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSION_CALL:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callToCitizen();
                }
                break;
            case REQUEST_CODE_ASK_PERMISSIONS_READ_ACCESS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImagePicker();
                }
                break;
            case REQUEST_CODE_ASK_PERMISSIONS_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                }
                break;

            default:break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkReadAccessPermission() {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS_READ_ACCESS);
            return false;
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkCallPermision() {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.CALL_PHONE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_CODE_ASK_PERMISSION_CALL);
            return false;
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkCameraPermission() {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.CAMERA);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    REQUEST_CODE_ASK_PERMISSIONS_CAMERA);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FROM_GALLERY && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            listUploadDocs.add(data.getData());
            loadOrRefreshUploadImageGrid();
        }
        else if (requestCode == PICK_PHOTO_FROM_CAMERA && resultCode == Activity.RESULT_OK) {
            try {
                File capturedImg=new File(cacheDir, "POST_IMAGE_" + imageIdxForCamera.get(0) + ".jpg");
                Uri uri = Uri.fromFile(capturedImg);
                listUploadDocs.add(uri);
                imageIdxForCamera.remove(0);
                loadOrRefreshUploadImageGrid();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }


    public void setComplaintAddressFromLatLng(final TextView textViewAddress, Double[] locationDetails)
    {
        class GetAddressFromLatLng extends AsyncTask<Double, Integer, String>
        {
            @Override
            protected String doInBackground(Double... params) {
                double lat=params[0];
                double lng=params[1];
                return LatLngAddressParser.getAddress(ViewTask.this,lat,lng);
            }

            @Override
            protected void onPostExecute(String address) {
                super.onPostExecute(address);
                textViewAddress.setText(address);
            }
        }

        new GetAddressFromLatLng().execute(locationDetails);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }




}
