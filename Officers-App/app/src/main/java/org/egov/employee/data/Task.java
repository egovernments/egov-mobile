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