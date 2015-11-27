package com.egovernments.egov.network;


import com.egovernments.egov.models.errors.ErrorResponse;
import com.google.gson.JsonObject;

import java.net.SocketTimeoutException;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class CustomErrorHandler implements retrofit.ErrorHandler {

    String errorDescription;
    ErrorResponse errorMessage;

    @Override
    public Throwable handleError(RetrofitError cause) {

        switch (cause.getKind()) {

            case NETWORK:
                if (cause.getCause() instanceof SocketTimeoutException) {
                    errorDescription = "The connection timed out while waiting for a response";
                } else
                    errorDescription = "Network is not accessible";
                break;

            case CONVERSION:
                errorDescription = "Received a malformed response from server";
                break;

            case HTTP:
                Response response = cause.getResponse();
                switch (response.getStatus()) {
                    case 400:
                        try {
                            errorMessage = (ErrorResponse) cause.getBodyAs(ErrorResponse.class);
                        } catch (Exception e) {
                            return cause;
                        }
                        try {
                            if (errorMessage != null) {
                                errorDescription = errorMessage.getErrorStatus().getMessage();
                            }
                        } catch (Exception e) {
                            try {
                                return cause;
                            } catch (Exception e1) {
                                errorDescription = "An unexpected error occurred";
                            }
                        }
                        return cause;

                    case 401:
                        JsonObject jsonObject = null;
                        try {
                            jsonObject = (JsonObject) cause.getBodyAs(JsonObject.class);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        if (jsonObject != null) {
                            //If failure due to invalid access token, attempt to renew token
                            String message = jsonObject.get("error_description").toString().trim();
                            if (message.contains("Invalid access token")) {

                                errorDescription = "Invalid access token";

                            } else return cause;
                        } else return cause;
                        break;

                    case 404:
                        errorDescription = "Server may be down for maintenance";
                        break;
                    case 503:
                        errorDescription = "Server is down for maintenance or over capacity";
                        break;
                    case 504:
                        errorDescription = "The connection timed out while waiting for a response";
                        break;

                    default:
                        return cause;
                }
                break;

            case UNEXPECTED:
                errorDescription = "An unexpected error occurred";
                break;

            default:
                return cause;
        }

        return new Exception(errorDescription);
    }
}
