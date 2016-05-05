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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by egov on 15/4/16.
 */
public class ComplaintHistory {

    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("department")
    @Expose
    private String department;
    @SerializedName("usertype")
    @Expose
    private String usertype;
    @SerializedName("updatedUserType")
    @Expose
    private String updatedUserType;
    @SerializedName("comments")
    @Expose
    private String comments;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("updatedBy")
    @Expose
    private String updatedBy;

    /**
     *
     * @return
     * The user
     */
    public String getUser() {
        return user;
    }

    /**
     *
     * @param user
     * The user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     *
     * @return
     * The department
     */
    public String getDepartment() {
        return department;
    }

    /**
     *
     * @param department
     * The department
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     *
     * @return
     * The usertype
     */
    public String getUsertype() {
        return usertype;
    }

    /**
     *
     * @param usertype
     * The usertype
     */
    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    /**
     *
     * @return
     * The updatedUserType
     */
    public String getUpdatedUserType() {
        return updatedUserType;
    }

    /**
     *
     * @param updatedUserType
     * The updatedUserType
     */
    public void setUpdatedUserType(String updatedUserType) {
        this.updatedUserType = updatedUserType;
    }

    /**
     *
     * @return
     * The comments
     */
    public String getComments() {
        return comments;
    }

    /**
     *
     * @param comments
     * The comments
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     *
     * @return
     * The date
     */
    public String getDate() {
        return date;
    }

    /**
     *
     * @param date
     * The date
     */
    public void setDate(String date) {
        this.date = date;
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
     * The updatedBy
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     *
     * @param updatedBy
     * The updatedBy
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

}