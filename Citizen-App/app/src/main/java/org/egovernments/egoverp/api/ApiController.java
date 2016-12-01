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

package org.egovernments.egoverp.api;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.egovernments.egoverp.config.SessionManager;
import org.egovernments.egoverp.models.BuildingPlanAPIResponse;
import org.egovernments.egoverp.models.City;
import org.egovernments.egoverp.models.District;
import org.egovernments.egoverp.models.GrievanceAPIResponse;
import org.egovernments.egoverp.models.GrievanceCommentAPIResponse;
import org.egovernments.egoverp.models.GrievanceLocationAPIResponse;
import org.egovernments.egoverp.models.GrievanceTypeAPIResponse;
import org.egovernments.egoverp.models.GrievanceUpdate;
import org.egovernments.egoverp.models.Profile;
import org.egovernments.egoverp.models.ProfileAPIResponse;
import org.egovernments.egoverp.models.PropertySearchRequest;
import org.egovernments.egoverp.models.PropertyTaxCallback;
import org.egovernments.egoverp.models.PropertyViewRequest;
import org.egovernments.egoverp.models.RegisterRequest;
import org.egovernments.egoverp.models.WaterConnectionSearchRequest;
import org.egovernments.egoverp.models.WaterTaxCallback;
import org.egovernments.egoverp.models.WaterTaxRequest;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

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

public class ApiController {

    private final static okhttp3.OkHttpClient.Builder okHttpBuilder = SSLTrustManager.createClient();
    public static APIInterface apiInterface = null;

    public static City getCityURL(String url) throws IOException{
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = okHttpBuilder.build().newCall(request).execute();
            return new Gson().fromJson(response.body().charStream(), City.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getResponseFromUrl(String url)throws IOException{
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = okHttpBuilder.build().newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            return null;
        }
    }

    public static List<District> getAllCitiesURLs(String url) throws IOException {

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = okHttpBuilder.build().newCall(request).execute();
            if (!response.isSuccessful())
                throw new IOException();
            Type type = new TypeToken<List<District>>() {
            }.getType();
            return new Gson().fromJson(response.body().charStream(), type);
        } catch (Exception e) {
            return null;
        }

    }

    public static City getCityURL(String url, int cityCode) throws IOException {

        try {
            Request request = new Request.Builder()
                    .url(url + "&code=" + cityCode)
                    .build();

            Response response = okHttpBuilder.build().newCall(request).execute();
            if (!response.isSuccessful())
                throw new IOException();
            return new Gson().fromJson(response.body().charStream(), City.class);
        } catch (Exception e) {
            return null;
        }

    }

    //Sets up the API client
    public static APIInterface getRetrofit2API(Context context, Interceptor.ErrorListener errorListener) {

        SessionManager sessionManager = new SessionManager(context);

        okHttpBuilder.interceptors().clear();

        Interceptor logging = new Interceptor();
        // set your desired log level none, body, header
        logging.setLevel((sessionManager.getKeyDebugLog() ? Interceptor.Level.BODY : Interceptor.Level.NONE));
        logging.setErrorListener(errorListener);

        okhttp3.OkHttpClient client = okHttpBuilder.addInterceptor(logging).build();

        if (apiInterface == null || errorListener != null) {

            Retrofit retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(sessionManager.getBaseURL())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiInterface = retrofit.create(APIInterface.class);
        }

        return apiInterface;
    }

    public static APIInterface getRetrofit2API(Context context, String url, Interceptor.ErrorListener errorListener) {
        SessionManager sessionManager = new SessionManager(context);
        apiInterface=null;

        okHttpBuilder.interceptors().clear();

        Interceptor logging = new Interceptor();
        //set your desired log level none, body, header
        logging.setLevel((sessionManager.getKeyDebugLog() ? Interceptor.Level.BODY : Interceptor.Level.NONE));
        logging.setErrorListener(errorListener);
        okhttp3.OkHttpClient client = okHttpBuilder.addInterceptor(logging).build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url)
                .build();

