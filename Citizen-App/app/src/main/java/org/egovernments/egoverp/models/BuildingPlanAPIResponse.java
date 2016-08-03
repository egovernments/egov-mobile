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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by egov on 29/7/16.
 */
public class BuildingPlanAPIResponse {


    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("response")
    @Expose
    private List<Response> response = new ArrayList<Response>();
    @SerializedName("Status")
    @Expose
    private String status;

    /**
     *
     * @return
     * The error
     */
    public String getError() {
        return error;
    }

    /**
     *
     * @param error
     * The error
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     *
     * @return
     * The response
     */
    public List<Response> getResponse() {
        return response;
    }

    /**
     *
     * @param response
     * The response
     */
    public void setResponse(List<Response> response) {
        this.response = response;
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

    public class Response {

        @SerializedName("CaseType")
        @Expose
        private String caseType;
        @SerializedName("LTPEmailID")
        @Expose
        private String lTPEmailID;
        @SerializedName("LTPMobileNo")
        @Expose
        private String lTPMobileNo;
        @SerializedName("LTPName")
        @Expose
        private String lTPName;
        @SerializedName("NatureOfSite")
        @Expose
        private String natureOfSite;
        @SerializedName("OwnerAddress")
        @Expose
        private String ownerAddress;
        @SerializedName("OwnerEmailID")
        @Expose
        private String ownerEmailID;
        @SerializedName("OwnerMobileNo")
        @Expose
        private String ownerMobileNo;
        @SerializedName("OwnerName")
        @Expose
        private String ownerName;
        @SerializedName("PermissionType")
        @Expose
        private String permissionType;
        @SerializedName("SiteAddress")
        @Expose
        private String siteAddress;
        @SerializedName("ProposalStatus")
        @Expose
        private String status;
        @SerializedName("DrawingPlain")
        @Expose
        private String drawingPlain;
        @SerializedName("ProceedingLetter")
        @Expose
        private String proceedingLetter;
        @SerializedName("ScrutinyReport")
        @Expose
        private String scrutinyReport;
        /**
         *
         * @return
         * The caseType
         */
        public String getCaseType() {
            return caseType;
        }

        /**
         *
         * @param caseType
         * The CaseType
         */
        public void setCaseType(String caseType) {
            this.caseType = caseType;
        }

        /**
         *
         * @return
         * The lTPEmailID
         */
        public String getLTPEmailID() {
            return lTPEmailID;
        }

        /**
         *
         * @param lTPEmailID
         * The LTPEmailID
         */
        public void setLTPEmailID(String lTPEmailID) {
            this.lTPEmailID = lTPEmailID;
        }

        /**
         *
         * @return
         * The lTPMobileNo
         */
        public String getLTPMobileNo() {
            return lTPMobileNo;
        }

        /**
         *
         * @param lTPMobileNo
         * The LTPMobileNo
         */
        public void setLTPMobileNo(String lTPMobileNo) {
            this.lTPMobileNo = lTPMobileNo;
        }

        /**
         *
         * @return
         * The lTPName
         */
        public String getLTPName() {
            return lTPName;
        }

        /**
         *
         * @param lTPName
         * The LTPName
         */
        public void setLTPName(String lTPName) {
            this.lTPName = lTPName;
        }

        /**
         *
         * @return
         * The natureOfSite
         */
        public String getNatureOfSite() {
            return natureOfSite;
        }

        /**
         *
         * @param natureOfSite
         * The NatureOfSite
         */
        public void setNatureOfSite(String natureOfSite) {
            this.natureOfSite = natureOfSite;
        }

        /**
         *
         * @return
         * The ownerAddress
         */
        public String getOwnerAddress() {
            return ownerAddress;
        }

        /**
         *
         * @param ownerAddress
         * The OwnerAddress
         */
        public void setOwnerAddress(String ownerAddress) {
            this.ownerAddress = ownerAddress;
        }

        /**
         *
         * @return
         * The ownerEmailID
         */
        public String getOwnerEmailID() {
            return ownerEmailID;
        }

        /**
         *
         * @param ownerEmailID
         * The OwnerEmailID
         */
        public void setOwnerEmailID(String ownerEmailID) {
            this.ownerEmailID = ownerEmailID;
        }

        /**
         *
         * @return
         * The ownerMobileNo
         */
        public String getOwnerMobileNo() {
            return ownerMobileNo;
        }

        /**
         *
         * @param ownerMobileNo
         * The OwnerMobileNo
         */
        public void setOwnerMobileNo(String ownerMobileNo) {
            this.ownerMobileNo = ownerMobileNo;
        }

        /**
         *
         * @return
         * The ownerName
         */
        public String getOwnerName() {
            return ownerName;
        }

        /**
         *
         * @param ownerName
         * The OwnerName
         */
        public void setOwnerName(String ownerName) {
            this.ownerName = ownerName;
        }

        /**
         *
         * @return
         * The permissionType
         */
        public String getPermissionType() {
            return permissionType;
        }

        /**
         *
         * @param permissionType
         * The PermissionType
         */
        public void setPermissionType(String permissionType) {
            this.permissionType = permissionType;
        }

        /**
         *
         * @return
         * The siteAddress
         */
        public String getSiteAddress() {
            return siteAddress;
        }

        /**
         *
         * @param siteAddress
         * The SiteAddress
         */
        public void setSiteAddress(String siteAddress) {
            this.siteAddress = siteAddress;
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
         * The Status
         */
        public void setStatus(String status) {
            this.status = status;
        }

        /**
         *
         * @return
         * The drawingPlain
         */
        public String getDrawingPlain() {
            return drawingPlain;
        }

        /**
         *
         * @param drawingPlain
         * The DrawingPlain
         */
        public void setDrawingPlain(String drawingPlain) {
            this.drawingPlain = drawingPlain;
        }

        /**
         *
         * @return
         * The proceedingLetter
         */
        public String getProceedingLetter() {
            return proceedingLetter;
        }

        /**
         *
         * @param proceedingLetter
         * The ProceedingLetter
         */
        public void setProceedingLetter(String proceedingLetter) {
            this.proceedingLetter = proceedingLetter;
        }

        /**
         *
         * @return
         * The scrutinyReport
         */
        public String getScrutinyReport() {
            return scrutinyReport;
        }

        /**
         *
         * @param scrutinyReport
         * The ScrutinyReport
         */
        public void setScrutinyReport(String scrutinyReport) {
            this.scrutinyReport = scrutinyReport;
        }

    }

}
