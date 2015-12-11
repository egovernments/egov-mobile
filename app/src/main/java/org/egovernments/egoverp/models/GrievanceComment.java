
package org.egovernments.egoverp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * POJO class, the comments retrieved by getComplaintHistory
 **/
public class GrievanceComment {

    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("department")
    @Expose
    private String department;
    @SerializedName("usertype")
    @Expose
    private String usertype;
    @SerializedName("updatedUserType")
    @Expose
    private String updatedUserType;
    @SerializedName("comments")
    @Expose
    private String comments;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("updatedBy")
    @Expose
    private String updatedBy;

    /**
     * @return The user
     */
    public String getUser() {
        return user;
    }

    /**
     * @return The department
     */
    public String getDepartment() {
        return department;
    }

    /**
     * @return The usertype
     */
    public String getUsertype() {
        return usertype;
    }

    /**
     * @return The updatedUserType
     */
    public String getUpdatedUserType() {
        return updatedUserType;
    }

    /**
     * @return The comments
     */
    public String getComments() {
        return comments;
    }

    /**
     * @return The date
     */
    public String getDate() {
        return date;
    }

    /**
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return The updatedBy
     */
    public String getUpdatedBy() {
        return updatedBy;
    }
}
