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
 * This Complaint Model class contains the complaint information.
 */

@SuppressWarnings("serial")
public class Complaint implements Serializable {

    private String complaintId = "";
    private String details = "";
    private int locationId = 0;
    private String landmarkDetails = "";
    private int complaintTypeId = 0;
    private String createdDate = "";
    private double latitude = 0.0;
    private double longitute = 0.0;
    private String status = "";
    private String imagePath = "";
    private String createdBy = "";
    private boolean isPagination = false;

    /**
     * returns the ComplaintId
     * 
     * @return
     */

    public String getComplaintId() {
        return complaintId;
    }

    /**
     * sets the ComplaintId
     */

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    /**
     * returns the Complain details
     * 
     * @return
     */

    public String getDetails() {
        return details;
    }

    /**
     * sets the Complain details
     */

    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * returns the Complain location
     * 
     * @return
     */

    public int getLocationId() {
        return locationId;
    }

    /**
     * sets the Complain location
     */

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    /**
     * returns the landmark details
     * 
     * @return
     */
    public String getLandmarkDetails() {
        return landmarkDetails;
    }

    /**
     * sets the landmark details
     */

    public void setLandmarkDetails(String landmarkDetails) {
        this.landmarkDetails = landmarkDetails;
    }

    /**
     * returns the ComplaintTypeId
     * 
     * @return
     */
    public int getComplaintTypeId() {
        return complaintTypeId;
    }

    /**
     * sets the ComplaintTypeId
     */

    public void setComplaintTypeId(int complaintTypeId) {
        this.complaintTypeId = complaintTypeId;
    }

    /**
     * returns the date of created complaints
     * 
     * @return
     */

    public String getCreatedDate() {
        return createdDate;
    }

    /**
     * sets the date of created complaints
     */

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * returns the Latitude of complaint location
     * 
     * @return
     */

    public double getLatitude() {
        return latitude;
    }

    /**
     * sets the Latitude of complaint location
     */

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * returns the Longitute of complaint location
     * 
     * @return
     */
    public double getLongitute() {
        return longitute;
    }

    /**
     * sets the Longitute of complaint location
     */

    public void setLongitute(double longitute) {
        this.longitute = longitute;
    }

    /**
     * returns the complaint status.
     */

    public String getStatus() {
        return status;
    }

    /**
     * sets the complaint status.
     */

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * returns the image path.
     */

    public String getImagePath() {
        return imagePath;
    }

    /**
     * sets the image path.
     */

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * returns the name of a person who create the complaints.
     */

    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * sets the name of a person who create the complaints.
     */

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * checking whether the pagination is available or not
     */

    public boolean isPagination() {
        return isPagination;
    }

    /**
     * setting the pagination
     * 
     * @param isPagination
     */

    public void setPagination(boolean isPagination) {
        this.isPagination = isPagination;
    }
    
}
