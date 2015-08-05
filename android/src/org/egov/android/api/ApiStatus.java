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

import java.util.Map;

/**
 * After the api call store the status, message and pagination. We will get these data in the
 * activity.
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
