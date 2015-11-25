
package com.egovernments.egov.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * POJO class,Returned by getProfile and used in updateProfile
 **/

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
     * @return The emailId
     */
    public String getEmailId() {
        return emailId;
    }

    /**
     * @return The mobileNumber
     */
    public String getMobileNumber() {
        return mobileNumber;
    }

    /**
     * @return The userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @return The altContactNumber
     */
    public String getAltContactNumber() {
        return altContactNumber;
    }

    /**
     * @return The gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * @return The panCard
     */
    public String getPanCard() {
        return panCard;
    }

    /**
     * @return The dob
     */
    public String getDob() {
        return dob;
    }

    /**
     * @return The aadhaarCard
     */
    public String getAadhaarCard() {
        return aadhaarCard;
    }

    /**
     * @return The preferredLanguage
     */
    public String getPreferredLanguage() {
        return preferredLanguage;
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
