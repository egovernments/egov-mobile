package org.egov.android.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

import org.egov.android.api.ApiStatus;
import org.egov.android.api.SSLTrustManager;

import android.util.Log;
 
/**
 * This utility class provides an abstraction layer for sending multipart HTTP
 * POST requests to a web server.
 * @author eGovernments Foundation
 *
 */
public class MultipartUtility {
    private final String boundary;
    private static final String LINE_FEED = "\r\n";
    private final static String TAG = MultipartUtility.class.getName();
    private HttpURLConnection httpConn;
    private String charset;
    private OutputStream outputStream;
    private PrintWriter writer;
 
    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data
     * @param requestURL
     * @param charset
     * @throws IOException
     */
    public MultipartUtility(String requestURL, String charset)
            throws IOException {
        this.charset = charset;
         
        // creates a unique boundary based on time stamp
        boundary = "*****";
         
        URL url = new URL(requestURL);
        if (requestURL.startsWith("https://")) {
		   new SSLTrustManager();
	       httpConn = (HttpsURLConnection) url.openConnection();
		}
		else
		{
	       httpConn = (HttpURLConnection) url.openConnection();
		}
        
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // indicates POST method
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary);
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                true);
    }
 
    /**
     * Adds a form field to the request
     * @param name field name
     * @param value field value
     */
    public void addFormField(String name, String value) {
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                .append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + charset).append(
                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }
 
    /**
     * Adds a upload file section to the request
     * @param fieldName name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(String fieldName, File uploadFile)
            throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: "
                        + URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();
 
        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();
         
        writer.append(LINE_FEED);
        writer.flush();    
    }
 
    /**
     * Adds a header field to the request.
     * @param name - name of the header field
     * @param value - value of the header field
     */
    public void addHeaderField(String name, String value) {
        writer.append(name + ": " + value).append(LINE_FEED);
        writer.flush();
    }
     
    /**
     * Completes the request and receives response from the server.
     * @return a list of Strings as response in case the server returned
     * status OK, otherwise an exception is thrown.
     * @throws IOException
     */
    public String finish() throws IOException {
 
        writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();
        
        String response = "";
 
        // checks server's status code first        
        InputStream is = null;
		int status = 0;
		try {
			status = httpConn.getResponseCode();
		} catch (IOException e) {
			status = httpConn.getResponseCode();
		}
		Log.d(TAG, "========status===========" + status);
		String encoding = httpConn.getContentEncoding() == null ? "" : httpConn
				.getContentEncoding();

		if (status == 200 || status == 201) {
			is = encoding.equalsIgnoreCase("gzip") ? new GZIPInputStream(
					httpConn.getInputStream()) : httpConn.getInputStream();
		} else {
			is = encoding.equalsIgnoreCase("gzip") ? new GZIPInputStream(
					httpConn.getErrorStream()) : httpConn.getErrorStream();
			ApiStatus.isError = true;
		}

		InputStreamReader input = new InputStreamReader(is);

		char[] data = new char[1024];
		int count = 0;
		StringBuffer sb = new StringBuffer();
		while ((count = input.read(data)) != -1) {
			sb.append(data, 0, count);
		}
		input.close();
		response = sb.toString();
        
        /*if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    httpConn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                response.add(line);
            }
            reader.close();
            httpConn.disconnect();
        } else {
            throw new IOException("Server returned non-OK status: " + status);
        }*/
 
        return response;
    }
}
