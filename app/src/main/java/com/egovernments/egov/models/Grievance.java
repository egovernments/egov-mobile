
package com.egovernments.egov.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Grievance implements Serializable {

    @SerializedName("detail")
    @Expose
    private String detail;
    @SerializedName("crn")
    @Expose
    private String crn;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("lastModifiedBy")
    @Expose
    private String lastModifiedBy;
    @SerializedName("lastModifiedDate")
    @Expose
    private String lastModifiedDate;
    @SerializedName("complainantName")
    @Expose
    private String complainantName;
    @SerializedName("locationName")
    @Expose
    private String locationName;
    @SerializedName("complaintTypeId")
    @Expose
    private Integer complaintTypeId;
    @SerializedName("complaintTypeName")
    @Expose
    private String complaintTypeName;
    @SerializedName("complaintTypeImage")
    @Expose
    private String complaintTypeImage;
    @SerializedName("landmarkDetails")
    @Expose
    private String landmarkDetails;
    @SerializedName("createdDate")
    @Expose
    private String createdDate;
    @SerializedName("supportDocsSize")
    @Expose
    private Integer supportDocsSize;

    /**
     * @return The detail
     */
    public String getDetail() {
        return detail;
    }

    /**
     * @param detail The detail
     */
    public void setDetail(String detail) {
        this.detail = detail;
    }

    /**
     * @return The crn
     */
    public String getCrn() {
        return crn;
    }

    /**
     * @param crn The crn
     */
    public void setCrn(String crn) {
        this.crn = crn;
    }

    /**
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return The lastModifiedBy
     */
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    /**
     * @param lastModifiedBy The lastModifiedBy
     */
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    /**
     * @return The lastModifiedDate
     */
    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    /**
     * @param lastModifiedDate The lastModifiedDate
     */
    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    /**
     * @return The complainantName
     */
    public String getComplainantName() {
        return complainantName;
    }

    /**
     * @param complainantName The complainantName
     */
    public void setComplainantName(String complainantName) {
        this.complainantName = complainantName;
    }

    /**
     * @return The locationName
     */
    public String getLocationName() {
        return locationName;
    }

    /**
     * @param locationName The locationName
     */
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    /**
     * @return The complaintTypeId
     */
    public Integer getComplaintTypeId() {
        return complaintTypeId;
    }

    /**
     * @param complaintTypeId The complaintTypeId
     */
    public void setComplaintTypeId(Integer complaintTypeId) {
        this.complaintTypeId = complaintTypeId;
    }

    /**
     * @return The complaintTypeName
     */
    public String getComplaintTypeName() {
        return complaintTypeName;
    }

    /**
     * @param complaintTypeName The complaintTypeName
     */
    public void setComplaintTypeName(String complaintTypeName) {
        this.complaintTypeName = complaintTypeName;
    }

    /**
     * @return The complaintTypeImage
     */
    public String getComplaintTypeImage() {
        return complaintTypeImage;
    }

    /**
     * @param complaintTypeImage The complaintTypeImage
     */
    public void setComplaintTypeImage(String complaintTypeImage) {
        this.complaintTypeImage = complaintTypeImage;
    }

    /**
     * @return The landmarkDetails
     */
    public String getLandmarkDetails() {
        return landmarkDetails;
    }

    /**
     * @param landmarkDetails The landmarkDetails
     */
    public void setLandmarkDetails(String landmarkDetails) {
        this.landmarkDetails = landmarkDetails;
    }

    /**
     * @return The createdDate
     */
    public String getCreatedDate() {
        return createdDate;
    }

    /**
     * @param createdDate The createdDate
     */
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * @return The supportDocsSize
     */
    public Integer getSupportDocsSize() {
        return supportDocsSize;
    }

    /**
     * @param supportDocsSize The supportDocsSize
     */
    public void setSupportDocsSize(Integer supportDocsSize) {
        this.supportDocsSize = supportDocsSize;
    }

}
