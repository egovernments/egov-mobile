package org.egov.android.view.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.egov.android.AndroidLibrary;
import org.egov.android.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;
import android.net.ParseException;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class CommentsAdapter extends BaseAdapter {
	
	Context context;
	JSONArray comments;
	private static LayoutInflater inflater=null;
	
	public CommentsAdapter(Context context, JSONArray comments)
	{
		this.comments=comments;
		this.context=context;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return comments.length();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public class CommentView
    {
        TextView txtviewuser;
        TextView txtviewmessage;
        TextView txtviewtime;
        TextView txtviewstatus;
        LinearLayout layoutmessageroot;
        LinearLayout layoutmessagecontainer;
    }

	@Override
	public View getView(int position, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		CommentView commentview=new CommentView();
		View chatviewrow;
		chatviewrow = inflater.inflate(R.layout.complaint_comments_list_item, null);
		commentview.layoutmessageroot=(LinearLayout)chatviewrow.findViewById(R.id.layoutmessageroot);
		commentview.layoutmessagecontainer=(LinearLayout)chatviewrow.findViewById(R.id.layoutmessagecontainer);
		commentview.txtviewuser=(TextView)chatviewrow.findViewById(R.id.txtviewusername);
		commentview.txtviewmessage=(TextView)chatviewrow.findViewById(R.id.txtviewmessage);
		commentview.txtviewstatus=(TextView)chatviewrow.findViewById(R.id.txtviewstatus);
		commentview.txtviewtime=(TextView)chatviewrow.findViewById(R.id.txtviewcommenttime);
		
		try {
			
			JSONObject commentobj=comments.getJSONObject(position);
			
			DateFormat format = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.ENGLISH);
			String timeagotext=commentobj.getString("date");
			try {
			    Date date = format.parse(commentobj.getString("date"));
			    timeagotext = (String) DateUtils.getRelativeTimeSpanString(date.getTime(), new Date().getTime(), DateUtils.MINUTE_IN_MILLIS);
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String currentUserName=AndroidLibrary.getInstance().getSession().getString("user_name", "");
			commentview.txtviewuser.setText((currentUserName.equals(commentobj.getString("updatedBy"))?"Me":commentobj.getString("updatedBy")));
			commentview.txtviewmessage.setText(commentobj.getString("comments"));
			commentview.txtviewtime.setText(timeagotext);
			commentview.txtviewstatus.setText(commentobj.getString("status"));
			float density=Resources.getSystem().getDisplayMetrics().density;
			MarginLayoutParams params = (MarginLayoutParams) commentview.layoutmessagecontainer.getLayoutParams();
			if(commentobj.getString("updatedUserType").equals("CITIZEN"))
			{
			  commentview.layoutmessagecontainer.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.balloon_incoming_normal));
			  commentview.txtviewuser.setTextColor(context.getResources().getColor(R.color.darkblue));
			  params.rightMargin=(int) (40 * density);
			  commentview.layoutmessagecontainer.setPadding((int) (15 * density), (int) (5 * density), (int) (10 * density), (int) (10 * density));
			  commentview.layoutmessageroot.setGravity(Gravity.LEFT);
			}
			else
			{
			  commentview.layoutmessagecontainer.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.balloon_outgoing_normal));
			  commentview.txtviewuser.setTextColor(context.getResources().getColor(R.color.darkred));
			  params.leftMargin=(int) (40 * Resources.getSystem().getDisplayMetrics().density);
			  commentview.layoutmessagecontainer.setPadding((int) (10 * density), (int) (5 * density), (int) (15 * density), (int) (10 * density));
			  commentview.layoutmessageroot.setGravity(Gravity.RIGHT);
			}
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return chatviewrow;
	}

	
	
}
