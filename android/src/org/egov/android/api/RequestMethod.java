package org.egov.android.api;

public enum RequestMethod {
    GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE");

    private String method = "";

    RequestMethod(String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return method;
    }

}
