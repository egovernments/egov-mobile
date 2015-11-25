
package com.egovernments.egov.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * POJO class, returned by getComplaintLocation in autocompleteTextview of NewGrievanceActivity
 **/

public class GrievanceLocation {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("id")
    @Expose
    private Integer id;

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

}
