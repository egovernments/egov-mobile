
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
    @SerializedName("childLocationName")
    @Expose
    private String childLocationName;
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
    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lng")
    @Expose
    private Double lng;

    public String getCrn() {
        return crn;
    }

    /**
     * @return The detail
     */
    public String getDetail() {
        return detail;
    }

    /**
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return The lastModifiedBy
     */
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    /**
     * @return The lastModifiedDate
     */
    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    /**
     * @return The complainantName
     */
    public String getComplainantName() {
        return complainantName;
    }

    /**
     * @return The locationName
     */
    public String getLocationName() {
        return locationName;
    }

    /**
     * @return The complaintTypeId
     */
    public Integer getComplaintTypeId() {
        return complaintTypeId;
    }

    /**
     * @return The complaintTypeName
     */
    public String getComplaintTypeName() {
        return complaintTypeName;
    }

    /**
     * @return The complaintTypeImage
     */
    public String getComplaintTypeImage() {
        return complaintTypeImage;
    }

    /**
     * @return The landmarkDetails
     */
    public String getLandmarkDetails() {
        return landmarkDetails;
    }

    /**
     * @return The createdDate
     */
    public String getCreatedDate() {
        return createdDate;
    }

    /**
     * @return The supportDocsSize
     */
    public Integer getSupportDocsSize() {
        return supportDocsSize;
    }

    public String getChildLocationName() {
        return childLocationName;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }
}
