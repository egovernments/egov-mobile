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
    private Context ctx;
    private Messenger myService = null;
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    public static ServiceController getInstance() {
        if (_instance == null)
            _instance = new ServiceController();
        return _instance;
    }

    public void startService(Context ctx) {
        this.ctx = ctx;
        if (!isServiceStarted) {
            egovService = new Intent(ctx, EgovService.class);
            ctx.startService(egovService);
            isServiceStarted = true;
        }
        bindService();
    }

    public void stopService() {
        isServiceStarted = false;
        unBindService();
    }

    public void bindService() {
        if (!isBound) {
            ctx.bindService(egovService, mConnection, Context.BIND_AUTO_CREATE);
            isBound = true;
        }
    }

    public void unBindService() {
        ctx.unbindService(mConnection);
        isBound = false;
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

    public void startJobs() {
        sendMessageToService(EgovService.START, null);
    }

    public void stopJobs() {
        isJobRunning = false;
        sendMessageToService(EgovService.STOP, null);
    }

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
            String jobType = jo.getString("type");
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

    public void setNetAvailable(Boolean isNetAvailable) {
        this.isNetAvailable = isNetAvailable;
    }

    @Override
    public void onProgress(int percent) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onComplete(byte[] data) {
        //delete the entry
        SQLiteHelper.getInstance().execSQL("DELETE FROM jobs WHERE id = " + id);
        isJobRunning = false;
        startJob();
    }

    @Override
    public void onError(byte[] data) {

        StorageManager sm = new StorageManager();
        Object[] obj = sm.getStorageInfo();
        long totalSize = (Long) obj[2];
        if (totalSize < AndroidLibrary.getInstance().getConfig().getInt("upload.file.size") * 1024 * 1024) {
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
