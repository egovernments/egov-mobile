
package com.egovernments.egov.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TaxDetail {

    @SerializedName("installment")
    @Expose
    private String installment;
    @SerializedName("taxAmount")
    @Expose
    private Integer taxAmount;
    @SerializedName("chqBouncePenalty")
    @Expose
    private Integer chqBouncePenalty;
    @SerializedName("penalty")
    @Expose
    private Integer penalty;
    @SerializedName("rebate")
    @Expose
    private Integer rebate;
    @SerializedName("totalAmount")
    @Expose
    private Integer totalAmount;

    /**
     * @return The installment
     */
    public String getInstallment() {
        return installment;
    }

    /**
     * @return The taxAmount
     */
    public Integer getTaxAmount() {
        return taxAmount;
    }

    /**
     * @return The chqBouncePenalty
     */
    public Integer getChqBouncePenalty() {
        return chqBouncePenalty;
    }

    /**
     * @return The penalty
     */
    public Integer getPenalty() {
        return penalty;
    }

    /**
     * @return The totalAmount
     */
    public Integer getTotalAmount() {
        return totalAmount;
    }

    public Integer getRebate() {
        return rebate;
    }
}
