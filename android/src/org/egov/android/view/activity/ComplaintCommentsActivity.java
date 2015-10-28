package org.egov.android.view.activity;

import org.egov.android.R;
import org.egov.android.view.adapter.CommentsAdapter;
import org.json.JSONArray;
import org.json.JSONException;

import android.os.Bundle;
import android.widget.ListView;

public class ComplaintCommentsActivity extends BaseActivity {
	
	private JSONArray jsonarycomments;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_complaint_comments);
    	try {
			jsonarycomments=new JSONArray(getIntent().getExtras().getString("comments"));
			_loadCommentsListview(jsonarycomments);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void _loadCommentsListview(JSONArray comments)
	{
		ListView listView=(ListView)findViewById(R.id.commentslistview);
		CommentsAdapter adapter=new CommentsAdapter(ComplaintCommentsActivity.this, comments);
		listView.setAdapter(adapter);
	}
	

}
