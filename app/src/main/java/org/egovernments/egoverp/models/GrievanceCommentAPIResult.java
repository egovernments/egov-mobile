
package org.egovernments.egoverp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * POJO class, needed to resolve the list returned by getComplaintHistory
 **/

public class GrievanceCommentAPIResult {

    @SerializedName("comments")
    @Expose
    private List<GrievanceComment> grievanceComments = new ArrayList<>();

    /**
     * @return The grievanceComments
     */
    public List<GrievanceComment> getGrievanceComments() {
        return grievanceComments;
    }

}
