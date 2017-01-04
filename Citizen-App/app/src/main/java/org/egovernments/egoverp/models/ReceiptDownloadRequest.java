package org.egovernments.egoverp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by egov on 2/1/17.
 */

public class ReceiptDownloadRequest {

    @SerializedName("ulbCode")
    @Expose
    private String ulbCode;
    @SerializedName("receiptNo")
    @Expose
    private String receiptNo;
    @SerializedName("referenceNo")
    @Expose
    private String referenceNo;

    public ReceiptDownloadRequest(String ulbCode, String receiptNo, String referenceNo) {
        this.ulbCode = ulbCode;
        this.receiptNo = receiptNo;
        this.referenceNo = referenceNo;
    }

    public String getUlbCode() {
        return ulbCode;
    }

    public void setUlbCode(String ulbCode) {
        this.ulbCode = ulbCode;
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

}
