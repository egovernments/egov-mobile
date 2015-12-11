
package org.egovernments.egoverp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TaxOwnerDetail {

    @SerializedName("ownerName")
    @Expose
    private String ownerName;
    @SerializedName("mobileNo")
    @Expose
    private String mobileNo;

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

}
