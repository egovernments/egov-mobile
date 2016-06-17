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
 * Created by egov on 2/5/16.
 */
public class SearchResultItem {

    @SerializedName("index")
    @Expose
    private String index;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("correlationId")
    @Expose
    private String correlationId;
    @SerializedName("resource")
    @Expose
    private Resource resource;

    /**
     *
     * @return
     * The index
     */
    public String getIndex() {
        return index;
    }

    /**
     *
     * @param index
     * The index
     */
    public void setIndex(String index) {
        this.index = index;
    }

    /**
     *
     * @return
     * The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     * The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return
     * The correlationId
     */
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     *
     * @param correlationId
     * The correlationId
     */
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    /**
     *
     * @return
     * The resource
     */
    public Resource getResource() {
        return resource;
    }

    /**
     *
     * @param resource
     * The resource
     */
    public void setResource(Resource resource) {
        this.resource = resource;
    }


    public class Resource {

        @SerializedName("common")
        @Expose
        private Common common;
        @SerializedName("clauses")
        @Expose
        private Clauses clauses;
        @SerializedName("searchable")
        @Expose
        private Searchable searchable;

        /**
         *
         * @return
         * The common
         */
        public Common getCommon() {
            return common;
        }

        /**
         *
         * @param common
         * The common
         */
        public void setCommon(Common common) {
            this.common = common;
        }

        /**
         *
         * @return
         * The clauses
         */
        public Clauses getClauses() {
            return clauses;
        }

        /**
         *
         * @param clauses
         * The clauses
         */
        public void setClauses(Clauses clauses) {
            this.clauses = clauses;
        }

        /**
         *
         * @return
         * The searchable
         */
        public Searchable getSearchable() {
            return searchable;
        }

        /**
         *
         * @param searchable
         * The searchable
         */
        public void setSearchable(Searchable searchable) {
            this.searchable = searchable;
        }

    }


    public class Common {

        @SerializedName("boundary")
        @Expose
        private Boundary boundary;
        @SerializedName("citizen")
        @Expose
        private Citizen citizen;
        @SerializedName("createdDate")
        @Expose
        private String createdDate;
        @SerializedName("locationBoundary")
        @Expose
        private LocationBoundary locationBoundary;

        /**
         *
         * @return
         * The boundary
         */
        public Boundary getBoundary() {
            return boundary;
        }

        /**
         *
         * @param boundary
         * The boundary
         */
        public void setBoundary(Boundary boundary) {
            this.boundary = boundary;
        }

        /**
         *
         * @return
         * The citizen
         */
        public Citizen getCitizen() {
            return citizen;
        }

        /**
         *
         * @param citizen
         * The citizen
         */
        public void setCitizen(Citizen citizen) {
            this.citizen = citizen;
        }

        /**
         *
         * @return
         * The createdDate
         */
        public String getCreatedDate() {
            return createdDate;
        }

        /**
         *
         * @param createdDate
         * The createdDate
         */
        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        /**
         *
         * @return
         * The locationBoundary
         */
        public LocationBoundary getLocationBoundary() {
            return locationBoundary;
        }

        /**
         *
         * @param locationBoundary
         * The locationBoundary
         */
        public void setLocationBoundary(LocationBoundary locationBoundary) {
            this.locationBoundary = locationBoundary;
        }



    }

    public class Citizen {

        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("mobile")
        @Expose
        private String mobile;
        @SerializedName("email")
        @Expose
        private String email;

        /**
         *
         * @return
         * The name
         */
        public String getName() {
            return name;
        }

        /**
         *
         * @param name
         * The name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         *
         * @return
         * The mobile
         */
        public String getMobile() {
            return mobile;
        }

        /**
         *
         * @param mobile
         * The mobile
         */
        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        /**
         *
         * @return
         * The email
         */
        public String getEmail() {
            return email;
        }

        /**
         *
         * @param email
         * The email
         */
        public void setEmail(String email) {
            this.email = email;
        }

    }

    public class Boundary {

        @SerializedName("createdDate")
        @Expose
        private String createdDate;
        @SerializedName("name")
        @Expose
        private String name;

        /**
         *
         * @return
         * The createdDate
         */
        public String getCreatedDate() {
            return createdDate;
        }

        /**
         *
         * @param createdDate
         * The createdDate
         */
        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        /**
         *
         * @return
         * The name
         */
        public String getName() {
            return name;
        }

