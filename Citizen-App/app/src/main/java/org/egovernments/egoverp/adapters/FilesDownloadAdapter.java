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

package org.egovernments.egoverp.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.models.DownloadDoc;

import java.util.ArrayList;

/**
 * Created by egov on 1/8/16.
 */
public class FilesDownloadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<DownloadDoc> downloadDocs;

    public FilesDownloadAdapter(Context context, ArrayList<DownloadDoc> downloadDocs)
    {
        this.context=context;
        this.downloadDocs=downloadDocs;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_file_download, viewGroup, false);
        RecyclerView.ViewHolder vh = new DownloadFileHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final DownloadDoc downloadDoc = downloadDocs.get(position);

        final DownloadFileHolder viewHolder=(DownloadFileHolder)holder;
        viewHolder.tvFileName.setText(downloadDoc.getFileName());

        viewHolder.layoutDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "Download ->"+downloadDoc.getDownloadLink(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(downloadDoc.getDownloadLink()));
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return downloadDocs.size();
    }

    public static class DownloadFileHolder extends RecyclerView.ViewHolder{

        LinearLayout layoutDownload;
        TextView tvFileName;
        DownloadFileHolder(View itemView)
        {
            super(itemView);
            layoutDownload = (LinearLayout) itemView.findViewById(R.id.layout_download);
            tvFileName=(TextView)itemView.findViewById(R.id.tvFileName);
        }

    }

}
