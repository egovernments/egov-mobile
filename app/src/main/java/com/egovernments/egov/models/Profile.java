
package com.egovernments.egov.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Profile implements Serializable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("emailId")
    @Expose
    private String emailId;
    @SerializedName("mobileNumber")
    @Expose
    private String mobileNumber;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("altContactNumber")
    @Expose
    private String altContactNumber;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("panCard")
    @Expose
    private String panCard;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("aadhaarCard")
    @Expose
    private String aadhaarCard;
    @SerializedName("preferredLanguage")
    @Expose
    private String preferredLanguage;
    @SerializedName("pan")
    @Expose
    private String pan;
    @SerializedName("aadhaarNumber")
    @Expose
    private String aadhaarNumber;

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The emailId
     */
    public String getEmailId() {
        return emailId;
    }

    /**
     * @param emailId The emailId
     */
    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    /**
     * @return The mobileNumber
     */
    public String getMobileNumber() {
        return mobileNumber;
    }

    /**
     * @param mobileNumber The mobileNumber
     */
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    /**
     * @return The userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName The userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return The altContactNumber
     */
    public String getAltContactNumber() {
        return altContactNumber;
    }

    /**
     * @param altContactNumber The altContactNumber
     */
    public void setAltContactNumber(String altContactNumber) {
        this.altContactNumber = altContactNumber;
    }

    /**
     * @return The gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * @param gender The gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * @return The panCard
     */
    public String getPanCard() {
        return panCard;
    }

    /**
     * @param panCard The panCard
     */
    public void setPanCard(String panCard) {
        this.panCard = panCard;
    }

    /**
     * @return The dob
     */
    public String getDob() {
        return dob;
    }

    /**
     * @param dob The dob
     */
    public void setDob(String dob) {
        this.dob = dob;
    }

    /**
     * @return The aadhaarCard
     */
    public String getAadhaarCard() {
        return aadhaarCard;
    }

    /**
     * @param aadhaarCard The aadhaarCard
     */
    public void setAadhaarCard(String aadhaarCard) {
        this.aadhaarCard = aadhaarCard;
    }

    /**
     * @return The preferredLanguage
     */
    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    /**
     * @param preferredLanguage The preferredLanguage
     */
    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public Profile(String name, String emailId, String mobileNumber, String userName, String altContactNumber, String gender, String pan, String dob, String aadhaarNumber) {
        this.name = name;
        this.emailId = emailId;
        this.mobileNumber = mobileNumber;
        this.userName = userName;
        this.altContactNumber = altContactNumber;
        this.gender = gender;
        this.dob = dob;
        this.pan = pan;
        this.aadhaarNumber = aadhaarNumber;
    }
}
