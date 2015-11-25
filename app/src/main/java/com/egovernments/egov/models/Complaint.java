package com.egovernments.egov.models;

/**
 * POJO class used to upload grievances
 **/

public class Complaint {

    private int locationId;
    private double lat;
    private double lng;
    private String details;
    private int complaintTypeId;
    private String landmarkDetails;

    //Constructor for use with lat/lng
    public Complaint(double lat, double lng, String details, int complaintTypeId, String landmarkDetails) {

        this.lat = lat;
        this.lng = lng;
        this.details = details;
        this.complaintTypeId = complaintTypeId;
        this.landmarkDetails = landmarkDetails;
    }

    //Constructor for use with locationID
    public Complaint(int locationId, String details, int complaintTypeId, String landmarkDetails) {
        this.locationId = locationId;
        this.details = details;
        this.complaintTypeId = complaintTypeId;
        this.landmarkDetails = landmarkDetails;
    }
}
