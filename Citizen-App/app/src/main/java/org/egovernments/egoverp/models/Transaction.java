package org.egovernments.egoverp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by egov on 29/12/16.
 */

public class Transaction {


    @SerializedName("transactionId")
    @Expose
    private String transactionId;
    @SerializedName("receiptNo")
    @Expose
    private String receiptNo;
    @SerializedName("referenceNo")
    @Expose
    private String referenceNo;
    @SerializedName("amount")
    @Expose
    private Double amount;
    @SerializedName("txnDate")
    @Expose
    private String txnDate;
    @SerializedName("paymentPeriod")
    @Expose
    private Object paymentPeriod;
    @SerializedName("paymentType")
    @Expose
    private Object paymentType;
    @SerializedName("serviceName")
    @Expose
    private String serviceName;
    @SerializedName("payeeName")
    @Expose
    private String payeeName;
    @SerializedName("receiptStatus")
    @Expose
    private String receiptStatus;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("errorDetails")
    @Expose
    private List<ErrorDetail> errorDetails = null;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getTxnDate() {
        return txnDate;
    }

    public void setTxnDate(String txnDate) {
        this.txnDate = txnDate;
    }

    public Object getPaymentPeriod() {
        return paymentPeriod;
    }

    public void setPaymentPeriod(Object paymentPeriod) {
        this.paymentPeriod = paymentPeriod;
    }

    public Object getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Object paymentType) {
        this.paymentType = paymentType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ErrorDetail> getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(List<ErrorDetail> errorDetails) {
        this.errorDetails = errorDetails;
    }

    public String getReceiptStatus() {
        return receiptStatus;
    }

    public void setReceiptStatus(String receiptStatus) {
        this.receiptStatus = receiptStatus;
    }

    public enum ReceiptStatus {

        @SerializedName("To Be Submitted")
        TO_BE_SUBMITTED("To Be Submitted"),

        @SerializedName("Submitted")
        SUBMITTED("Submitted"),

        @SerializedName("Approved")
        APPROVED("Approved"),

        @SerializedName("Remitted")
        REMITTED("Remitted"),

        @SerializedName("Pending")
        PENDING("Pending"),

        @SerializedName("Cancelled")
        CANCELLED("Cancelled"),

        @SerializedName("Failed")
        FAILED("Failed");

        private final String receiptValue;

        private ReceiptStatus(String s) {
            this.receiptValue = s;
        }

        public String getValue() {
            return this.receiptValue;
        }

    }

}
