package org.egov.employee.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by egov on 14/4/16.
 */
public class ComplaintViewAPIResponse {

    @SerializedName("status")
    @Expose
    private Status status;
    @SerializedName("result")
    @Expose
    private Result result;

    /**
     *
     * @return
     * The status
     */
    public Status getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The result
     */
    public Result getResult() {
        return result;
    }

    /**
     *
     * @param result
     * The result
     */
    public void setResult(Result result) {
        this.result = result;
    }


    public class Result {

        @SerializedName("complaintdetails")
        @Expose
        private ComplaintDetails complaintdetails;
        @SerializedName("complainthistory")
        @Expose
        private List<ComplaintHistory> complainthistory = new ArrayList<ComplaintHistory>();

        /**
         *
         * @return
         * The complaintdetails
         */
        public ComplaintDetails getComplaintdetails() {
            return complaintdetails;
        }

        /**
         *
         * @param complaintdetails
         * The complaintdetails
         */
        public void setComplaintdetails(ComplaintDetails complaintdetails) {
            this.complaintdetails = complaintdetails;
        }

        /**
         *
         * @return
         * The complainthistory
         */
        public List<ComplaintHistory> getComplainthistory() {
            return complainthistory;
        }

        /**
         *
         * @param complainthistory
         * The complainthistory
         */
        public void setComplainthistory(List<ComplaintHistory> complainthistory) {
            this.complainthistory = complainthistory;
        }

    }


}
