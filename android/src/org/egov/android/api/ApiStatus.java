package org.egov.android.api;

import java.util.Map;

/**
 * 
 * @author nihalsheik
 * @version 1.0
 * @since 2015
 *
 */
public class ApiStatus {

	public static final String SUCCESS = "success";

	public static final String DATA_PARSING_ERROR = "data_parsing_error";

	public static final String HTTP_ERROR = "http_error";

	public static boolean isError = false;

	private String status = "success";
	private String message = "";
	private String isPagination = "";
	private Map<String, ApiStatus> detail = null;

	public ApiStatus() {
	}

	public ApiStatus(String status, String message, String isPagination) {
		this.status = status;
		this.message = message;
		this.isPagination = isPagination;
	}

	public boolean hasError() {
		return !SUCCESS.equals(this.status);
	}

	public String getStatus() {
		return this.status;
	}

	public String getMessage() {
		return this.message;
	}

	public Map<String, ApiStatus> getDetail() {
		return detail;
	}

	public void setDetail(Map<String, ApiStatus> detail) {
		this.detail = detail;
	}

	public String isPagination() {
		return this.isPagination;
	}
}
