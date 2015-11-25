
package com.egovernments.egov.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * POJO class, response to getComplaintLocation in autocompleteTextview of NewGrievanceActivity
 **/

public class GrievanceLocationAPIResponse {

    @SerializedName("result")
    @Expose
    private List<GrievanceLocation> grievanceLocation = new ArrayList<>();

    /**
     * @return The grievanceLocation
     */
    public List<GrievanceLocation> getGrievanceLocation() {
        return grievanceLocation;
    }

}
