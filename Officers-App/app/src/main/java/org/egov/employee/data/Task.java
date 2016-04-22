package org.egov.employee.data;

/**
 * Created by egov on 15/12/15.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Task implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("sender")
    @Expose
    private String sender;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("task")
    @Expose
    private String task;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("details")
    @Expose
    private String details;
    @SerializedName("link")
    @Expose
    private String link;

    /**
     *
     * @return
     *     The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     *     The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     *     The sender
     */
    public String getSender() {
        return sender;
    }

    /**
     *
     * @param sender
     *     The sender
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     *
     * @return
     *     The date
     */
    public String getDate() {
        return date;
    }

    /**
     *
     * @param date
     *     The date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     *
     * @return
     *     The task
     */
    public String getTask() {
        return task;
    }

    /**
     *
     * @param task
     *     The task
     */
    public void setTask(String task) {
        this.task = task;
    }

    /**
     *
     * @return
     *     The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     *     The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return
     *     The details
     */
    public String getDetails() {
        return details;
    }

    /**
     *
     * @param details
     *     The details
     */
    public void setDetails(String details) {
        this.details = details;
    }

    /**
     *
     * @return
     *     The link
     */
    public String getLink() {
        return link;
    }

    /**
     *
     * @param link
     *     The link
     */
    public void setLink(String link) {
        this.link = link;
    }

}