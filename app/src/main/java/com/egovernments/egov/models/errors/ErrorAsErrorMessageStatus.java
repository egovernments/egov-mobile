
package com.egovernments.egov.models.errors;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * POJO class of an element of ErrorAsErrorMessage
 **/

public class ErrorAsErrorMessageStatus {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("message")
    @Expose
    private String message;

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @return The message
     */
    public String getMessage() {
        return message;
    }

}
