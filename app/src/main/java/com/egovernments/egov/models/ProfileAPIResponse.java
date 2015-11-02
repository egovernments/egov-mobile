
package com.egovernments.egov.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileAPIResponse {

    @SerializedName("result")
    @Expose
    private Profile profile;

    /**
     * @return The profile
     */
    public Profile getProfile() {
        return profile;
    }

}
