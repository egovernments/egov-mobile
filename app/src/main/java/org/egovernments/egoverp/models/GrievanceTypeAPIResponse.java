
package org.egovernments.egoverp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * POJO class, response to getComplaintType, used in spinner of NewGrievanceActivity
 **/

public class GrievanceTypeAPIResponse {

    @SerializedName("result")
    @Expose
    private List<GrievanceType> grievanceType = new ArrayList<>();

    /**
     * @return The complaintType
     */
    public List<GrievanceType> getGrievanceType() {
        return grievanceType;
    }

}
