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

package org.egov.employee.api;

import android.content.Context;

import com.google.gson.JsonObject;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.egov.employee.config.AppPreference;
import org.egov.employee.data.ComplaintViewAPIResponse;
import org.egov.employee.data.TaskAPIResponse;
import org.egov.employee.data.TaskAPISearchResponse;

import java.util.Map;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PartMap;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by egov on 11/1/16.
 */

public class ApiController {

    public static APIInterface apiInterface = null;

    private final static OkHttpClient client = SSLTrustManager.createClient();

    public static Response getCityURL(String url, LoggingInterceptor.ErrorListener errorListener) {

        try {

            client.interceptors().clear();

            LoggingInterceptor logging = new LoggingInterceptor();
            // set your desired log level none, body, header
            logging.setLevel(LoggingInterceptor.Level.BODY);
            logging.setErrorListener(errorListener);
            client.interceptors().add(logging);

            Request request = new Request.Builder()
                    .url(url)
                    .build();
            return client.newCall(request).execute();

        } catch (Exception e) {
            return null;
        }
    }

    public static Response getCityURL(String url, int cityCode, LoggingInterceptor.ErrorListener errorListener) {

        try {

            LoggingInterceptor logging = new LoggingInterceptor();
            // set your desired log level none, body, header
            logging.setLevel(LoggingInterceptor.Level.BODY);
            logging.setErrorListener(errorListener);
            client.interceptors().add(logging);

            Request request = new Request.Builder()
                    .url(url + "&code=" + cityCode)
                    .build();
            return client.newCall(request).execute();
        } catch (Exception e) {
            return null;
        }

    }


    /*public static List<City> getAllCitiesURLs(String url) throws IOException {

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful())
                throw new IOException();
            Type type = new TypeToken<List<City>>() {
            }.getType();
            return new Gson().fromJson(response.body().charStream(), type);
        } catch (Exception e) {
            return null;
        }

    }*/

    //Sets up the API client
    public static APIInterface getAPI(Context context, LoggingInterceptor.ErrorListener errorListener) {
        /*SessionManager sessionManager = new SessionManager(context);
        if (apiInterface == null) {


        }*/

        client.interceptors().clear();

        LoggingInterceptor logging = new LoggingInterceptor();
        // set your desired log level none, body, header
        logging.setLevel(LoggingInterceptor.Level.BODY);
        logging.setErrorListener(errorListener);

        client.interceptors().add(logging);

        AppPreference preference=new AppPreference(context);

        Retrofit restAdapter = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl((preference.getActiveCityUrl().endsWith("/") ? preference.getActiveCityUrl() : preference.getActiveCityUrl() + "/"))
                .build();

        apiInterface = restAdapter.create(APIInterface.class);
        return apiInterface;
    }

    public interface APIInterface {

        @FormUrlEncoded
        @POST(ApiUrl.EMPLOYEE_LOGIN)
        Call<JsonObject> login(@Header("Authorization") String authorization, @Field("username") String username,
                               @Field("scope") String scope,
                               @Field("password") String password,
                               @Field("grant_type") String grant_type);

        @POST(ApiUrl.EMPLOYEE_LOGOUT)
        Call<JsonObject> logout(@Query("access_token") String accessToken);

        @GET(ApiUrl.EMPLOYEE_WORKLIST_TYPES)
        Call<JsonObject> inboxCategoryWithItemsCount(@Query("access_token") String accessToken);

        @GET(ApiUrl.EMPLOYEE_WORKLIST)
        Call<TaskAPIResponse> getInboxItemsByCategory(@Path("worklisttype") String worklisttype, @Path("from") int from, @Path("to") int to, @Query("access_token") String accessToken);

        @POST(ApiUrl.EMPLOYEE_SEARCH_INBOX)
        Call<TaskAPISearchResponse> searchInboxItems(@Header("Content-Type") String contentType, @Body JsonObject jsonObject, @Path("pageno") int from, @Path("limit") int to, @Query("access_token") String accessToken);

        @GET(ApiUrl.EMPLOYEE_FORWARD_DETAILS)
        Call<JsonObject> getForwardDetails(@Query(value = "department") String departmentId, @Query(value = "designation") String designationId, @Query(value = "access_token") String accessToken);

        @GET(ApiUrl.PGR_COMPLAINT_DETAILS)
        Call<ComplaintViewAPIResponse> getComplaintDetails(@Path(value = "complaintNo") String complaintNo,
                                 @Query(value = "access_token") String accessToken);

        @GET(ApiUrl.PGR_COMPLAINT_HISTORY)
        Call<ComplaintViewAPIResponse.HistoryAPIResponse> getComplaintHistory(@Path(value = "complaintNo") String complaintNo,
                                                           @Query(value = "access_token") String accessToken);

        @GET(ApiUrl.PGR_COMPLAINT_ACTIONS)
        Call<JsonObject> getComplaintActions(@Path(value = "complaintNo") String complaintNo, @Query(value = "access_token") String accessToken);

        @Multipart
        @POST(ApiUrl.PGR_COMPLAINT_UPDATE)
        Call<JsonObject> updateComplaint(@Path(value = "complaintNo") String complaintNo, @Query(value = "access_token") String accessToken, @PartMap Map<String, RequestBody> files);


    }


}