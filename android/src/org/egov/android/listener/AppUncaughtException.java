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
