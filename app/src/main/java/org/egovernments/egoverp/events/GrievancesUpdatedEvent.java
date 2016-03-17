package org.egovernments.egoverp.events;

/**
 * Intentionally empty. Used by eventbus to post events
 * Posted when updateService updates grievance
 **/
public class GrievancesUpdatedEvent {

    private boolean isSendRequest=false;

    private boolean isPaginationEnded=false;

    public boolean isSendRequest() {
        return isSendRequest;
    }

    public void setIsSendRequest(boolean isSendRequest) {
        this.isSendRequest = isSendRequest;
    }

    public boolean isPaginationEnded() {
        return isPaginationEnded;
    }

    public void setIsPaginationEnded(boolean isPaginationEnded) {
        this.isPaginationEnded = isPaginationEnded;
    }
}
