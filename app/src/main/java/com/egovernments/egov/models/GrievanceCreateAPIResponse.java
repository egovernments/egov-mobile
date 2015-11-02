package com.egovernments.egov.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GrievanceCreateAPIResponse {
    @SerializedName("status")
    @Expose
    private GrievanceAPIStatus status;
    @SerializedName("result")
    @Expose
    private Grievance result;

    public GrievanceAPIStatus getStatus() {
        return status;
    }

    public Grievance getResult() {
        return result;
    }

}
