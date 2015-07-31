/***
 * 
 * Org          : Neemtec
 * Created By   : Sheik
 * Date         : May 2013
 * Project      : Shoutrr 
 * Modified By  :
 * 
 */

package org.egov.android.http;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.os.AsyncTask;
import android.util.Log;

public class Uploader extends AsyncTask<Void, Integer, byte[]> {

	private static final String TAG = Uploader.class.getName();

	private final static String LINE_END = "\r\n";
	private final static String TWO_HYPHEN = "--";
	private final static String BOUNDARY = "*****";

	private String inputFile = "";
	private String outputFile = "";
	private String url = "";
	private Map<String, String> params;
	private Map<String, String> header = new HashMap<String, String>();

	HttpURLConnection con = null;

	private String loadingMessage = " Loading ...";
	private IHttpClientListener listener = null;

	public Uploader() {
		params = new HashMap<String, String>();
	}

	// ----------------------------------------------------------------------------//

	public String getUrl() {
		return url;
	}

	public Uploader setUrl(String url) {
		this.url = url;
		return this;
	}

	// ----------------------------------------------------------------------------//

	public String getLoadingMessage() {
		return loadingMessage;
	}

	public void setLoadingMessage(String loadingMessage) {
		this.loadingMessage = loadingMessage;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public Uploader addParams(String key, String value) {
		params.put(key, value);
		return this;
	}

	// ----------------------------------------------------------------------------//

	public IHttpClientListener getListener() {
		return listener;
	}

	public Uploader setListener(IHttpClientListener listener) {
		this.listener = listener;
		return this;
	}

	// ----------------------------------------------------------------------------//

	public String getInputFile() {
		return inputFile;
	}

	public Uploader setInputFile(String inputFile) {
		this.inputFile = inputFile;
		return this;
	}

	// ----------------------------------------------------------------------------//

	public String getOutputFile() {
		return outputFile;
	}

	public Uploader setOutputFile(String outputFile) {
		this.outputFile = outputFile;
		return this;
	}

	// ----------------------------------------------------------------------------//

	public Map<String, String> getHeader() {
		return header;
	}

	public Uploader addHeader(String key, String value) {
		header.put(key, value);
		return this;
	}

	// ----------------------------------------------------------------------------//
	public void upload() {
		execute();
	}

	// ----------------------------------------------------------------------------//

	private String _getUrlWidthParams() {
		if (params.isEmpty()) {
			return this.url;
		}

		String param = "?";

		Set<Entry<String, String>> paramsSet = params.entrySet();
		for (Entry<String, String> obj : paramsSet) {
			String p = "";
			try {
				p = obj.getKey() + "="
						+ URLEncoder.encode(obj.getValue(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			param += (param.length() == 1 ? p : "&" + p);
		}

		return this.url + param;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	// ----------------------------------------------------------------------------//

	@Override
	protected void onPostExecute(byte[] result) {
		super.onPostExecute(result);
		try {
			if (result == null || con.getResponseCode() != 200) {
				listener.onError(result);
			} else if (con.getResponseCode() == 200) {
				listener.onComplete(result);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ----------------------------------------------------------------------------//

	@Override
	protected byte[] doInBackground(Void... params) {
		Log.d(TAG, "=========== uploading start ...");

		Log.d(TAG, "Upload File " + this.inputFile);

		byte[] content = null;
		con = null;
		try {
			String url = _getUrlWidthParams();

			Log.d(TAG, "URL : " + url);
			con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod("POST");
			con.setUseCaches(false);
			con.setDoOutput(true);

			publishProgress(10);

			con.setRequestMethod("POST");
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + BOUNDARY);

			Set<Entry<String, String>> headerSet = header.entrySet();
			for (Entry<String, String> entry : headerSet) {
				Log.d(TAG,
						"Adding header" + entry.getKey() + "="
								+ entry.getValue());
				con.addRequestProperty(entry.getKey(), entry.getValue());
			}

			DataOutputStream dos = new DataOutputStream(con.getOutputStream());

			int pos = this.inputFile.lastIndexOf("/");
			String fn = this.inputFile.substring(pos + 1);

			dos.writeBytes(TWO_HYPHEN + BOUNDARY + LINE_END);
			dos.writeBytes("Content-Disposition: form-data; name=\"files\"; filename=\""
					+ fn + "\"" + LINE_END);
			dos.writeBytes(LINE_END);

			File f = new File(inputFile);

			Log.d(TAG,
					"================================= publish ==> Size"
							+ f.length());

			long length = f.length();

			FileInputStream fis = new FileInputStream(f);

			int count = 0;
			long total = 0;

			byte[] data = new byte[1024];
			while ((count = fis.read(data)) != -1) {
				total += count;
				dos.write(data, 0, count);
				publishProgress(10 + ((int) (total * 90 / length)));
				// Log.d(TAG,
				// "================================= publish progress starrt");
			}

			dos.writeBytes(LINE_END);
			dos.writeBytes(TWO_HYPHEN + BOUNDARY + TWO_HYPHEN + LINE_END);
			Log.d(TAG, "Uploaded file  finish send data=> ");

			publishProgress(100);

			InputStream is = null;
			if (con.getResponseCode() != 200) {
				System.out.println("Error Stream");
				is = con.getErrorStream();
			} else {
				System.out.println("Success Stream");
				is = con.getInputStream();
			}
			Log.d(TAG, "Uploaded file   input stream=> ");

			// ByteArrayOutputStream out = _readData(is, -1);

			Log.d(TAG, " === reading ----");

			/**
			 * ----------------------------------------------------------------
			 * ------------
			 * 
			 * Writing data from server
			 */
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			data = new byte[1024];
			count = 0;
			while ((count = is.read(data)) != -1) {
				out.write(data, 0, count);
			}
			is.close();
			out.flush();

			content = out.toByteArray();

			out.close();
			dos.flush();
			dos.close();
			fis.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		} catch (OutOfMemoryError ex) {
			Log.d(TAG, " == out of memory");
			ex.printStackTrace();
		} finally {
			con.disconnect();
		}
		Log.d(TAG, "================================= connection end starrt"
				+ content);
		if (content != null)
			Log.d(TAG, "======================content" + new String(content));

		return content;

	}

	// ----------------------------------------------------------------------------//

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		if (listener != null) {
			listener.onProgress(values[0]);
		}
	}

}
