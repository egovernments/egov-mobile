/*
 * ******************************************************************************
 *  eGov suite of products aim to improve the internal efficiency,transparency,
 *      accountability and the service delivery of the government  organizations.
 *
 *        Copyright (C) <2016>  eGovernments Foundation
 *
 *        The updated version of eGov suite of products as by eGovernments Foundation
 *        is available at http://www.egovernments.org
 *
 *        This program is free software: you can redistribute it and/or modify
 *        it under the terms of the GNU General Public License as published by
 *        the Free Software Foundation, either version 3 of the License, or
 *        any later version.
 *
 *        This program is distributed in the hope that it will be useful,
 *        but WITHOUT ANY WARRANTY; without even the implied warranty of
 *        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *        GNU General Public License for more details.
 *
 *        You should have received a copy of the GNU General Public License
 *        along with this program. If not, see http://www.gnu.org/licenses/ or
 *        http://www.gnu.org/licenses/gpl.html .
 *
 *        In addition to the terms of the GPL license to be adhered to in using this
 *        program, the following additional terms are to be complied with:
 *
 *    	1) All versions of this program, verbatim or modified must carry this
 *    	   Legal Notice.
 *
 *    	2) Any misrepresentation of the origin of the material is prohibited. It
 *    	   is required that all modified versions of this material be marked in
 *    	   reasonable ways as different from the original version.
 *
 *    	3) This license does not grant any rights to any user of the program
 *    	   with regards to rights under trademark law for use of the trade names
 *    	   or trademarks of eGovernments Foundation.
 *
 *      In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *  *****************************************************************************
 */

package org.egovernments.egoverp.network;


import com.google.gson.JsonObject;

import org.egovernments.egoverp.models.errors.ErrorResponse;

import java.net.SocketTimeoutException;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class CustomErrorHandler implements retrofit.ErrorHandler {

    String errorDescription;
    ErrorResponse errorMessage;

    public static String SESSION_EXPRIED_MESSAGE="Invalid access token";

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
                        break;

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
                            if (message.contains(SESSION_EXPRIED_MESSAGE)) {

                                errorDescription = SESSION_EXPRIED_MESSAGE;

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
