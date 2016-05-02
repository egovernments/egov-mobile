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
    private ComplaintDetails complaintDetails;

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

    public ComplaintDetails getComplaintDetails() {
        return complaintDetails;
    }

    public void setComplaintDetails(ComplaintDetails complaintDetails) {
        this.complaintDetails = complaintDetails;
    }

    public class HistoryAPIResponse{

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

            @SerializedName("comments")
            @Expose
            private List<ComplaintHistory> comments = new ArrayList<ComplaintHistory>();

            /**
             *
             * @return
             * The comments
             */
            public List<ComplaintHistory> getComments() {
                return comments;
            }

            /**
             *
             * @param comments
             * The comments
             */
            public void setComments(List<ComplaintHistory> comments) {
                this.comments = comments;
            }

        }

    }



}
