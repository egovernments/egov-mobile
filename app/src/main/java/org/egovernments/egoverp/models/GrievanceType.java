
package org.egovernments.egoverp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * POJO class, returned by getComplaintType, used in spinner of NewGrievanceActivity
 **/
public class GrievanceType {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("typeImage")
    @Expose
    private String typeImage;
    @SerializedName("description")
    @Expose
    private String description;

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

    /**
     * @return The typeImage
     */
    public String getTypeImage() {
        return typeImage;
    }

    /**
     * @return The description
     */
    public String getDescription() {
        return description;
    }

}
