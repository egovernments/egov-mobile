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

package org.egov.android.http;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import org.egov.android.api.SSLTrustManager;

import android.os.AsyncTask;
import android.util.Log;

/**
 * This is to upload a file.
 */

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

    HttpsURLConnection con = null;

    private String loadingMessage = " Loading ...";
    private IHttpClientListener listener = null;

    public Uploader() {
        params = new HashMap<String, String>();
    }

    /**
     * Function to set and get url.
     */

    public String getUrl() {
        return url;
    }

    public Uploader setUrl(String url) {
        this.url = url;
        return this;
    }

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

    /**
     * Function to set and get the listener.
     */

    public IHttpClientListener getListener() {
        return listener;
    }

    public Uploader setListener(IHttpClientListener listener) {
        this.listener = listener;
        return this;
    }

    public String getInputFile() {
        return inputFile;
    }

    public Uploader setInputFile(String inputFile) {
        this.inputFile = inputFile;
        return this;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public Uploader setOutputFile(String outputFile) {
        this.outputFile = outputFile;
        return this;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    /**
     * Function to add parameters
     * 
     * @param key
     * @param value
     * @return
     */

    public Uploader addHeader(String key, String value) {
        header.put(key, value);
        return this;
    }

    /**
     * Function to start upload.
     */

    public void upload() {
        execute();
    }

    /**
     * Function to add url with parameters
     */

    private String _getUrlWidthParams() {
        if (params.isEmpty()) {
            return this.url;
        }

        String param = "?";

        Set<Entry<String, String>> paramsSet = params.entrySet();
        for (Entry<String, String> obj : paramsSet) {
            String p = "";
            try {
                p = obj.getKey() + "=" + URLEncoder.encode(obj.getValue(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            param += (param.length() == 1 ? p : "&" + p);
        }

        return this.url + param;
    }

    /**
     * This method gets executed before upload starts.
     */

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /**
     * This method is the completion of the download either success or failure. If it is success,
     * the result will be set to the onComplete listener. If it is error, the result will be set to
     * the onError listener.
     */
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

    /**
     * This method runs in background. This function will be called after onPreExecute and before
     * onPostExecute.
     */

    @Override
    protected byte[] doInBackground(Void... params) {

        byte[] content = null;
        con = null;
        try {
            String url = _getUrlWidthParams();

            Log.d(TAG, "URL : " + url);

            new SSLTrustManager();
            con = (HttpsURLConnection) new URL(url).openConnection();
            con.setRequestMethod("POST");
            con.setUseCaches(false);
            con.setDoOutput(true);

            publishProgress(10);

            con.setRequestMethod("POST");
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);

            Set<Entry<String, String>> headerSet = header.entrySet();
            for (Entry<String, String> entry : headerSet) {
                Log.d(TAG, "Adding header" + entry.getKey() + "=" + entry.getValue());
                con.addRequestProperty(entry.getKey(), entry.getValue());
            }

            DataOutputStream dos = new DataOutputStream(con.getOutputStream());

            int pos = this.inputFile.lastIndexOf("/");
            String fn = this.inputFile.substring(pos + 1);

            dos.writeBytes(TWO_HYPHEN + BOUNDARY + LINE_END);
            dos.writeBytes("Content-Disposition: form-data; name=\"files\"; filename=\"" + fn
                    + "\"" + LINE_END);
            dos.writeBytes(LINE_END);

            File f = new File(inputFile);

            long length = f.length();

            FileInputStream fis = new FileInputStream(f);

            int count = 0;
            long total = 0;

            byte[] data = new byte[1024];
            while ((count = fis.read(data)) != -1) {
                total += count;
                dos.write(data, 0, count);
                publishProgress(10 + ((int) (total * 90 / length)));
            }

            dos.writeBytes(LINE_END);
            dos.writeBytes(TWO_HYPHEN + BOUNDARY + TWO_HYPHEN + LINE_END);
            publishProgress(100);

            InputStream is = null;
            if (con.getResponseCode() != 200) {
                System.out.println("Error Stream");
                is = con.getErrorStream();
            } else {
                System.out.println("Success Stream");
                is = con.getInputStream();
            }

            /**
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
        if (content != null)
            Log.d(TAG, "content=============" + content);
        return content;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (listener != null) {
            listener.onProgress(values[0]);
        }
    }

}
