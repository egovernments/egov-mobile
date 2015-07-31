package org.egov.android.service;

import org.egov.android.controller.ServiceController;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

public class EgovService extends IntentService {

    public final static int START = 100;
    public final static int STOP = 200;
    public final static int START_DOWN_SYNC = 600;
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    public EgovService() {
        super("org.egov.android.service.EgovService");
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START:
                    ServiceController.getInstance().setNetAvailable(true);
                    ServiceController.getInstance().startJob();
                    break;
                case STOP:
                    ServiceController.getInstance().setNetAvailable(false);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
