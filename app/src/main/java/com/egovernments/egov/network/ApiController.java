package com.egovernments.egov.network;


import android.content.Context;

import com.egovernments.egov.models.City;
import com.egovernments.egov.models.GrievanceAPIResponse;
import com.egovernments.egov.models.GrievanceCommentAPIResponse;
import com.egovernments.egov.models.GrievanceLocationAPIResponse;
import com.egovernments.egov.models.GrievanceTypeAPIResponse;
import com.egovernments.egov.models.GrievanceUpdate;
import com.egovernments.egov.models.Profile;
import com.egovernments.egov.models.ProfileAPIResponse;
import com.egovernments.egov.models.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.MultipartTypedOutput;

public class ApiController {

    private static LoginInterface loginInterface = null;

    private static APIInterface APIInterface = null;

    private final static OkHttpClient client = SSLTrustManager.createClient();

    private static SessionManager sessionManager;

    public static String getCityURL(String url) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();

        return response.body().string();

    }

    public static List<City> getAllCitiesURL(String url) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful())
            throw new IOException();
        Type type = new TypeToken<List<City>>() {
        }.getType();
        return new Gson().fromJson(response.body().charStream(), type);

    }

    //Sets up the API client
    public static APIInterface getAPI(Context context) {
        sessionManager = new SessionManager(context);
        if (APIInterface == null) {

            RestAdapter restAdapter = new RestAdapter
                    .Builder()
                    .setClient(new OkClient(client))
                    .setEndpoint(sessionManager.getBaseURL())
                    .setErrorHandler(new CustomErrorHandler())
                    .build();
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
            APIInterface = restAdapter.create(APIInterface.class);
        }
        return APIInterface;
    }

    public interface APIInterface {

        @POST(ApiUrl.CITIZEN_REGISTER)
        void registerUser(@Body User user,
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


    //Separate interface for login calls as base url differs slightly, and as request interceptor must be set to add header
    public static LoginInterface getLoginAPI(Context context) {

        sessionManager = new SessionManager(context);
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setClient(new OkClient(client))
                .setErrorHandler(new CustomErrorHandler())
                .setEndpoint(sessionManager.getBaseURL());

        builder.setRequestInterceptor(new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                //noinspection SpellCheckingInspection
                request.addHeader("Authorization", "Basic ZWdvdi1hcGk6ZWdvd i1hcGk=");
            }
        });
        RestAdapter restAdapter = builder.build();
        restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        loginInterface = restAdapter.create(LoginInterface.class);
        APIInterface = null;

        return loginInterface;
    }

    public interface LoginInterface {

        @FormUrlEncoded
        @POST(ApiUrl.CITIZEN_LOGIN)
        void login(@Field("username") String username,
                   @Field("scope") String scope,
                   @Field("password") String password,
                   @Field("grant_type") String grant_type,
                   Callback<JsonObject> jsonObjectCallback);

    }


}

