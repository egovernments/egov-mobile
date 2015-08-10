/**
 * eGov suite of products aim to improve the internal efficiency,transparency, accountability and the service delivery of the
 * government organizations.
 * 
 * Copyright (C) <2015> eGovernments Foundation
 * 
 * The updated version of eGov suite of products as by eGovernments Foundation is available at http://www.egovernments.org
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * http://www.gnu.org/licenses/ or http://www.gnu.org/licenses/gpl.html .
 * 
 * In addition to the terms of the GPL license to be adhered to in using this program, the following additional terms are to be
 * complied with:
 * 
 * 1) All versions of this program, verbatim or modified must carry this Legal Notice.
 * 
 * 2) Any misrepresentation of the origin of the material is prohibited. It is required that all modified versions of this
 * material be marked in reasonable ways as different from the original version.
 * 
 * 3) This license does not grant any rights to any user of the program with regards to rights under trademark law for use of the
 * trade names or trademarks of eGovernments Foundation.
 * 
 * In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.android.model;

import java.io.Serializable;

/**
 * This User Model class contains the user information.
 */

@SuppressWarnings("serial")
public class User implements Serializable {
    private String name = "";
    private String email = "";
    private String password = "";
    private String confirmPassword = "";
    private String mobileNo = "";
    private String grantType = "password";
    private String scope = "read write";
    private String gender = "";
    private String altContactNumber = "";
    private String dateOfBirth = "";
    private String panCardNumber = "";
    private String aadhaarCardNumber = "";
    private String language = "";
    private String deviceId = "";

    /**
     * returns the user name
     * 
     * @return
     */

    public String getName() {
        return name;
    }

    /**
     * sets the ComplainType Id
     */

    public void setName(String name) {
        this.name = name;
    }

    /**
     * returns the email id
     * 
     * @return
     */

    public String getEmail() {
        return email;
    }

    /**
     * sets the email id
     */

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * returns the password
     * 
     * @return
     */

    public String getPassword() {
        return password;
    }

    /**
     * sets the password
     */

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * returns the confirm password
     * 
     * @return
     */

    public String getConfirmPassword() {
        return confirmPassword;
    }

    /**
     * sets the confirm password
     */

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    /**
     * returns the mobile number
     * 
     * @return
     */

    public String getMobileNo() {
        return mobileNo;
    }

    /**
     * sets the mobile number
     */

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    /**
     * returns the grant type
     * 
     * @return
     */

    public String getGrantType() {
        return grantType;
    }

    /**
     * sets the grant type
     */

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    /**
     * returns the scope
     * 
     * @return
     */

    public String getScope() {
        return scope;
    }

    /**
     * sets the scope
     */

    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * returns the gender
     * 
     * @return
     */

    public String getGender() {
        return gender;
    }

    /**
     * sets the gender
     */

    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * returns the alternate contact number
     * 
     * @return
     */

    public String getAltContactNumber() {
        return altContactNumber;
    }

    /**
     * sets the alternate contact numbe
     */

    public void setAltContactNumber(String altContactNumber) {
        this.altContactNumber = altContactNumber;
    }

    /**
     * returns the date of birth
     * 
     * @return
     */

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * sets the date of birth
     */

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * returns the pan card number
     * 
     * @return
     */

    public String getPanCardNumber() {
        return panCardNumber;
    }

    /**
     * sets the pan card number
     * 
     * @return
     */

    public void setPanCardNumber(String panCardNumber) {
        this.panCardNumber = panCardNumber;
    }

    /**
     * returns the aadhaaar card number
     * 
     * @return
     */

    public String getAadhaarCardNumber() {
        return aadhaarCardNumber;
    }

    /**
     * sets the aadhaaar card number
     */

    public void setAadhaarCardNumber(String aadhaarCardNumber) {
        this.aadhaarCardNumber = aadhaarCardNumber;
    }

    /**
     * returns the preferred language
     * 
     * @return
     */

    public String getLanguage() {
        return language;
    }

    /**
     * sets the preferred language
     */

    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * returns the device id
     * 
     * @return
     */

    public String getDeviceId() {
        return deviceId;
    }

    /**
     * sets the device id
     */

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
