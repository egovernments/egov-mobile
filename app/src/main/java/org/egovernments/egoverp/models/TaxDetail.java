
package org.egovernments.egoverp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TaxDetail {

    @SerializedName("installment")
    @Expose
    private String installment;
    @SerializedName("taxAmount")
    @Expose
    private Double taxAmount;
    @SerializedName("chqBouncePenalty")
    @Expose
    private Double chqBouncePenalty;
    @SerializedName("penalty")
    @Expose
    private Double penalty;
    @SerializedName("rebate")
    @Expose
    private Double rebate;
    @SerializedName("totalAmount")
    @Expose
    private Double totalAmount;

    /**
     * @return The installment
     */
    public String getInstallment() {
        return installment;
    }

    /**
     * @return The taxAmount
     */
    public Double getTaxAmount() {
        return taxAmount;
    }

    /**
     * @return The chqBouncePenalty
     */
    public Double getChqBouncePenalty() {
        return chqBouncePenalty;
    }

    /**
     * @return The penalty
     */
    public Double getPenalty() {
        return penalty;
    }

    /**
     * @return The totalAmount
     */
    public Double getTotalAmount() {
        return totalAmount;
    }

    public Double getRebate() {
        return rebate;
    }
}
