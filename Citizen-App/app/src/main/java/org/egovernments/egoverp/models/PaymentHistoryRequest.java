package org.egovernments.egoverp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by egov on 29/12/16.
 */

public class PaymentHistoryRequest {

    @SerializedName("ulbCode")
    @Expose
    String ulbCode;

    @SerializedName("userName")
    @Expose
    String userName;

    @SerializedName("serviceName")
    @Expose
    ServiceName serviceName;

    @SerializedName("consumerCode")
    @Expose
    String consumerCode;

    public PaymentHistoryRequest(String ulbCode, String userName, ServiceName serviceName, String consumerCode) {
        this.ulbCode = ulbCode;
        this.userName = userName;
        this.serviceName = serviceName;
        this.consumerCode = consumerCode;
    }

    public enum ServiceName {

        @SerializedName("Water Tax")
        WATER_TAX("Water Tax"),

        @SerializedName("Property Tax")
        PROPERTY_TAX("Property Tax");

        String value;

        ServiceName(String s) {
            value = s;
        }

        public String getValue() {
            return value;
        }
    }

}
