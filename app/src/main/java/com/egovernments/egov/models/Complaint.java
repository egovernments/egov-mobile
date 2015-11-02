package com.egovernments.egov.models;


public class Complaint {

    private int locationId;
    private double lat;
    private double lng;
    private String details;
    private int complaintTypeId;
    private String landmarkDetails;

    public Complaint(int locationId, double lat, double lng, String details, int complaintTypeId, String landmarkDetails) {
        this.locationId = locationId;
        this.lat = lat;
        this.lng = lng;
        this.details = details;
        this.complaintTypeId = complaintTypeId;
        this.landmarkDetails = landmarkDetails;
    }

    public int getLocationId() {
        return locationId;
    }

    public double getLat() {
        return lat;
    }


    public double getLng() {
        return lng;
    }

    public String getDetails() {
        return details;
    }

    public int getComplaintTypeId() {
        return complaintTypeId;
    }

    public String getLandmarkDetails() {
        return landmarkDetails;
    }
}
