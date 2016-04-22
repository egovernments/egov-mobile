package org.egov.employee.data;

/**
 * Created by egov on 11/2/16.
 */

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TaskAPIResponse {

    @SerializedName("status")
    @Expose
    private Status status;
    @SerializedName("result")
    @Expose
    private List<Task> result = new ArrayList<Task>();

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
    public List<Task> getResult() {
        return result;
    }

    /**
     *
     * @param result
     * The result
     */
    public void setResult(List<Task> result) {
        this.result = result;
    }

}