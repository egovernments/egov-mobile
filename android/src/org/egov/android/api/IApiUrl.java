package org.egov.android.api;

public interface IApiUrl {

	public String getUrl();

	public boolean isSecured();

	public String getUrl(boolean prefixWithBaseUrl);
	
	public boolean useAccessToken();
}