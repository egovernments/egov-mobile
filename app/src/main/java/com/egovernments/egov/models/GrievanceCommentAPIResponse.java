
package com.egovernments.egov.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * POJO class, server response to getComplaintHistory
 **/

public class GrievanceCommentAPIResponse {
    @SerializedName("result")
    @Expose
    private GrievanceCommentAPIResult grievanceCommentAPIResult;

    /**
     * @return The grievanceCommentAPIResult
     */
    public GrievanceCommentAPIResult getGrievanceCommentAPIResult() {
        return grievanceCommentAPIResult;
    }

}
