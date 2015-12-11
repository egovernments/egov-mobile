package org.egovernments.egoverp.models;

public class WaterTaxRequest {

    private String ulbCode;
    private String consumerNo;

    public WaterTaxRequest(String ulbCode, String consumerNo) {
        this.ulbCode = ulbCode;
        this.consumerNo = consumerNo;
    }
}
