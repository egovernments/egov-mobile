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

package org.egovernments.egoverp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by egov on 9/8/16.
 */
public class BuildingPenalizationAPIResponse {

    @SerializedName("ApplicationNo")
    @Expose
    private String applicationNo;
    @SerializedName("ApplicantName")
    @Expose
    private String applicantName;
    @SerializedName("MobileNo")
    @Expose
    private String mobileNo;
    @SerializedName("CurrentStatus")
    @Expose
    private String currentStatus;
    @SerializedName("StatusID")
    @Expose
    private Integer statusID;
    @SerializedName("DocType")
    @Expose
    private String docType;
    @SerializedName("DocPath")
    @Expose
    private String docPath;

    /**
     *
     * @return
     * The applicationNo
     */
    public String getApplicationNo() {
        return applicationNo;
    }

    /**
     *
     * @param applicationNo
     * The ApplicationNo
     */
    public void setApplicationNo(String applicationNo) {
        this.applicationNo = applicationNo;
    }

    /**
     *
     * @return
     * The applicantName
     */
    public String getApplicantName() {
        return applicantName;
    }

    /**
     *
     * @param applicantName
     * The ApplicantName
     */
    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    /**
     *
     * @return
     * The mobileNo
     */
    public String getMobileNo() {
        return mobileNo;
    }

    /**
     *
     * @param mobileNo
     * The MobileNo
     */
    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    /**
     *
     * @return
     * The currentStatus
     */
    public String getCurrentStatus() {
        return currentStatus;
    }

    /**
     *
     * @param currentStatus
     * The CurrentStatus
     */
    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    /**
     *
     * @return
     * The statusID
     */
    public Integer getStatusID() {
        return statusID;
    }

    /**
     *
     * @param statusID
     * The StatusID
     */
    public void setStatusID(Integer statusID) {
        this.statusID = statusID;
    }

    /**
     *
     * @return
     * The docType
     */
    public String getDocType() {
        return docType;
    }

    /**
     *
     * @param docType
     * The DocType
     */
    public void setDocType(String docType) {
        this.docType = docType;
    }

    /**
     *
     * @return
     * The docPath
     */
    public String getDocPath() {
        return docPath;
    }

    /**
     *
     * @param docPath
     * The DocPath
     */
    public void setDocPath(String docPath) {
        this.docPath = docPath;
    }

}
