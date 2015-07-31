package org.egov.android.api;

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

	public ApiMethod() {
		this(null);
	}

	public ApiMethod(IApiUrl apiUrl) {
		this.apiUrl = apiUrl;
		header = new HashMap<String, String>();
		queryParams = new HashMap<String, Object>();
	}

	public String getFullUrl() {
		String url = this.apiUrl.getUrl(true);

		if (this.queryParams.size() > 0) {
			url += "?" + getQueryParameter();
		}
		return url;
	}

	public IApiUrl getApiUrl() {
		return apiUrl;
	}

	public ApiMethod setApiUrl(IApiUrl apiUrl) {
		this.apiUrl = apiUrl;
		return this;
	}

	public ApiMethod addParameter(String name, Object value) {
		if (value != null) {
			queryParams.put(name, value);
		}
		return this;
	}

	@SuppressWarnings({ "rawtypes" })
	public String getQueryParameter() {
		StringBuilder query = new StringBuilder();
		try {
			Iterator it = queryParams.entrySet().iterator();
			if(!it.hasNext()) {
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
					query.append(URLEncoder.encode((String) param.getValue(),
							"UTF-8"));
				}

			}
		} catch (UnsupportedEncodingException e) {
		}
		return (getAccessTokenQry().length() > 0) ? getAccessTokenQry() + "&"
				+ query.toString() : query.toString();
	}

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
		if(postStr.matches(".*\"lat\":0[}|,].*")){
            postStr =  postStr.replaceFirst("\"lat\":0", "\"lat\":0.0");
        }
        if(postStr.matches(".*\"lng\":0[}|,].*")){
            postStr =  postStr.replaceFirst("\"lng\":0", "\"lng\":0.0");
        }
		return postStr;
	}

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

	public Map<String, String> getHeaders() {
		return header;
	}

	public ApiMethod addHeader(String key, String value) {
		this.header.put(key, value);
		return this;
	}

	public RequestMethod getMethod() {
		return method;
	}

	public ApiMethod setMethod(RequestMethod method) {
		this.method = method;
		return this;
	}

	public ApiMethod setQueryType(String queryType) {
		this.queryType = queryType;
		return this;
	}

	public String getQueryType() {
		return queryType;
	}

	public String getExtraParam() {
		if (extraParam == null) {
			return "";
		}
		return "/" + extraParam;
	}

	public void setExtraParam(String extraParam) {
		this.extraParam = extraParam;
	}
}
