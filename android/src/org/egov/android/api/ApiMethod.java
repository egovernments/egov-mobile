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

package org.egov.android.api;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.egov.android.AndroidLibrary;
import org.json.JSONException;
import org.json.JSONObject;

public class ApiMethod {

    private IApiUrl apiUrl = null;
    private Map<String, String> header = null;
    private RequestMethod method = RequestMethod.GET;
    private HashMap<String, Object> queryParams;
    private String queryType = "x-www-form-urlencoded";
    private String extraParam = null;
    private String serverBaseURL=null;
    private boolean isMultiPart=false;
    private File[] uploadDocs;

    public ApiMethod() {
        this(null);
    }

    /**
     * Constructor used to set the api url, header and query parameters to the
     * api call
     * 
     * @param apiUrl
     */
    public ApiMethod(IApiUrl apiUrl) {
        this.apiUrl = apiUrl;
        header = new HashMap<String, String>();
        queryParams = new HashMap<String, Object>();
    }
    
    public ApiMethod(IApiUrl apiUrl, String serverBaseURL) {
        this.apiUrl = apiUrl;
        this.apiUrl.setBaseServer(serverBaseURL);
        this.serverBaseURL=serverBaseURL;
        header = new HashMap<String, String>();
        queryParams = new HashMap<String, Object>();
    }

    /**
     * Function used to get the full api url with parameters
     * 
     * @return
     */
    public String getFullUrl() {
    	//added condition for url with serverbaseurl or not
    	String url = (serverBaseURL!=null? serverBaseURL+this.apiUrl.getUrl() : this.apiUrl.getUrl(true));
        if (this.queryParams.size() > 0) {
            url += "?" + getQueryParameter();
        }
        return url;
    }

    /**
     * Function used to get the IApiUrl for the ApiMethod
     * 
     * @return
     */
    public IApiUrl getApiUrl() {
        return apiUrl;
    }

    /**
     * Function to set the IApiUrl to ApiMethod
     * 
     * @param apiUrl
     * @return
     */
    public ApiMethod setApiUrl(IApiUrl apiUrl) {
        this.apiUrl = apiUrl;
        return this;
    }

    /**
     * Function to add parameters send to the api call
     * 
     * @param name
     * @param value
     * @return
     */
    public ApiMethod addParameter(String name, Object value) {
        if (value != null) {
            queryParams.put(name, value);
        }
        return this;
    }

    /**
     * Function used to get the parameters send through url.
     * 
     * @return
     */
    @SuppressWarnings({ "rawtypes" })
    public String getQueryParameter() {
        StringBuilder query = new StringBuilder();
        try {
            Iterator it = queryParams.entrySet().iterator();
            if (!it.hasNext()) {
                return getAccessTokenQry();
            }
            while (it.hasNext()) {
                Map.Entry param = (Map.Entry) it.next();
                if (query.length() > 0) {
                    query.append("&");
                }
                query.append(param.getKey()).append("=");
                if (!(param.getValue() instanceof String)) {
                    query.append(param.getValue());
                } else {
                    query.append(URLEncoder.encode((String) param.getValue(), "UTF-8"));
                }

            }
        } catch (UnsupportedEncodingException e) {
        }
        return (getAccessTokenQry().length() > 0) ? getAccessTokenQry() + "&" + query.toString()
                : query.toString();
    }

    /**
     * Function used to get the parameters send through post data.
     * 
     * @return
     */
    @SuppressWarnings("rawtypes")
    public String getPostParameter() {
        JSONObject postData = new JSONObject();
        try {
            Iterator it = queryParams.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry param = (Map.Entry) it.next();
                postData.put((String) param.getKey(), param.getValue());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String postStr = postData.toString();
        if (postStr.matches(".*\"lat\":0[}|,].*")) {
            postStr = postStr.replaceFirst("\"lat\":0", "\"lat\":0.0");
        }
        if (postStr.matches(".*\"lng\":0[}|,].*")) {
            postStr = postStr.replaceFirst("\"lng\":0", "\"lng\":0.0");
        }
        return postStr;
    }

    /**
     * Function used to get access_token parameter to pass in url
     * 
     * @return
     */
    public String getAccessTokenQry() {
        if (apiUrl.useAccessToken()) {
            String accessToken = AndroidLibrary.getInstance().getSession()
                    .getString("access_token", "");
            if (!accessToken.equals("")) {
                return "access_token=" + accessToken;
            }
        }

        return "";
    }

    /**
     * Function to get header values
     * 
     * @return
     */
    public Map<String, String> getHeaders() {
        return header;
    }

    /**
     * Function to add values in header for security
     * 
     * @param key
     * @param value
     * @return
     */
    public ApiMethod addHeader(String key, String value) {
        this.header.put(key, value);
        return this;
    }

    /**
     * Function to get the request method
     * 
     * @return => get/post/put
     */
    public RequestMethod getMethod() {
        return method;
    }

    /**
     * Function to set request method
     * 
     * @param method
     * @return
     */
    public ApiMethod setMethod(RequestMethod method) {
        this.method = method;
        return this;
    }

    /**
     * Function to set, pass the parameters through url or post data
     * 
     * if the queryType is json then pass parameters through post data.
     * 
     * if the queryType is x-www-form-urlencoded then pass parameters through url.
     * 
     * @param queryType
     *            => json/x-www-form-urlencoded
     * @return
     */
    public ApiMethod setQueryType(String queryType) {
        this.queryType = queryType;
        return this;
    }

    /**
     * Function to get the query type
     * 
     * @return => json/x-www-form-urlencoded
     */
    public String getQueryType() {
        return queryType;
    }

    /**
     * Function to get extra string added with api url like id, page, etc
     * 
     * @return
     */
    public String getExtraParam() {
        if (extraParam == null) {
            return "";
        }
        return "/" + extraParam;
    }

    /**
     * Function to set extra values added in url like id, page, etc
     * 
     * @param extraParam
     */
    public void setExtraParam(String extraParam) {
        this.extraParam = extraParam;
    }

	public boolean isMultiPart() {
		return isMultiPart;
	}

	public void setMultiPart(boolean isMultiPart) {
		this.isMultiPart = isMultiPart;
	}

	public File[] getUploadDocs() {
		return uploadDocs;
	}

	public void setUploadDocs(File[] uploadDocs) {
		this.uploadDocs = uploadDocs;
	}
    
}
