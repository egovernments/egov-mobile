package org.egovernments.egoverp.models;


public class GrievanceUpdate {

    private String action;
    private String feedback;
    private String comment;

    public GrievanceUpdate(String action, String feedback, String comment) {
        this.action = action;
        this.feedback = feedback;
        this.comment = comment;
    }
}
