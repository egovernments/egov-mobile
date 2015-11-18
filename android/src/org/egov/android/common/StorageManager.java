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

package org.egov.android.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

/**
 * It is to get the device storage path and its size.
 */

public class StorageManager {

    private static final String TAG = StorageManager.class.getName();

    public Object[] getStorageInfo(Context context) {

    	
    	if(context instanceof Activity)
    	{
    		context=context.getApplicationContext();
    	}
    	
    	//get external storage cache dir
    	File cacheDir=context.getExternalCacheDir();
    	
    	//check whether external cache dir is available or not
    	if(cacheDir == null)
    	{
    		//get internal cache dir
    		cacheDir=context.getCacheDir();
    	}
    	
        String path = cacheDir.getAbsolutePath(); //this.getExternalStoragePath();

        Object[] result = new Object[] { "", "", 0, "" };

        result[0] = path;

        long blockSize = 0;
        long availableBlock = 0;
        long size = 0;
        if (path.equals("")) {
            return result;
        }

        File f = new File(path);

        if (!f.canWrite()) {
            result[1] = Environment.MEDIA_MOUNTED_READ_ONLY;
        } else {
            StatFs stat = new StatFs(path);
            blockSize = stat.getBlockSize();
            availableBlock = stat.getAvailableBlocks();
            size = blockSize * availableBlock;
            result[1] = Environment.getExternalStorageState();
        }

        result[2] = size;
        result[3] = Environment.getExternalStorageDirectory().getPath();

        Log.d(TAG, result[0].toString());
        Log.d(TAG, result[1].toString());
        Log.d(TAG, result[2].toString());
        Log.d(TAG, result[3].toString());
        return result;
    }

    /**
     * 
     * @return path
     * 
     */
    public String getExternalStoragePath() {
        String path = "";
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader("/proc/mounts"));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("/mnt/secure") || line.contains("/mnt/asec")
                        || line.contains("/mnt/obb") || line.contains("/dev/mapper")
                        || line.contains("tmpfs")) {
                    continue;
                }
                if (line.contains("vfat") || line.contains("/mnt")) {
                    StringTokenizer tokens = new StringTokenizer(line, " ");
                    String s = tokens.nextToken();
                    s = tokens.nextToken();
                    if (line.contains("/dev/block/vold")) {
                        File file = new File(s);
                        if (!file.canWrite()) {
                            path = "";
                        } else {
                            path = s;
                        }
                    }
                }
            }

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            try {
                br.close();
            } catch (IOException e) {
            }
        }

        return path.equals("") ? Environment.getExternalStorageDirectory().getPath() : path;
    }

    /**
     * @param path
     * @return boolean
     * 
     *         true - if directories has been created successfully; false - if already exist or an
     *         error occur.
     * 
     */

    public boolean mkdirs(String path) {
        File f = new File(path);
        if (f.exists()) {
            Log.d(TAG, "path exist ==>");
            return false;
        }
        return f.mkdirs();
    }

}
