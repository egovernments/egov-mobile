
package org.egovernments.egoverp.models.errors;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * POJO class of an element of ErrorResponse
 **/

public class ErrorMessage {

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
