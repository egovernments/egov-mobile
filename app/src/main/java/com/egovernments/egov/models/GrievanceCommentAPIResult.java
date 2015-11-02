
package com.egovernments.egov.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

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
