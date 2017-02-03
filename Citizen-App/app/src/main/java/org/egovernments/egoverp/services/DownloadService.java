package org.egovernments.egoverp.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;

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
    String fileName = "";
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
        Call<ResponseBody> request = ApiController.getRetrofit2API(getApplicationContext()).downloadPaymentReceipt(referrerIp, receiptDownloadRequest);
        try {
            downloadFile(notificationId, request.execute().body());
        } catch (IOException e) {
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

        PendingIntent pIntent = PendingIntent.getActivity(this, 0, openFile(downloadedFile), 0);
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


    public Intent openFile(File url) {

        Uri uri = Uri.fromFile(url);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if (url.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if (url.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if (url.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if (url.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            //if you want you can also define the intent type for any other file
            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            intent.setDataAndType(uri, "*/*");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

}