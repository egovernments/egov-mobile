package org.egov.employee.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by egov on 15/4/16.
 */
public class ComplaintHistory {

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
     *
     * @return
     * The user
     */
    public String getUser() {
        return user;
    }

    /**
     *
     * @param user
     * The user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     *
     * @return
     * The department
     */
    public String getDepartment() {
        return department;
    }

    /**
     *
     * @param department
     * The department
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     *
     * @return
     * The usertype
     */
    public String getUsertype() {
        return usertype;
    }

    /**
     *
     * @param usertype
     * The usertype
     */
    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    /**
     *
     * @return
     * The updatedUserType
     */
    public String getUpdatedUserType() {
        return updatedUserType;
    }

    /**
     *
     * @param updatedUserType
     * The updatedUserType
     */
    public void setUpdatedUserType(String updatedUserType) {
        this.updatedUserType = updatedUserType;
    }

    /**
     *
     * @return
     * The comments
     */
    public String getComments() {
        return comments;
    }

    /**
     *
     * @param comments
     * The comments
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     *
     * @return
     * The date
     */
    public String getDate() {
        return date;
    }

    /**
     *
     * @param date
     * The date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The updatedBy
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     *
     * @param updatedBy
     * The updatedBy
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

}