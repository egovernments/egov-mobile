package org.egov.android.view.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.egov.android.AndroidLibrary;
import org.egov.android.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;
import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
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
			
			//DateFormat format = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.ENGLISH);
			String timeagotext=commentobj.getString("date");
			timeagotext=getActualTime(timeagotext);
			
			String currentUserName=AndroidLibrary.getInstance().getSession().getString("user_name", "");
			commentview.txtviewuser.setText((currentUserName.equals(commentobj.getString("updatedBy"))?"Me":commentobj.getString("updatedBy")));
			if(!commentobj.getString("comments").equals(""))
			{
				commentview.txtviewmessage.setText(commentobj.getString("comments"));
			}
			else
			{
				commentview.txtviewmessage.setVisibility(View.GONE);
			}
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
	
	public static String getActualTime(String createdAt) {
        String dateStart = (createdAt.contains("T")) ? formatdate(createdAt) : createdAt;
        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.ENGLISH);

        Date d1 = null;
        Date d2 = new Date();

        try {
            d1 = format.parse(dateStart);
            //d2 = format.parse(current_date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        long diff = d2.getTime() - d1.getTime();
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000);
        long min = (diffHours * 60) + diffMinutes + (diffSeconds / 60);
        PrettyTime p = new PrettyTime();
        String currentTime = p.format(new Date(System.currentTimeMillis() - 1000 * 60 * min));
        if (currentTime.equalsIgnoreCase("moments from now")) {
            return currentTime.replaceAll(currentTime, "just now");
        }
        if (currentTime.equalsIgnoreCase("moments ago")) {
            return currentTime.replaceAll(currentTime, "1 minute ago");
        }
        return currentTime;
    }
	
	public static String formatdate(String datetime) {
        String s = datetime;
        String[] parts = s.split("\\.");
        String part1 = parts[0];
        String s1 = part1.replace('T', '\t');
        String[] parts1 = s1.split("\\t");
        String date = parts1[0];
        String time = parts1[1];
        String newdate = date + " " + time;
        return newdate;
    }
	

	
	
}
