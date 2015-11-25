
package com.egovernments.egov.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * POJO class, initial response to getComplaints API calls
 **/

public class GrievanceAPIResponse {

    @SerializedName("status")
    @Expose
    private GrievanceAPIStatus status;
    @SerializedName("result")
    @Expose
    private List<Grievance> result = new ArrayList<>();

    public GrievanceAPIStatus getStatus() {
        return status;
    }

    public List<Grievance> getResult() {
        return result;
    }

}
