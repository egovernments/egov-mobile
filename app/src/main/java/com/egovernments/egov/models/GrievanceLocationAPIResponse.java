
package com.egovernments.egov.models;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

    /**
     * @param grievanceLocation The grievanceLocation
     */
    public void setGrievanceLocation(List<GrievanceLocation> grievanceLocation) {
        this.grievanceLocation = grievanceLocation;
    }

}
