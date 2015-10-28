
package com.egovernments.egov.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
     * @param hasNextPage The hasNextPage
     */
    public void setHasNextPage(String hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return The message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

}
