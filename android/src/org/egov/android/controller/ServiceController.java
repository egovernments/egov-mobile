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

package org.egov.android.controller;

import org.egov.android.AndroidLibrary;
import org.egov.android.common.StorageManager;
import org.egov.android.data.SQLiteHelper;
import org.egov.android.http.Downloader;
import org.egov.android.http.IHttpClientListener;
import org.egov.android.http.Uploader;
import org.egov.android.service.EgovService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.Gravity;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class ServiceController implements IHttpClientListener {

    private static ServiceController _instance = null;

    private Intent egovService;
    private boolean isServiceStarted = false;
    private boolean isBound = false;

    private boolean isNetAvailable = false;
    private boolean isJobRunning = false;

    private int id = 0;
    private String jobType = "";
    private Context ctx;
    private Messenger myService = null;
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    /**
     * This class is used to manage the upload and download jobs running on background. To avoid
     * unnecessary object creation, we have used the getInstance() function.
     * 
     * @return
     */
    public static ServiceController getInstance() {
        if (_instance == null)
            _instance = new ServiceController();
        return _instance;
    }

    /**
     * When starting an activity, this function is called. If the service is already started, then
     * no need to start it again. The service is called to run the jobs in background.
     * 
     * @param ctx
     */
    public void startService(Context ctx) {
        this.ctx = ctx;
        if (!isServiceStarted) {
            egovService = new Intent(ctx, EgovService.class);
            ctx.startService(egovService);
            isServiceStarted = true;
        }
        bindService();
    }

    /**
     * We have to bind the service to handle messages. If the service get the message correctly,
     * then only it can start the background jobs.
     */
    public void bindService() {
        if (!isBound) {
            ctx.bindService(egovService, mConnection, Context.BIND_AUTO_CREATE);
            isBound = true;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myService = new Messenger(service);
            try {
                Message msg = Message.obtain(null, 1);
                msg.replyTo = mMessenger;
                myService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            myService = null;
        }
    };

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle b = msg.getData();
            int action = b.getInt("action");
            switch (action) {
            }
        }
    }

    /**
     * After binding the service, we can send message to the service to start/stop running
     * background jobs.
     * 
     * @param action
     *            => START, STOP
     * @param b
     *            => bundle data to be send
     */
    public void sendMessageToService(int action, Bundle b) {
        if (isBound && myService != null) {
            try {
                Message msg = Message.obtain(null, action);
                msg.setData(b);
                myService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This function is called when upload/download jobs are added in 'jobs' table and when network
     * change is received.
     */
    public void startJobs() {
        sendMessageToService(EgovService.START, null);
    }

    /**
     * This function is called when no job exists in 'jobs' table and no network state is received.
     */
    public void stopJobs() {
        isJobRunning = false;
        sendMessageToService(EgovService.STOP, null);
    }

    /**
     * Function to start job from 'jobs' table. Before starting a job, we have checked the network
     * state and also another job's running state. If any one is true, don't start the job. If there
     * are no jobs in the 'jobs' table then call stopJobs() function. When starting a job, change
     * the job status to 'started'.
     */
    public void startJob() {
        if (!isNetAvailable || isJobRunning) {
            return;
        }
        JSONArray ja = SQLiteHelper
                .getInstance()
                .query("SELECT * FROM jobs WHERE status='waiting' OR (status='error' AND triedCount < 4) ORDER BY status DESC LIMIT 1");
        if (ja == null || ja.length() == 0) {
            ja = SQLiteHelper.getInstance().query(
                    "SELECT * FROM jobs WHERE status='started' ORDER BY id LIMIT 1");
            if (ja == null || ja.length() == 0) {
                stopJobs();
                return;
            }
        }

        try {
            JSONObject jo = ja.getJSONObject(0);
            id = jo.getInt("id");
            isJobRunning = true;
            jobType = jo.getString("type");
            JSONObject data = new JSONObject(jo.getString("data"));
            if (jobType.equalsIgnoreCase("upload")) {
                _upload(data);
            } else if (jobType.equalsIgnoreCase("download")) {
                _download(data);
            }

            SQLiteHelper.getInstance().execSQL("UPDATE jobs SET status='started' WHERE id = " + id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to upload the file using Uploader class.We have set the source and destination path.
     * 
     * @param data
     *            => json object contains the source and destination path
     */
    private void _upload(JSONObject data) {
        try {
            String accessToken = AndroidLibrary.getInstance().getSession()
                    .getString("access_token", "");
            Uploader upload = new Uploader();
            upload.setUrl(data.getString("url"));
            upload.setInputFile(data.getString("file"));
            upload.addParams("access_token", accessToken);
            upload.setListener(this);
            upload.upload();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to download the file using Downloader class. We have set the source and destination
     * path.
     * 
     * @param data
     *            => json object contains the source and destination path
     */
    private void _download(JSONObject data) {
        try {
            String accessToken = AndroidLibrary.getInstance().getSession()
                    .getString("access_token", "");
            Downloader downloader = new Downloader();
            downloader.setUrl(data.getString("url"));
            if (data.getString("type").equals("complaint")) {
                downloader.addParams("fileNo", data.getString("fileNo"));
            }
            downloader.addParams("access_token", accessToken);
            downloader.setDestination(data.getString("destPath"));
            downloader.setListener(this);
            downloader.start();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * When network change received by the service, call this function to set net availability.
     * 
     * @param isNetAvailable
     *            => boolean
     */
    public void setNetAvailable(Boolean isNetAvailable) {
        this.isNetAvailable = isNetAvailable;
    }

    @Override
    public void onProgress(int percent) {
        // TODO Auto-generated method stub
    }

    /**
     * On completing the jo,b delete the entry having the job id from 'jobs' table and start the
     * next job.
     */
    @Override
    public void onComplete(byte[] data) {
        //delete the entry
        SQLiteHelper.getInstance().execSQL("DELETE FROM jobs WHERE id = " + id);
        isJobRunning = false;
        startJob();
    }

    /**
     * On error we have checked the file size with device available spac. If the space is
     * insufficient then show the message else increment the tried count.
     */
    @Override
    public void onError(byte[] data) {

        StorageManager sm = new StorageManager();
        Object[] obj = sm.getStorageInfo();
        long totalSize = (Long) obj[2];
        if (jobType.equals("download")
                && totalSize < AndroidLibrary.getInstance().getConfig().getInt("upload.file.size") * 1024 * 1024) {
            SQLiteHelper.getInstance().execSQL("UPDATE jobs SET status='error' WHERE id = " + id);
            Toast toast = Toast.makeText(ctx, "There is no sufficient space in your sdcard",
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 120);
            toast.show();
            stopJobs();
            return;
        }
        JSONArray ja = SQLiteHelper.getInstance().query("SELECT * FROM jobs WHERE id = " + id);
        if (ja == null || (ja != null && ja.length() == 0)) {
            startJob();
            return;
        }
        int triedCount = 0;
        try {
            JSONObject jo = ja.getJSONObject(0);
            triedCount = jo.getInt("triedCount") + 1;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SQLiteHelper.getInstance().execSQL(
                "UPDATE jobs SET status='error', triedCount=" + triedCount + " WHERE id = " + id);
        isJobRunning = false;
        startJob();
    }
}
