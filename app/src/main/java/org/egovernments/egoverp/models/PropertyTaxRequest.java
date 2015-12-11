package org.egovernments.egoverp.models;


public class PropertyTaxRequest {

    private String ulbCode;
    private String assessmentNo;

    public PropertyTaxRequest(String ulbCode, String assessmentNo) {
        this.ulbCode = ulbCode;
        this.assessmentNo = assessmentNo;
    }
}
