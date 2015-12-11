
package org.egovernments.egoverp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PropertyTaxCallback {

    @SerializedName("assessmentNo")
    @Expose
    private String assessmentNo;
    @SerializedName("propertyAddress")
    @Expose
    private String propertyAddress;
    @SerializedName("localityName")
    @Expose
    private String localityName;
    @SerializedName("ownerDetails")
    @Expose
    private List<TaxOwnerDetail> taxOwnerDetails = new ArrayList<>();
    @SerializedName("taxDetails")
    @Expose
    private List<TaxDetail> taxDetails = new ArrayList<>();
    @SerializedName("errorDetails")
    @Expose
    private TaxErrorDetails taxErrorDetails;

    /**
     * @return The assessmentNo
     */
    public String getAssessmentNo() {
        return assessmentNo;
    }

    /**
     * @return The propertyAddress
     */
    public String getPropertyAddress() {
        return propertyAddress;
    }

    /**
     * @return The localityName
     */
    public String getLocalityName() {
        return localityName;
    }

    /**
     * @return The ownerDetails
     */
    public List<TaxOwnerDetail> getTaxOwnerDetails() {
        return taxOwnerDetails;
    }

    /**
     * @return The taxDetails
     */
    public List<TaxDetail> getTaxDetails() {
        return taxDetails;
    }

    /**
     * @return The taxErrorDetails
     */
    public TaxErrorDetails getTaxErrorDetails() {
        return taxErrorDetails;
    }

}
