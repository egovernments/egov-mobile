
package org.egovernments.egoverp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class WaterTaxCallback {

    @SerializedName("consumerNo")
    @Expose
    private String consumerNo;
    @SerializedName("ownerName")
    @Expose
    private String ownerName;
    @SerializedName("mobileNo")
    @Expose
    private String mobileNo;
    @SerializedName("propertyAddress")
    @Expose
    private String propertyAddress;
    @SerializedName("localityName")
    @Expose
    private String localityName;
    @SerializedName("taxDetails")
    @Expose
    private List<TaxDetail> taxDetails = new ArrayList<>();
    @SerializedName("errorDetails")
    @Expose
    private TaxErrorDetails errorDetails;

    /**
     * @return The consumerNo
     */
    public String getConsumerNo() {
        return consumerNo;
    }

    /**
     * @return The ownerName
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * @return The mobileNo
     */
    public String getMobileNo() {
        return mobileNo;
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
     * @return The taxDetails
     */
    public List<TaxDetail> getTaxDetails() {
        return taxDetails;
    }

    /**
     * @return The errorDetails
     */
    public TaxErrorDetails getTaxErrorDetails() {
        return errorDetails;
    }

}
