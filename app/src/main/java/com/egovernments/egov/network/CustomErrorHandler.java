package com.egovernments.egov.network;


import com.egovernments.egov.models.errors.ErrorAsErrorMessage;

import java.net.SocketTimeoutException;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class CustomErrorHandler implements retrofit.ErrorHandler {

    String errorDescription;
    ErrorAsErrorMessage errorMessage;

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
                            errorMessage = (ErrorAsErrorMessage) cause.getBodyAs(ErrorAsErrorMessage.class);
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
                        break;

                    case 401:
                        try {
                            errorMessage = (ErrorAsErrorMessage) cause.getBodyAs(ErrorAsErrorMessage.class);
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
                }
                break;

            case UNEXPECTED:
                errorDescription = "An unexpected error occurred";
                break;
        }

        return new Exception(errorDescription);
    }
}