        apiInterface = retrofit.create(APIInterface.class);
        return apiInterface;
    }

    public interface APIInterface {

        @FormUrlEncoded
        @POST(ApiUrl.CITIZEN_LOGIN)
        Call<JsonObject> login(@Header("Authorization") String authorization, @Field("username") String username,
                               @Field("scope") String scope,
                               @Field("password") String password,
                               @Field("grant_type") String grant_type);

        @GET(ApiUrl.CITIZEN_GET_PROFILE)
        Call<ProfileAPIResponse> getProfile(@Query(value = "access_token") String access_token);//Callback<ProfileAPIResponse> profileAPIResponseCallback

        @FormUrlEncoded
        @POST(ApiUrl.CITIZEN_PASSWORD_RECOVER)
        Call<JsonObject> recoverPassword(@Field("identity") String identity,
                                         @Field("redirectURL") String redirectURL);

        @FormUrlEncoded
        @POST(ApiUrl.CITIZEN_PASSWORD_RECOVER)
        Call<JsonObject> resetPassword(@Field("identity") String identity,
                                       @Field("token") String token,
                                       @Field("newPassword") String newPassword,
                                       @Field("confirmPassword") String confirmPassword);

        @FormUrlEncoded
        @POST(ApiUrl.CITIZEN_SEND_OTP)
        Call<JsonObject> sendOTPToVerifyBeforeAccountCreate(@Field("identity") String identity);

        @POST(ApiUrl.CITIZEN_REGISTER)
        Call<JsonObject> registerUser(@Body RegisterRequest registerRequest);

        @GET(ApiUrl.CITIZEN_GET_COMPLAINT_CATEGORIES_COUNT)
        Call<JsonObject> getComplaintCategoryCount(@Query(value = "access_token") String authorization);

        @GET(ApiUrl.CITIZEN_GET_MY_COMPLAINT)
        Call<GrievanceAPIResponse> getMyComplaints(@Path("page") String pages,
                                                   @Path(value = "pageSize") String pageSize,
                                                   @Query(value = "access_token") String accessToken,
                                                   @Query(value = "complaintStatus") String complaintStatus);

        @GET(ApiUrl.COMPLAINT_CATEGORIES_TYPES)
        Call<GrievanceTypeAPIResponse> getComplaintCategoryAndTypes(@Query(value = "access_token") String access_token);

        @GET(ApiUrl.COMPLAINT_GET_LOCATION_BY_NAME)
        Call<GrievanceLocationAPIResponse> getComplaintLocation(@Query(value = "locationName") String location,
                                                                @Query(value = "access_token") String access_token);

        @Multipart
        @POST(ApiUrl.COMPLAINT_CREATE)
        Call<JsonObject> createComplaint(@Query(value = "access_token") String access_token, @PartMap Map<String, RequestBody> files);

        @GET(ApiUrl.COMPLAINT_HISTORY)
        Call<GrievanceCommentAPIResponse> getComplaintHistory(@Path(value = "complaintNo") String complaintNo,
                                                              @Query(value = "access_token") String access_token);

        @PUT(ApiUrl.COMPLAINT_UPDATE_STATUS)
        Call<JsonObject> updateGrievance(@Path(value = "complaintNo") String complaintNo,
                                         @Body GrievanceUpdate grievanceUpdate,
                                         @Query(value = "access_token") String access_token);

        @POST(ApiUrl.SEARCH_PROPERTY)
        Call<List<PropertyTaxCallback>> searchProperty(@Header("Referer") String referer,
                                                       @Body PropertySearchRequest propertySearchRequest);

        @POST(ApiUrl.PROPERTY_TAX_DETAILS)
        Call<PropertyTaxCallback> getPropertyTax(@Header("Referer") String referer,
                                                 @Body PropertyViewRequest propertyViewRequest);

        @POST(ApiUrl.SEARCH_WATER_CONNECTION)
        Call<List<WaterTaxCallback>> searchWaterConnection(@Header("Referer") String referer,
                                                           @Body WaterConnectionSearchRequest waterConnectionSearchRequest);

        @POST(ApiUrl.WATER_TAX_DETAILS)
        Call<WaterTaxCallback> getWaterTax(@Header("Referer") String referer,
                                           @Body WaterTaxRequest waterTaxRequest);

        @GET(ApiUrl.BPA_DETAILS)
        Call<BuildingPlanAPIResponse> getBuildingPlanApprovalDetails(@Path(value = "applicationNo") String applicationNo, @Path(value = "authKey") String authKey);

        @PUT(ApiUrl.CITIZEN_UPDATE_PROFILE)
        Call<ProfileAPIResponse> updateProfile(@Body Profile profile,
                                               @Query(value = "access_token") String access_token);

        @FormUrlEncoded
        @POST(ApiUrl.CITIZEN_LOGOUT)
        Call<JsonObject> logout(@Field("access_token") String access_token);

        /*@GET(ApiUrl.COMPLAINTS_COUNT_DETAILS)
        void getComplaintCountDetails(@Query(value = "access_token", encodeValue = false) String access_token,
                           Callback<JsonObject> grievanceAPIJsonCallback);*/

        /*@FormUrlEncoded
        @POST(ApiUrl.CITIZEN_LOGOUT)
        void logout(@Field("access_token") String access_token,
                    Callback<JsonObject> jsonObjectCallback);




        @PUT(ApiUrl.CITIZEN_UPDATE_PROFILE)
        void updateProfile(@Body Profile profile,
                           @Query(value = "access_token", encodeValue = false) String access_token,
                           Callback<ProfileAPIResponse> profileAPIResponseCallback);
*/

    }


}

