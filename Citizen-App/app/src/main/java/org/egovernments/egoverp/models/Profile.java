
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
    @SerializedName("dob")
    @Expose
    private String dob;
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

    public String getPan() {
        return pan;
    }

    /**
     * @return The dob
     */
    public String getDob() {
        return dob;
    }

    public String getAadhaarNumber() {
        return aadhaarNumber;
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
