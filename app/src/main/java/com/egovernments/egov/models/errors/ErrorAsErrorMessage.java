
package com.egovernments.egov.models.errors;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ErrorAsErrorMessage {

    @SerializedName("status")
    @Expose
    private ErrorAsErrorMessageStatus errorStatus;
    @SerializedName("result")
    @Expose
    private String result;

    /**
     * @return The errorStatus
     */
    public ErrorAsErrorMessageStatus getErrorStatus() {
        return errorStatus;
    }

    /**
     * @return The result
     */
    public String getResult() {
        return result;
    }

}
