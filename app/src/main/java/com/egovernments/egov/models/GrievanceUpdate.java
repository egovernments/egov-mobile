package com.egovernments.egov.models;


public class GrievanceUpdate {

    private String action;
    private String feedbackOption;
    private String comment;

    public GrievanceUpdate(String action, String feedbackOption, String comment) {
        this.action = action;
        this.feedbackOption = feedbackOption;
        this.comment = comment;
    }
}
