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

package org.egovernments.egoverp.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.egovernments.egoverp.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SOSActivity extends BaseActivity {

    private static final int REQUEST_CODE_ASK_PERMISSION_CALL = 115;

    String mobileNoClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.recylerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(SOSActivity.this));

        JsonParser parser=new JsonParser();
        Gson gson=new Gson();
        ArrayList<EmergencyContact> emergencyContacts=gson.fromJson(parser.parse(loadJSONFromAsset()), new TypeToken<List<EmergencyContact>>(){}.getType());

        recyclerView.setAdapter(new SOSAdapter(SOSActivity.this, emergencyContacts));


    }


    public class SOSAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;
        ArrayList<EmergencyContact> emergencyContacts;

        public SOSAdapter(Context context, ArrayList<EmergencyContact> emergencyContacts)
        {
            this.context=context;
            this.emergencyContacts =emergencyContacts;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_sos, viewGroup, false);
            RecyclerView.ViewHolder vh = new ContactViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final EmergencyContact emergencyContact = emergencyContacts.get(position);

            final ContactViewHolder viewHolder=(ContactViewHolder)holder;

            viewHolder.tvCharacter.setText(emergencyContact.getContactName().substring(0,1).toUpperCase());
            viewHolder.tvContactName.setText(emergencyContact.getContactName());
            viewHolder.tvContactNo.setText(emergencyContact.getContactNo());

            viewHolder.layoutContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String contactNos[]=emergencyContact.getContactNo().split("/");

                    if(contactNos.length>1)
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SOSActivity.this);
                        builder.setTitle(emergencyContact.getContactName());
                        builder.setItems(contactNos, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                // Do something with the selection
                                mobileNoClicked=contactNos[item];
                                if (Build.VERSION.SDK_INT < 23) {
                                    callToEmergenyNo(mobileNoClicked);
                                } else {
                                    if (checkCallPermision()) {
                                        callToEmergenyNo(mobileNoClicked);
                                    }
                                }
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                    else {
                        mobileNoClicked = emergencyContact.getContactNo();

                        if (Build.VERSION.SDK_INT < 23) {
                            callToEmergenyNo(mobileNoClicked);
                        } else {
                            if (checkCallPermision()) {
                                callToEmergenyNo(mobileNoClicked);
                            }
                        }
                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return emergencyContacts.size();
        }

        public class ContactViewHolder extends RecyclerView.ViewHolder{

            LinearLayout layoutContact;
            TextView tvCharacter, tvContactName, tvContactNo;
            ContactViewHolder(View itemView)
            {
                super(itemView);
                layoutContact = (LinearLayout) itemView.findViewById(R.id.layout_contact);
                tvCharacter=(TextView)itemView.findViewById(R.id.tvCharacter);
                tvContactName=(TextView)itemView.findViewById(R.id.tvContactName);
                tvContactNo=(TextView)itemView.findViewById(R.id.tvContactNo);
            }

        }

    }

    public class EmergencyContact
    {
        String contactName;
        String contactNo;

        public EmergencyContact(String contactNo, String contactName) {
            this.contactNo = contactNo;
            this.contactName = contactName;
        }

        public String getContactName() {
            return contactName;
        }

        public void setContactName(String contactName) {
            this.contactName = contactName;
        }

        public String getContactNo() {
            return contactNo;
        }

        public void setContactNo(String contactNo) {
            this.contactNo = contactNo;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSION_CALL:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callToEmergenyNo(mobileNoClicked);
                }
                break;
        }
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

    public void callToEmergenyNo(String emergencyNo){

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + emergencyNo));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(intent);

    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getApplicationContext().getAssets().open("SOS.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }




}
