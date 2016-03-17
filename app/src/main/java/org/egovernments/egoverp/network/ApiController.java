package org.egovernments.egoverp.network;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.egovernments.egoverp.models.City;
import org.egovernments.egoverp.models.District;
import org.egovernments.egoverp.models.GrievanceAPIResponse;
import org.egovernments.egoverp.models.GrievanceCommentAPIResponse;
import org.egovernments.egoverp.models.GrievanceLocationAPIResponse;
import org.egovernments.egoverp.models.GrievanceTypeAPIResponse;
import org.egovernments.egoverp.models.GrievanceUpdate;
import org.egovernments.egoverp.models.Profile;
import org.egovernments.egoverp.models.ProfileAPIResponse;
import org.egovernments.egoverp.models.PropertyTaxCallback;
import org.egovernments.egoverp.models.PropertyTaxRequest;
import org.egovernments.egoverp.models.RegisterRequest;
import org.egovernments.egoverp.models.WaterTaxCallback;
import org.egovernments.egoverp.models.WaterTaxRequest;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.MultipartTypedOutput;

public class ApiController {

    public static APIInterface apiInterface = null;

    private final static OkHttpClient client = SSLTrustManager.createClient();

    public static String getCityURL(String url) throws IOException{

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();

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

            Response response = client.newCall(request).execute();
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

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful())
                throw new IOException();
            return new Gson().fromJson(response.body().charStream(), City.class);
        } catch (Exception e) {
            return null;
        }

    }

    //Sets up the API client
    public static APIInterface getAPI(Context context) {
        SessionManager sessionManager = new SessionManager(context);
        if (apiInterface == null) {
            RestAdapter restAdapter = new RestAdapter
                    .Builder()
                    .setClient(new OkClient(client))
                    .setEndpoint(sessionManager.getBaseURL())
                    .setErrorHandler(new CustomErrorHandler())
                    .build();
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
            apiInterface = restAdapter.create(APIInterface.class);
        }
        return apiInterface;
    }

    public static APIInterface resetAndGetAPI(Context context) {
        SessionManager sessionManager = new SessionManager(context);
        apiInterface=null;
        RestAdapter restAdapter = new RestAdapter
                    .Builder()
                    .setClient(new OkClient(client))
                    .setEndpoint(sessionManager.getBaseURL())
                    .setErrorHandler(new CustomErrorHandler())
                    .build();
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        apiInterface = restAdapter.create(APIInterface.class);
        return apiInterface;
    }

    public interface APIInterface {

        @POST(ApiUrl.CITIZEN_REGISTER)
        void registerUser(@Body RegisterRequest registerRequest,
                          Callback<JsonObject> jsonObjectCallback);

        @FormUrlEncoded
        @POST(ApiUrl.CITIZEN_LOGIN)
        void login(@Header("Authorization") String authorization, @Field("username") String username,
                   @Field("scope") String scope,
                   @Field("password") String password,
                   @Field("grant_type") String grant_type,
                   Callback<JsonObject> jsonObjectCallback);

        @FormUrlEncoded
        @POST(ApiUrl.CITIZEN_LOGOUT)
        void logout(@Field("access_token") String access_token,
                    Callback<JsonObject> jsonObjectCallback);

        @GET(ApiUrl.CITIZEN_GET_MY_COMPLAINT)
        void getMyComplaints(@Path(value = "page", encode = false) String pages,
                             @Path(value = "pageSize", encode = false) String pagesize,
                             @Query(value = "access_token", encodeValue = false) String access_token,
                             Callback<GrievanceAPIResponse> complaintAPIResponseCallback);

        @GET(ApiUrl.COMPLAINT_LATEST)
        void getLatestComplaints(@Path(value = "page", encode = false) String pages,
                                 @Path(value = "pageSize", encode = false) String pagesize,
                                 @Query(value = "access_token", encodeValue = false) String access_token,
                                 Callback<GrievanceAPIResponse> complaintAPIResponseCallback);

        @GET(ApiUrl.COMPLAINT_GET_TYPES)
        void getComplaintTypes(@Query(value = "access_token", encodeValue = false) String access_token,
                               Callback<GrievanceTypeAPIResponse> grievanceTypeAPIResponseCallback);

        @GET(ApiUrl.COMPLAINT_HISTORY)
        void getComplaintHistory(@Path(value = "complaintNo", encode = false) String complaintNo,
                                 @Query(value = "access_token", encodeValue = false) String access_token,
                                 Callback<GrievanceCommentAPIResponse> grievanceCommentAPIResponseCallback);

        @GET(ApiUrl.COMPLAINT_GET_LOCATION_BY_NAME)
        void getComplaintLocation(@Query(value = "locationName", encodeValue = false) String location,
                                  @Query(value = "access_token", encodeValue = false) String access_token,
                                  Callback<GrievanceLocationAPIResponse> grievanceLocationAPIResponseCallback);

        @POST(ApiUrl.COMPLAINT_CREATE)
        void createComplaint(@Body MultipartTypedOutput output,
                             @Query(value = "access_token", encodeValue = false) String access_token,
                             Callback<JsonObject> jsonObjectCallback);

        @PUT(ApiUrl.COMPLAINT_UPDATE_STATUS)
        void updateGrievance(@Path(value = "complaintNo", encode = false) String complaintNo,
                             @Body GrievanceUpdate grievanceUpdate,
                             @Query(value = "access_token", encodeValue = false) String access_token,
                             Callback<JsonObject> jsonObjectCallback);

        @POST(ApiUrl.PROPERTY_TAX_DETAILS)
        void getPropertyTax(@Header("Referer") String referer,
                            @Body PropertyTaxRequest propertyTaxRequest,
                            Callback<PropertyTaxCallback> taxCallback);

        @POST(ApiUrl.WATER_TAX_DETAILS)
        void getWaterTax(@Header("Referer") String referer,
                         @Body WaterTaxRequest waterTaxRequest,
                         Callback<WaterTaxCallback> taxCallback);

        @FormUrlEncoded
        @POST(ApiUrl.CITIZEN_ACTIVATE)
        void activate(@Field("userName") String username,
                      @Field("activationCode") String activationCode,
                      Callback<JsonObject> jsonObjectCallback);

        @FormUrlEncoded
        @POST(ApiUrl.CITIZEN_SEND_OTP)
        void sendOTP(@Field("identity") String identity,
                     Callback<JsonObject> jsonObjectCallback);

        @FormUrlEncoded
        @POST(ApiUrl.CITIZEN_PASSWORD_RECOVER)
        void recoverPassword(@Field("identity") String identity,
                             @Field("redirectURL") String redirectURL,
                             Callback<JsonObject> jsonObjectCallback);

        @GET(ApiUrl.CITIZEN_GET_PROFILE)
        void getProfile(@Query(value = "access_token", encodeValue = false) String access_token,
                        Callback<ProfileAPIResponse> profileAPIResponseCallback);

        @PUT(ApiUrl.CITIZEN_UPDATE_PROFILE)
        void updateProfile(@Body Profile profile,
                           @Query(value = "access_token", encodeValue = false) String access_token,
                           Callback<ProfileAPIResponse> profileAPIResponseCallback);
    }


}

