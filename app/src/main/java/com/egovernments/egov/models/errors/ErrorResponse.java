
package com.egovernments.egov.models.errors;

/**
 * POJO class to parse server error messages. Cannot parse all error messages as they are inconsistent
 **/

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ErrorResponse {

    @SerializedName("status")
    @Expose
    private ErrorMessage errorStatus;
    @SerializedName("result")
    @Expose
    private String result;

    /**
     * @return The errorStatus
     */
    public ErrorMessage getErrorStatus() {
        return errorStatus;
    }

    /**
     * @return The result
     */
    public String getResult() {
        return result;
    }

}
