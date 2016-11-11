
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

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * POJO class to parse server response to getComplaints
 **/

public class Grievance implements Serializable, Parcelable {

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
    @SerializedName("complainantMobileNo")
    @Expose
    private String complainantMobileNo;
    @SerializedName("complainantEmail")
    @Expose
    private String complainantEmail;
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
    @SerializedName("supportDocs")
    @Expose
    private ArrayList<SupportDoc> supportDocs;
    @SerializedName("citizenFeedback")
    @Expose
    private String citizenFeedback;

    public Grievance(Parcel in){
        this.detail = in.readString();
        this.crn = in.readString();
        this.status = in.readString();
        this.lastModifiedBy = in.readString();
        this.lastModifiedDate = in.readString();
        this.complainantName = in.readString();
        this.complainantMobileNo=in.readString();
        this.complainantEmail=in.readString();
        this.locationName = in.readString();
        this.childLocationName = in.readString();
        this.complaintTypeId = in.readInt();
        this.complaintTypeName = in.readString();
        this.landmarkDetails = in.readString();
        this.createdDate = in.readString();
        this.supportDocsSize = in.readInt();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.citizenFeedback=in.readString();
    }



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
     * @return The complainantMobileNo
     */
    public String getComplainantMobileNo() {
        return complainantMobileNo;
    }

    /**
     * @return The complainantEmail
     */
    public String getComplainantEmail() {
        return complainantEmail;
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

    public void setLocationName(String locationName)
    {
        this.locationName=locationName;
    }

    public void setChildLocationName(String childLocationName)
    {
        this.childLocationName=childLocationName;
    }

    public void setSupportDocs(ArrayList<SupportDoc> supportDocs) {
        this.supportDocs=supportDocs;
    }

    public ArrayList<SupportDoc> getSupportDocs() {
        return supportDocs;
    }

    public String getCitizenFeedback() {
        return citizenFeedback;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.detail);
        dest.writeString(this.crn);
        dest.writeString(this.status);
        dest.writeString(this.lastModifiedBy);
        dest.writeString(this.lastModifiedDate);
        dest.writeString(this.complainantName);
        dest.writeString(this.complainantMobileNo);
        dest.writeString(this.complainantEmail);
        dest.writeString(this.locationName);
        dest.writeString(this.childLocationName);
        dest.writeInt(this.complaintTypeId);
        dest.writeString(this.complaintTypeName);
        dest.writeString(this.landmarkDetails);
        dest.writeString(this.createdDate);
        dest.writeInt(this.supportDocsSize);
        if(this.lat==null)
        {
            this.lat=0d;
            this.lng=0d;
        }
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeString(this.citizenFeedback);
    }

    public static final Creator<Grievance> CREATOR = new Creator<Grievance>() {
        public Grievance createFromParcel(Parcel in) {
            return new Grievance(in);
        }

        public Grievance[] newArray(int size) {
            return new Grievance[size];
        }
    };
}
