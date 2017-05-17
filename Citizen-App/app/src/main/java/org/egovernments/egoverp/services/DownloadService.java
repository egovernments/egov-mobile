package org.egovernments.egoverp.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.webkit.MimeTypeMap;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.api.ApiController;
import org.egovernments.egoverp.models.Download;
import org.egovernments.egoverp.models.ReceiptDownloadRequest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Download File Service
 */


public class DownloadService extends IntentService {

    public static final String DOWNLOAD_FILE_NAME_WITH_EXT = "downloadFileNameWithExtension";
    public static final String REFERRER_IP = "referrerIp";
    public static final String ULB_CODE = "ulbCode";
    public static final String RECEIPT_NO = "receiptNo";
    public static final String REFERENCE_NO = "referenceNo";
    private String fileName = "";
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private int totalFileSize;

    public DownloadService() {
        super("Download Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        fileName = intent.getStringExtra(DOWNLOAD_FILE_NAME_WITH_EXT);

        ReceiptDownloadRequest receiptDownloadRequest = new ReceiptDownloadRequest(intent.getStringExtra(ULB_CODE),
                intent.getStringExtra(RECEIPT_NO), intent.getStringExtra(REFERENCE_NO));

        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_file_download_white_36dp)
                .setContentTitle(fileName)
                .setContentText("Downloading File")
                .setOngoing(true)
                .setAutoCancel(false);

        //for intermediate progress
        notificationBuilder.setProgress(0, 0, true);

        Calendar cal = Calendar.getInstance();

        int notifyId = cal.get(Calendar.MINUTE) + cal.get(Calendar.SECOND);

        notificationManager.notify(notifyId, notificationBuilder.build());

        initDownload(notifyId, intent.getStringExtra(REFERRER_IP), receiptDownloadRequest);

    }

    private void initDownload(int notificationId, String referrerIp, ReceiptDownloadRequest receiptDownloadRequest) {
        try {
            Call<ResponseBody> request = ApiController.getRetrofit2API(getApplicationContext()).downloadPaymentReceipt(
                    referrerIp, receiptDownloadRequest.getUlbCode(), receiptDownloadRequest.getReceiptNo(),
                    receiptDownloadRequest.getReferenceNo());
            downloadFile(notificationId, request.execute().body());
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            onDownloadFailed(notificationId);
        }
    }

    private void downloadFile(int notificationId, ResponseBody body) throws IOException {


        int count;
        byte data[] = new byte[1024 * 4];
        long fileSize = body.contentLength();
        InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);
        File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        OutputStream output = new FileOutputStream(outputFile);
        long total = 0;
        long startTime = System.currentTimeMillis();
        int timeCount = 1;
        while ((count = bis.read(data)) != -1) {

            total += count;
            totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));
            double current = Math.round(total / (Math.pow(1024, 2)));

            int progress = (int) ((total * 100) / fileSize);

            long currentTime = System.currentTimeMillis() - startTime;

            Download download = new Download();
            download.setTotalFileSize(totalFileSize);

            if (currentTime > 1000 * timeCount) {
                download.setCurrentFileSize((int) current);
                download.setProgress(progress);
                sendNotification(notificationId, download);
                timeCount++;
            }

            output.write(data, 0, count);
        }
        onDownloadComplete(notificationId, outputFile);
        output.flush();
        output.close();
        bis.close();

    }

    private void sendNotification(int notificationId, Download download) {
        /*sendIntent(download);*/
        notificationBuilder.setProgress(100, download.getProgress(), false);
        notificationBuilder.setContentText("Downloading file " + download.getCurrentFileSize() + "/" + totalFileSize + " MB");
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    /*private void sendIntent(Download download){
        Intent intent = new Intent(Activity.MESSAGE_PROGRESS);
        intent.putExtra("download",download);
        LocalBroadcastManager.getInstance(DownloadService.this).sendBroadcast(intent);
    }*/

    private void onDownloadComplete(int notificationId, File downloadedFile) {
        /*Download download = new Download();
        download.setProgress(100);
        sendIntent(download);*/

        PendingIntent pIntent = PendingIntent.getActivity(this, 0, getFileIntent(downloadedFile), 0);
        setNotificationBuilderEndStatusWithMsg("File Downloaded in Downloads Folder");
        notificationBuilder.setContentIntent(pIntent);
        Notification notification = notificationBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(notificationId, notification);
    }

    private void setNotificationBuilderEndStatusWithMsg(String msg) {
        notificationManager.cancel(0);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText(msg);
        notificationBuilder.setAutoCancel(false);
        notificationBuilder.setOngoing(false);
    }

    private void onDownloadFailed(int notificationId) {
        setNotificationBuilderEndStatusWithMsg("Download failed, please retry");
        Notification notification = notificationBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(notificationId, notification);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }


    public Intent getFileIntent(File downloadedFile) {

        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String ext = downloadedFile.getName().substring(downloadedFile.getName().lastIndexOf(".") + 1);
        String type = mime.getMimeTypeFromExtension(ext);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", downloadedFile);
            intent.setDataAndType(contentUri, type);
        } else {
            intent.setDataAndType(Uri.fromFile(downloadedFile), type);
        }
        return intent;

    }

}