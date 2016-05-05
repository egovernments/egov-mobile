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
 * Created by egov on 15/12/15.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Task implements Serializable {
    @SerializedName("refNum")
    @Expose
    private String refNum;
    @SerializedName("refDate")
    @Expose
    private String refDate;
    @SerializedName("task")
    @Expose
    private String task;
    @SerializedName("citizenName")
    @Expose
    private String citizenName;
    @SerializedName("citizenPhoneno")
    @Expose
    private String citizenPhoneno;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("resolutionDate")
    @Expose
    private String resolutionDate;
    @SerializedName("sender")
    @Expose
    private String sender;
    @SerializedName("senderPhoneno")
    @Expose
    private String senderPhoneno;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("itemDetails")
    @Expose
    private String itemDetails;
    @SerializedName("link")
    @Expose
    private String link;

    /**
     *
     * @return
     * The refNum
     */
    public String getRefNum() {
        return refNum;
    }

    /**
     *
     * @param refNum
     * The refNum
     */
    public void setRefNum(String refNum) {
        this.refNum = refNum;
    }

    /**
     *
     * @return
     * The refDate
     */
    public String getRefDate() {
        return refDate;
    }

    /**
     *
     * @param refDate
     * The refDate
     */
    public void setRefDate(String refDate) {
        this.refDate = refDate;
    }

    /**
     *
     * @return
     * The task
     */
    public String getTask() {
        return task;
    }

    /**
     *
     * @param task
     * The task
     */
    public void setTask(String task) {
        this.task = task;
    }

    /**
     *
     * @return
     * The citizenName
     */
    public String getCitizenName() {
        return citizenName;
    }

    /**
     *
     * @param citizenName
     * The citizenName
     */
    public void setCitizenName(String citizenName) {
        this.citizenName = citizenName;
    }

    /**
     *
     * @return
     * The citizenPhoneno
     */
    public String getCitizenPhoneno() {
        return citizenPhoneno;
    }

    /**
     *
     * @param citizenPhoneno
     * The citizenPhoneno
     */
    public void setCitizenPhoneno(String citizenPhoneno) {
        this.citizenPhoneno = citizenPhoneno;
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
     * The resolutionDate
     */
    public String getResolutionDate() {
        return resolutionDate;
    }

    /**
     *
     * @param resolutionDate
     * The resolutionDate
     */
    public void setResolutionDate(String resolutionDate) {
        this.resolutionDate = resolutionDate;
    }

    /**
     *
     * @return
     * The sender
     */
    public String getSender() {
        return sender;
    }

    /**
     *
     * @param sender
     * The sender
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     *
     * @return
     * The senderPhoneno
     */
    public String getSenderPhoneno() {
        return senderPhoneno;
    }

    /**
     *
     * @param senderPhoneno
     * The senderPhoneno
     */
    public void setSenderPhoneno(String senderPhoneno) {
        this.senderPhoneno = senderPhoneno;
    }

    /**
     *
     * @return
     * The location
     */
    public String getLocation() {
        return location;
    }

    /**
     *
     * @param location
     * The location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     *
     * @return
     * The itemDetails
     */
    public String getItemDetails() {
        return itemDetails;
    }

    /**
     *
     * @param itemDetails
     * The itemDetails
     */
    public void setItemDetails(String itemDetails) {
        this.itemDetails = itemDetails;
    }

    /**
     *
     * @return
     * The link
     */
    public String getLink() {
        return link;
    }

    /**
     *
     * @param link
     * The link
     */
    public void setLink(String link) {
        this.link = link;
    }


}