/*
 * ******************************************************************************
 *  eGov suite of products aim to improve the internal efficiency,transparency,
 *      accountability and the service delivery of the government  organizations.
 *
 *        Copyright (C) <2016>  eGovernments Foundation
 *
 *        The updated version of eGov suite of products as by eGovernments Foundation
 *        is available at http://www.egovernments.org
 *
 *        This program is free software: you can redistribute it and/or modify
 *        it under the terms of the GNU General Public License as published by
 *        the Free Software Foundation, either version 3 of the License, or
 *        any later version.
 *
 *        This program is distributed in the hope that it will be useful,
 *        but WITHOUT ANY WARRANTY; without even the implied warranty of
 *        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *        GNU General Public License for more details.
 *
 *        You should have received a copy of the GNU General Public License
 *        along with this program. If not, see http://www.gnu.org/licenses/ or
 *        http://www.gnu.org/licenses/gpl.html .
 *
 *        In addition to the terms of the GPL license to be adhered to in using this
 *        program, the following additional terms are to be complied with:
 *
 *    	1) All versions of this program, verbatim or modified must carry this
 *    	   Legal Notice.
 *
 *    	2) Any misrepresentation of the origin of the material is prohibited. It
 *    	   is required that all modified versions of this material be marked in
 *    	   reasonable ways as different from the original version.
 *
 *    	3) This license does not grant any rights to any user of the program
 *    	   with regards to rights under trademark law for use of the trade names
 *    	   or trademarks of eGovernments Foundation.
 *
 *      In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *  *****************************************************************************
 */

package org.egov.employee.data;

/**
 * Created by egov on 15/4/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ComplaintDetails {


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
    @SerializedName("childLocationName")
    @Expose
    private String childLocationName;
    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lng")
    @Expose
    private Double lng;
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
    @SerializedName("supportDocs")
    @Expose
    private List<SupportDoc> supportDocs = new ArrayList<SupportDoc>();

    /**
     *
     * @return
     * The detail
     */
    public String getDetail() {
        return detail;
    }

    /**
     *
     * @param detail
     * The detail
     */
    public void setDetail(String detail) {
        this.detail = detail;
    }

    /**
     *
     * @return
     * The crn
     */
    public String getCrn() {
        return crn;
    }

    /**
     *
     * @param crn
     * The crn
     */
    public void setCrn(String crn) {
        this.crn = crn;
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
     * The lastModifiedBy
     */
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    /**
     *
     * @param lastModifiedBy
     * The lastModifiedBy
     */
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    /**
     *
     * @return
     * The lastModifiedDate
     */
    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    /**
     *
     * @param lastModifiedDate
     * The lastModifiedDate
     */
    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    /**
     *
     * @return
     * The complainantName
     */
    public String getComplainantName() {
        return complainantName;
    }

    /**
     *
     * @param complainantName
     * The complainantName
     */
    public void setComplainantName(String complainantName) {
        this.complainantName = complainantName;
    }

    /**
     *
     * @return
     * The childLocationName
     */
    public String getChildLocationName() {
        return childLocationName;
    }

    /**
     *
     * @param childLocationName
     * The childLocationName
     */
    public void setChildLocationName(String childLocationName) {
        this.childLocationName = childLocationName;
    }

    /**
     *
     * @return
     * The lat
     */
    public Double getLat() {
        return lat;
    }

    /**
     *
     * @param lat
     * The lat
     */
    public void setLat(Double lat) {
        this.lat = lat;
    }

    /**
     *
     * @return
     * The lng
     */
    public Double getLng() {
        return lng;
    }

    /**
     *
     * @param lng
     * The lng
     */
    public void setLng(Double lng) {
        this.lng = lng;
    }

    /**
     *
     * @return
     * The locationName
     */
    public String getLocationName() {
        return locationName;
    }

    /**
     *
     * @param locationName
     * The locationName
     */
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    /**
     *
     * @return
     * The complaintTypeId
     */
    public Integer getComplaintTypeId() {
        return complaintTypeId;
    }

    /**
     *
     * @param complaintTypeId
     * The complaintTypeId
     */
    public void setComplaintTypeId(Integer complaintTypeId) {
        this.complaintTypeId = complaintTypeId;
    }

    /**
     *
     * @return
     * The complaintTypeName
     */
    public String getComplaintTypeName() {
        return complaintTypeName;
    }

    /**
     *
     * @param complaintTypeName
     * The complaintTypeName
     */
    public void setComplaintTypeName(String complaintTypeName) {
        this.complaintTypeName = complaintTypeName;
    }

    /**
     *
     * @return
     * The complaintTypeImage
     */
    public String getComplaintTypeImage() {
        return complaintTypeImage;
    }

    /**
     *
     * @param complaintTypeImage
     * The complaintTypeImage
     */
    public void setComplaintTypeImage(String complaintTypeImage) {
        this.complaintTypeImage = complaintTypeImage;
    }

    /**
     *
     * @return
     * The landmarkDetails
     */
    public String getLandmarkDetails() {
        return landmarkDetails;
    }

    /**
     *
     * @param landmarkDetails
     * The landmarkDetails
     */
    public void setLandmarkDetails(String landmarkDetails) {
        this.landmarkDetails = landmarkDetails;
    }

    /**
     *
     * @return
     * The createdDate
     */
    public String getCreatedDate() {
        return createdDate;
    }

    /**
     *
     * @param createdDate
     * The createdDate
     */
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    /**
     *
     * @return
     * The supportDocsSize
     */
    public Integer getSupportDocsSize() {
        return supportDocsSize;
    }

    /**
     *
     * @param supportDocsSize
     * The supportDocsSize
     */
    public void setSupportDocsSize(Integer supportDocsSize) {
        this.supportDocsSize = supportDocsSize;
    }

    /**
     *
     * @return
     * The supportDocs
     */
    public List<SupportDoc> getSupportDocs() {
        return supportDocs;
    }

    /**
     *
     * @param supportDocs
     * The supportDocs
     */
    public void setSupportDocs(List<SupportDoc> supportDocs) {
        this.supportDocs = supportDocs;
    }


    public class SupportDoc {

        @SerializedName("fileId")
        @Expose
        private String fileId;
        @SerializedName("fileContentType")
        @Expose
        private String fileContentType;
        @SerializedName("fileIndexId")
        @Expose
        private String fileIndexId;

        /**
         *
         * @return
         * The fileId
         */
        public String getFileId() {
            return fileId;
        }

        /**
         *
         * @param fileId
         * The fileId
         */
        public void setFileId(String fileId) {
            this.fileId = fileId;
        }

        /**
         *
         * @return
         * The fileContentType
         */
        public String getFileContentType() {
            return fileContentType;
        }

        /**
         *
         * @param fileContentType
         * The fileContentType
         */
        public void setFileContentType(String fileContentType) {
            this.fileContentType = fileContentType;
        }

        /**
         *
         * @return
         * The fileIndexId
         */
        public String getFileIndexId() {
            return fileIndexId;
        }

        /**
         *
         * @param fileIndexId
         * The fileIndexId
         */
        public void setFileIndexId(String fileIndexId) {
            this.fileIndexId = fileIndexId;
        }

    }

}