        /**
         *
         * @param name
         * The name
         */
        public void setName(String name) {
            this.name = name;
        }

    }

    public class ComplaintType {

        @SerializedName("createdDate")
        @Expose
        private String createdDate;
        @SerializedName("name")
        @Expose
        private String name;

        /**
         *
         * @return
         * The createdDate
         */
        public String getCreatedDate() {
            return createdDate;
        }

        /**
         *
         * @param createdDate
         * The createdDate
         */
        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        /**
         *
         * @return
         * The name
         */
        public String getName() {
            return name;
        }

        /**
         *
         * @param name
         * The name
         */
        public void setName(String name) {
            this.name = name;
        }

    }

    public class Department {

        @SerializedName("createdDate")
        @Expose
        private String createdDate;
        @SerializedName("name")
        @Expose
        private String name;

        /**
         *
         * @return
         * The createdDate
         */
        public String getCreatedDate() {
            return createdDate;
        }

        /**
         *
         * @param createdDate
         * The createdDate
         */
        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        /**
         *
         * @return
         * The name
         */
        public String getName() {
            return name;
        }

        /**
         *
         * @param name
         * The name
         */
        public void setName(String name) {
            this.name = name;
        }

    }

    public class Citydetails {

        @SerializedName("code")
        @Expose
        private String code;
        @SerializedName("districtCode")
        @Expose
        private String districtCode;
        @SerializedName("createdDate")
        @Expose
        private String createdDate;
        @SerializedName("districtName")
        @Expose
        private String districtName;
        @SerializedName("regionName")
        @Expose
        private String regionName;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("domainURL")
        @Expose
        private String domainURL;

        /**
         *
         * @return
         * The code
         */
        public String getCode() {
            return code;
        }

        /**
         *
         * @param code
         * The code
         */
        public void setCode(String code) {
            this.code = code;
        }

        /**
         *
         * @return
         * The districtCode
         */
        public String getDistrictCode() {
            return districtCode;
        }

        /**
         *
         * @param districtCode
         * The districtCode
         */
        public void setDistrictCode(String districtCode) {
            this.districtCode = districtCode;
        }

        /**
         *
         * @return
         * The createdDate
         */
        public String getCreatedDate() {
            return createdDate;
        }

        /**
         *
         * @param createdDate
         * The createdDate
         */
        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        /**
         *
         * @return
         * The districtName
         */
        public String getDistrictName() {
            return districtName;
        }

        /**
         *
         * @param districtName
         * The districtName
         */
        public void setDistrictName(String districtName) {
            this.districtName = districtName;
        }

        /**
         *
         * @return
         * The regionName
         */
        public String getRegionName() {
            return regionName;
        }

        /**
         *
         * @param regionName
         * The regionName
         */
        public void setRegionName(String regionName) {
            this.regionName = regionName;
        }

        /**
         *
         * @return
         * The name
         */
        public String getName() {
            return name;
        }

        /**
         *
         * @param name
         * The name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         *
         * @return
         * The domainURL
         */
        public String getDomainURL() {
            return domainURL;
        }

        /**
         *
         * @param domainURL
         * The domainURL
         */
        public void setDomainURL(String domainURL) {
            this.domainURL = domainURL;
        }

    }

    public class LocationBoundary {

        @SerializedName("createdDate")
        @Expose
        private String createdDate;
        @SerializedName("name")
        @Expose
        private String name;

        /**
         *
         * @return
         * The createdDate
         */
        public String getCreatedDate() {
            return createdDate;
        }

        /**
         *
         * @param createdDate
         * The createdDate
         */
        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        /**
         *
         * @return
         * The name
         */
        public String getName() {
            return name;
        }

        /**
         *
         * @param name
         * The name
         */
        public void setName(String name) {
            this.name = name;
        }

    }


    public class Searchable {

        @SerializedName("owner")
        @Expose
        private Owner owner;
        @SerializedName("landmarkDetails")
        @Expose
        private String landmarkDetails;
        @SerializedName("isClosed")
        @Expose
        private Boolean isClosed;
        @SerializedName("complaintType")
        @Expose
        private ComplaintType complaintType;
        @SerializedName("complaintDuration")
        @Expose
        private Double complaintDuration;
        @SerializedName("details")
        @Expose
        private String details;
        @SerializedName("durationRange")
        @Expose
        private String durationRange;

        /**
         *
         * @return
         * The owner
         */
        public Owner getOwner() {
            return owner;
        }

