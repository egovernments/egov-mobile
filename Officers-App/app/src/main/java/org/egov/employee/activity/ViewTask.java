package org.egov.employee.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.egov.employee.controls.AutoHeightGridView;
import org.egov.employee.data.ComplaintHistory;
import org.egov.employee.data.Task;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import offices.org.egov.egovemployees.R;

public class ViewTask extends BaseActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private Task viewTask;

    public String GrievanceModuleName="GRIEVANCE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(viewTask.getTask());

        if(viewTask.getTask().toUpperCase().equals(GrievanceModuleName))
        {
            //initializeListenersAndComponentsForComplaint();
            String complaintNo=viewTask.getRefNum();

            /*WebView webView=(WebView)findViewById(R.id.webview);
            // Enable javascript
            webView.getSettings().setJavaScriptEnabled(true);

            // Set WebView client
            webView.setWebChromeClient(new WebChromeClient());

            webView.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });

            webView.loadUrl(preference.getActiveCityUrl()+"/pgr/rest/complaint/update/"+ complaintNo +"?access_token="+preference.getApiAccessToken());*/

            initializeListenersAndComponentsForComplaint();
        }
        else {

            List<String> categories = new ArrayList<String>();
            categories.add("Select Approver Department");
            categories.add("Accounts");
            categories.add("Administration");
            categories.add("Education");
            categories.add("Revenue");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, categories);
            dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

            Spinner spinnerdept = (Spinner) findViewById(R.id.spinnerdept);
            spinnerdept.setAdapter(dataAdapter);

            categories = new ArrayList<String>();
            categories.add("Select Approver Designation");
            categories.add("Accounts");
            categories.add("Administration");
            categories.add("Education");

            Spinner spinnerdesg = (Spinner) findViewById(R.id.spinnerdesg);
            dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, categories);
            dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinnerdesg.setAdapter(dataAdapter);

            categories = new ArrayList<String>();
            categories.add("Select Approver");
            categories.add("Vigish");
            categories.add("Dinesh");
            categories.add("Aslam");
            categories.add("Syed");

            Spinner spinnerapvr = (Spinner) findViewById(R.id.spinnerappvr);
            dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, categories);
            dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinnerapvr.setAdapter(dataAdapter);

            showSnackBar(getIntent().getExtras().getString("url"));
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

    public void initializeListenersAndComponentsForComplaint()
    {

        ProgressBar pb=(ProgressBar)findViewById(R.id.progressBar);
        ScrollView svContent=(ScrollView)findViewById(R.id.svcontent);
        LinearLayout layoutLandMark=(LinearLayout)findViewById(R.id.layoutlandmark);
        LinearLayout layoutCompComments =(LinearLayout)findViewById(R.id.complaintcommentscontainer);
        LinearLayout layoutToggleComments = (LinearLayout) findViewById(R.id.complainttogglecomments);
        final LinearLayout layoutForward=(LinearLayout)findViewById(R.id.layoutforward);

        TextView tvComplaintNo=(TextView)findViewById(R.id.tvcomplaintno);
        TextView tvComplaintDate=(TextView)findViewById(R.id.tvcomplaintdate);
        TextView tvComplainantName=(TextView)findViewById(R.id.tvcomplainantname);
        TextView tvComplaintType=(TextView)findViewById(R.id.tvcomplainttype);
        TextView tvComplaintLoc=(TextView)findViewById(R.id.tvcomplaintloc);
        TextView tvComplaintDesc=(TextView)findViewById(R.id.tvcomplaintdesc);
        TextView tvComplaintLandmark=(TextView)findViewById(R.id.tvlandmark);
        TextView tvComplaintStatus=(TextView)findViewById(R.id.tvcomplaintstatus);

        EditText etComments=(EditText)findViewById(R.id.etcomplaintcomments);

        Spinner spinnerCompStatus = (Spinner) findViewById(R.id.spinnercomplaintactions);
        Spinner spinnerForwardDept = (Spinner) findViewById(R.id.spinnerdept);
        Spinner spinnerForwardDesg = (Spinner) findViewById(R.id.spinnerdesg);
        Spinner spinnerForwardEmp = (Spinner) findViewById(R.id.spinneremp);

        final Button btnAction=(Button)findViewById(R.id.btnaction);
        Button btnMoreComments=(Button)findViewById(R.id.btnmorecomments);

        //get complaint no from task object
        String complaintNo=viewTask.getRefNum();

        tvComplaintNo.setText(complaintNo);
        tvComplaintDate.setText("24/03/2016 04:26 PM");
        tvComplainantName.setText("Dinesh S");
        tvComplaintType.setText("Burning of Garbage");
        tvComplaintLoc.setText("Anjaneya Temple Street - Election Ward No 1");
        tvComplaintDesc.setText("Someone's burning garbage's daily!");
        tvComplaintLandmark.setText("Near XYZ complex");
        tvComplaintStatus.setText("REGISTERED");


        AutoHeightGridView gridView=(AutoHeightGridView)findViewById(R.id.gridviewDocs);
        final String[] eatFoodyImages = {
                "http://i.imgur.com/rFLNqWI.jpg",
                "http://i.imgur.com/C9pBVt7.jpg",
                "http://i.imgur.com/rT5vXE1.jpg",
        };
        gridView.setAdapter(new ImageAdapter(this, Arrays.asList(eatFoodyImages)));
        gridView.setExpanded(true);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intOpenImageView=new Intent(ViewTask.this, ImageViewerActivity.class);
                intOpenImageView.putExtra("position", position);
                ArrayList<String> imageUrls=new ArrayList<String>();
                imageUrls.addAll(Arrays.asList(eatFoodyImages));
                intOpenImageView.putStringArrayListExtra("imageUrls", imageUrls);
                startActivity(intOpenImageView);
            }
        });

        /*
        "http://i.imgur.com/aIy5R2k.jpg",
                "http://i.imgur.com/MoJs9pT.jpg",
                "http://i.imgur.com/S963yEM.jpg",
                "http://i.imgur.com/rLR2cyc.jpg",
                "http://i.imgur.com/SEPdUIx.jpg",
                "http://i.imgur.com/aC9OjaM.jpg",
                "http://i.imgur.com/76Jfv9b.jpg",
                "http://i.imgur.com/fUX7EIB.jpg",
                "http://i.imgur.com/syELajx.jpg",
                "http://i.imgur.com/COzBnru.jpg",
                "http://i.imgur.com/Z3QjilA.jpg",
         */

        AutoHeightGridView gridViewUploadDocs=(AutoHeightGridView)findViewById(R.id.gridviewUploadDocs);
        String[] eatFoodyImages2 = {
                "http://i.imgur.com/MoJs9pT.jpg"
        };
        gridViewUploadDocs.setAdapter(new ImageAdapter(this, Arrays.asList(eatFoodyImages2)));
        gridViewUploadDocs.setExpanded(true);

        List<String> categories = new ArrayList<String>();
        categories.add("REGISTERED");
        categories.add("FORWARDED");
        categories.add("PROCESSING");
        categories.add("REJECTED");
        categories.add("COMPLETED");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, categories);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerCompStatus.setAdapter(dataAdapter);


        spinnerCompStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                layoutForward.setVisibility((i==1?View.VISIBLE:View.GONE));
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        categories = new ArrayList<String>();
        categories.add("SELECT DEPARTMENT");
        categories.add("Buildings");
        categories.add("Education");
        categories.add("Electrical");
        categories.add("Engineering");

        dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, categories);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerForwardDept.setAdapter(dataAdapter);

        categories = new ArrayList<String>();
        categories.add("SELECT DESIGNATION");
        categories.add("Junior engineer");
        categories.add("Assitant engineer");

        dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, categories);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerForwardDesg.setAdapter(dataAdapter);

        categories = new ArrayList<String>();
        categories.add("SELECT USER");
        categories.add("Sankar");
        categories.add("Prabu");
        categories.add("Dinesh");

        dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, categories);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerForwardEmp.setAdapter(dataAdapter);


        ArrayList<ComplaintHistory> histories=new ArrayList<>();
        histories.add(new ComplaintHistory());

        Type listType = new TypeToken<List<ComplaintHistory>>(){}.getType();
        histories= new Gson().fromJson(" [ { \"user\": \"narasappa::narasappa\", \"department\": \"Electrical\", \"usertype\": \"EMPLOYEE\", \"updatedUserType\": \"CITIZEN\", \"comments\": \"Grievance registered with Complaint Number : 00244-2016-EP\", \"date\": \"Mar 24, 2016 4:26:55 PM\", \"status\": \"REGISTERED\", \"updatedBy\": \"Tester\" } ]",listType);
        loadComplaintComments(histories, layoutToggleComments, layoutCompComments, btnMoreComments);

    }


    private void loadComplaintComments(List<ComplaintHistory> grievanceComments, LinearLayout layoutToggleComments, LinearLayout layoutCompComments,Button btnMoreComments)
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

        layoutToggleComments.removeAllViews();
        layoutCompComments.removeAllViews();


        for(int i=(grievanceComments.size()-1);i>=0;i--)
        {
            View commentItemTemplate=getLayoutInflater().inflate(R.layout.template_comment_item,null);
            ComplaintHistory comment=grievanceComments.get(i);
            TextView tvUserName=(TextView)commentItemTemplate.findViewById(R.id.commenter_name);
            tvUserName.setText(comment.getUpdatedBy());

            if (comment.getUpdatedUserType().equals("EMPLOYEE"))
                tvUserName.setTextColor(getResources().getColor(R.color.colorPrimary));

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

    public class ImageAdapter extends BaseAdapter {

        private Context mContext;
        private List<String> gridViewImages;

        public ImageAdapter(Context c, List<String> gridViewImages){
            mContext = c;
            this.gridViewImages=gridViewImages;
        }
        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ImageView imageView;
            if(view == null){
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
                //imageView.setPadding(2, 2, 2, 2);
            }
            else{
                imageView = (ImageView) view;
            }
            Picasso.with(mContext).setLoggingEnabled(true);
            Picasso.with(mContext)
                    .load(gridViewImages.get(i))
                    .error(R.drawable.ic_broken_image_white_18dp)
                    .fit()
                    .centerCrop()
                    .into(imageView);
            return imageView;
        }

        @Override
        public int getCount() {
            return gridViewImages.size();
        }


    }


}
