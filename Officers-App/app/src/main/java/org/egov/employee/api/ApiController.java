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

import org.egov.employee.config.AppPreference;
import org.egov.employee.data.ComplaintViewAPIResponse;
import org.egov.employee.data.GrievanceAPIResponse;
import org.egov.employee.data.GrievanceLocationAPIResponse;
import org.egov.employee.data.GrievanceTypeAPIResponse;
import org.egov.employee.data.GrievanceUpdate;
import org.egov.employee.data.TaskAPIResponse;
import org.egov.employee.data.TaskAPISearchResponse;

import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * REST API Controller
 */

public class ApiController {

    private final static okhttp3.OkHttpClient.Builder okHttpBuilder = SSLTrustManager.createClient();
    @SuppressWarnings("All")
    public static APIInterface apiInterface = null;

    private static OkHttpClient getOkHttpClient(Context context) {
        okHttpBuilder.interceptors().clear();

        Interceptor logging = new Interceptor(context);
        // set your desired log level none, body, header
        logging.setLevel(Interceptor.Level.BODY);
        return okHttpBuilder.addInterceptor(logging).build();
    }

    public static Response getResponseFromUrl(Context context, HttpUrl url) {

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            return getOkHttpClient(context).newCall(request).execute();

        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("All")
    public static Response getCityURL(Context context, String url, int cityCode) {

        try {
            Request request = new Request.Builder()
                    .url(url + "&code=" + cityCode)
                    .build();
            return getOkHttpClient(context).newCall(request).execute();
        } catch (Exception e) {
            return null;
        }
    }

    //Sets up the API client
    public static APIInterface getAPI(Context context) {
        if (apiInterface == null) {
            okHttpBuilder.interceptors().clear();
            Interceptor logging = new Interceptor(context);
            // set your desired log level none, body, header
            logging.setLevel(Interceptor.Level.BODY);

            AppPreference preference = new AppPreference(context);

            okhttp3.OkHttpClient client = okHttpBuilder.addInterceptor(logging).build();

            Retrofit retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(preference.getActiveCityUrl().endsWith("/") ?
                            preference.getActiveCityUrl() : preference.getActiveCityUrl() + "/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiInterface = retrofit.create(APIInterface.class);
        }

        return apiInterface;
    }

    public static APIInterface getAPI(Context context, String baseUrl) {

        apiInterface = null;

        okHttpBuilder.interceptors().clear();
        Interceptor logging = new Interceptor(context);
        // set your desired log level none, body, header
        logging.setLevel(Interceptor.Level.BODY);
        okhttp3.OkHttpClient client = okHttpBuilder.addInterceptor(logging).build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiInterface = retrofit.create(APIInterface.class);


        return apiInterface;
    }

    @SuppressWarnings("All")
    public interface APIInterface {

        @FormUrlEncoded
        @POST(ApiUrl.EMPLOYEE_LOGIN)
        Call<JsonObject> login(@Header("Authorization") String authorization, @Field("username") String username,
                               @Field("scope") String scope,
                               @Field("password") String password,
                               @Field("grant_type") String grant_type);

        @POST(ApiUrl.EMPLOYEE_LOG)
        Call<JsonObject> addDeviceLog(@Query(value = "deviceId") String deviceId, @Query(value = "deviceType") String deivceType,
                                      @Query(value = "deviceOS") String deviceOS, @Query(value = "access_token") String accessToken);

        @POST(ApiUrl.EMPLOYEE_LOGOUT)
        Call<JsonObject> logout(@Query("access_token") String accessToken);

        @GET(ApiUrl.EMPLOYEE_WORKLIST_TYPES)
        Call<JsonObject> inboxCategoryWithItemsCount(@Query("access_token") String accessToken);

        @GET(ApiUrl.GET_ESCALATED_COMPLAINTS_COUNT)
        Call<JsonObject> getEscalatedComplaintsCount(@Query("access_token") String accessToken);

        @GET(ApiUrl.EMPLOYEE_WORKLIST)
        Call<TaskAPIResponse> getInboxItemsByCategory(@Path("worklisttype") String worklisttype, @Path("from") int from,
                                                      @Path("to") int to, @Query("priority") String priority,
                                                      @Query("access_token") String accessToken);

        @GET(ApiUrl.EMPLOYEE_COMPLAINTS_ESCALATED)
        Call<TaskAPIResponse> getEscalatedComplaints(@Path("from") int from, @Path("to") int to,
                                                     @Query("access_token") String accessToken);

        @POST(ApiUrl.EMPLOYEE_SEARCH_INBOX)
        Call<TaskAPISearchResponse> searchInboxItems(@Header("Content-Type") String contentType, @Body JsonObject jsonObject,
                                                     @Path("pageno") int from, @Path("limit") int to,
                                                     @Query("access_token") String accessToken);

        @GET(ApiUrl.EMPLOYEE_FORWARD_DETAILS)
        Call<JsonObject> getForwardDetails(@Query(value = "department") String departmentId,
                                           @Query(value = "designation") String designationId,
                                           @Query(value = "access_token") String accessToken);

        @GET(ApiUrl.PGR_COMPLAINT_DETAILS)
        Call<ComplaintViewAPIResponse> getComplaintDetails(@Path(value = "complaintNo") String complaintNo,
                                                           @Query(value = "access_token") String accessToken);

        @GET(ApiUrl.PGR_COMPLAINT_HISTORY)
        Call<ComplaintViewAPIResponse.HistoryAPIResponse> getComplaintHistory(@Path(value = "complaintNo") String complaintNo,
                                                                              @Query(value = "access_token") String accessToken);

        @GET(ApiUrl.PGR_COMPLAINT_ACTIONS)
        Call<JsonObject> getComplaintActions(@Path(value = "complaintNo") String complaintNo,
                                             @Query(value = "access_token") String accessToken);

        @Multipart
        @POST(ApiUrl.PGR_COMPLAINT_UPDATE)
        Call<JsonObject> updateComplaint(@Path(value = "complaintNo") String complaintNo,
                                         @Query(value = "access_token") String accessToken, @PartMap Map<String, RequestBody> files);


        /*
           Grievance
         */

        @GET(ApiUrl.GET_MY_COMPLAINTS_CATEGORIES_COUNT)
        Call<JsonObject> getComplaintCategories(@Query(value = "access_token") String accessToken);

        @GET(ApiUrl.GET_MY_COMPLAINTS)
        Call<GrievanceAPIResponse> getMyComplaints(@Path(value = "page") String pages,
                                                   @Path(value = "pageSize") String pagesize,
                                                   @Query(value = "access_token") String access_token,
                                                   @Query(value = "complaintStatus") String complaintStatus);

        @GET(ApiUrl.COMPLAINT_CATEGORIES_TYPES)
        Call<GrievanceTypeAPIResponse> getComplaintCategoriesAndTypes(@Query(value = "access_token") String access_token);

        @GET(ApiUrl.COMPLAINT_GET_LOCATION_BY_NAME)
        Call<GrievanceLocationAPIResponse> getComplaintLocation(@Query(value = "locationName") String location,
                                                                @Query(value = "access_token") String access_token);

        @Multipart
        @POST(ApiUrl.COMPLAINT_CREATE)
        Call<JsonObject> createComplaint(@Query(value = "access_token") String accessToken, @PartMap Map<String, RequestBody> files);

        @PUT(ApiUrl.COMPLAINT_UPDATE_STATUS)
        Call<JsonObject> updateComplaint(@Path(value = "complaintNo") String complaintNo,
                                         @Body GrievanceUpdate grievanceUpdate, @Query(value = "access_token") String accessToken);

    }


}