        /**
         *
         * @param owner
         * The owner
         */
        public void setOwner(Owner owner) {
            this.owner = owner;
        }

        /**
         *
         * @return
         * The landmarkDetails
         */
        public String getLandmarkDetails() {
            return landmarkDetails;
        }

        /**
         *
         * @param landmarkDetails
         * The landmarkDetails
         */
        public void setLandmarkDetails(String landmarkDetails) {
            this.landmarkDetails = landmarkDetails;
        }

        /**
         *
         * @return
         * The isClosed
         */
        public Boolean getIsClosed() {
            return isClosed;
        }

        /**
         *
         * @param isClosed
         * The isClosed
         */
        public void setIsClosed(Boolean isClosed) {
            this.isClosed = isClosed;
        }

        /**
         *
         * @return
         * The complaintType
         */
        public ComplaintType getComplaintType() {
            return complaintType;
        }

        /**
         *
         * @param complaintType
         * The complaintType
         */
        public void setComplaintType(ComplaintType complaintType) {
            this.complaintType = complaintType;
        }

        /**
         *
         * @return
         * The complaintDuration
         */
        public Double getComplaintDuration() {
            return complaintDuration;
        }

        /**
         *
         * @param complaintDuration
         * The complaintDuration
         */
        public void setComplaintDuration(Double complaintDuration) {
            this.complaintDuration = complaintDuration;
        }

        /**
         *
         * @return
         * The details
         */
        public String getDetails() {
            return details;
        }

        /**
         *
         * @param details
         * The details
         */
        public void setDetails(String details) {
            this.details = details;
        }

        /**
         *
         * @return
         * The durationRange
         */
        public String getDurationRange() {
            return durationRange;
        }

        /**
         *
         * @param durationRange
         * The durationRange
         */
        public void setDurationRange(String durationRange) {
            this.durationRange = durationRange;
        }

    }

    public class Owner {

        @SerializedName("createdDate")
        @Expose
        private String createdDate;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("id")
        @Expose
        private Integer id;

        /**
         *
         * @return
         * The createdDate
         */
        public String getCreatedDate() {
            return createdDate;
        }

        /**
         *
         * @param createdDate
         * The createdDate
         */
        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        /**
         *
         * @return
         * The name
         */
        public String getName() {
            return name;
        }

        /**
         *
         * @param name
         * The name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         *
         * @return
         * The id
         */
        public Integer getId() {
            return id;
        }

        /**
         *
         * @param id
         * The id
         */
        public void setId(Integer id) {
            this.id = id;
        }

    }

    public class Status {

        @SerializedName("name")
        @Expose
        private String name;

        /**
         *
         * @return
         * The name
         */
        public String getName() {
            return name;
        }

        /**
         *
         * @param name
         * The name
         */
        public void setName(String name) {
            this.name = name;
        }

    }

    public class Clauses {

        @SerializedName("receivingMode")
        @Expose
        private String receivingMode;
        @SerializedName("department")
        @Expose
        private Department department;
        @SerializedName("citydetails")
        @Expose
        private Citydetails citydetails;
        @SerializedName("crn")
        @Expose
        private String crn;
        @SerializedName("status")
        @Expose
        private Status status;

        /**
         *
         * @return
         * The receivingMode
         */
        public String getReceivingMode() {
            return receivingMode;
        }

        /**
         *
         * @param receivingMode
         * The receivingMode
         */
        public void setReceivingMode(String receivingMode) {
            this.receivingMode = receivingMode;
        }

        /**
         *
         * @return
         * The department
         */
        public Department getDepartment() {
            return department;
        }

        /**
         *
         * @param department
         * The department
         */
        public void setDepartment(Department department) {
            this.department = department;
        }

        /**
         *
         * @return
         * The citydetails
         */
        public Citydetails getCitydetails() {
            return citydetails;
        }

        /**
         *
         * @param citydetails
         * The citydetails
         */
        public void setCitydetails(Citydetails citydetails) {
            this.citydetails = citydetails;
        }

        /**
         *
         * @return
         * The crn
         */
        public String getCrn() {
            return crn;
        }

        /**
         *
         * @param crn
         * The crn
         */
        public void setCrn(String crn) {
            this.crn = crn;
        }

        /**
         *
         * @return
         * The status
         */
        public Status getStatus() {
            return status;
        }

        /**
         *
         * @param status
         * The status
         */
        public void setStatus(Status status) {
            this.status = status;
        }

    }



}
