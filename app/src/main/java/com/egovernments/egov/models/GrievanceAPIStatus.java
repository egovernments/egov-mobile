
package com.egovernments.egov.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * POJO class, the status response of getComplaints. Used only for hasNextPage value in pagination of grievance activity recyclerview
 **/

public class GrievanceAPIStatus {

    @SerializedName("hasNextPage")
    @Expose
    private String hasNextPage;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("message")
    @Expose
    private String message;

    /**
     * @return The hasNextPage
     */
    public String getHasNextPage() {
        return hasNextPage;
    }

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
