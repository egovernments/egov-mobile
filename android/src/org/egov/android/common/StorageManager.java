package org.egov.android.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class StorageManager {

	private static final String TAG = StorageManager.class.getName();

	// ----------------------------------------------------------------------------//

	/**
	 * 
	 * @return Object[] <br/>
	 *         0 - Path <br/>
	 *         1 - State <br/>
	 *         2 - Size <br/>
	 */

	public Object[] getStorageInfo() {

		String path = this.getExternalStoragePath();

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

	// ----------------------------------------------------------------------------//
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
						|| line.contains("/mnt/obb")
						|| line.contains("/dev/mapper")
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

		return path.equals("") ? Environment.getExternalStorageDirectory()
				.getPath() : path;
	}

	// ----------------------------------------------------------------------------//
	/**
	 * 
	 * 
	 * @param path
	 * @return boolean
	 * 
	 *         true - if directories has been created successfully <br/>
	 *         false - if already exist or an error occur. <br/>
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
