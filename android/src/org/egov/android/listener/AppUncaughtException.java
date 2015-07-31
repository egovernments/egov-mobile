package org.egov.android.listener;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Calendar;

import org.egov.android.AndroidLibrary;
import org.egov.android.common.StorageManager;

import android.content.Context;
import android.os.Environment;

public class AppUncaughtException implements UncaughtExceptionHandler {

    public AppUncaughtException(Context context) {
    }

    public void uncaughtException(Thread t, Throwable e) {
        final StringWriter result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        printWriter.close();
        String filename = String.valueOf(Calendar.getInstance().getTimeInMillis()) + ".txt";
        _writeToFile(stacktrace, filename);
    }

    private void _writeToFile(String stacktrace, String filename) {
        String path = "";
        StorageManager sm = new StorageManager();
        Object[] obj = sm.getStorageInfo();
        if (obj[1].toString().equals(Environment.MEDIA_MOUNTED)) {
            path = obj[0].toString()
                    + AndroidLibrary.getInstance().getConfig().getString("app.name")
                    + "/assets/log";
            sm.mkdirs(path);
            path += "/" + filename;
        }
        if (!path.equals("")) {
            try {
                BufferedWriter bos = new BufferedWriter(new FileWriter(path));
                bos.write(stacktrace);
                bos.flush();
                bos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
