package com.egovernments.egov.network;


import com.egovernments.egov.models.Complaint;
import com.egovernments.egov.models.GrievanceAPIResponse;
import com.egovernments.egov.models.GrievanceLocationAPIResponse;
import com.egovernments.egov.models.GrievanceTypeAPIResponse;
import com.egovernments.egov.models.Profile;
import com.egovernments.egov.models.ProfileAPIResponse;
import com.egovernments.egov.models.User;
import com.google.gson.JsonObject;
import com.squareup.okhttp.OkHttpClient;

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

public class ApiController {

    private static LoginInterface loginInterface = null;
    private static APIInterface APIInterface = null;

    private final static OkHttpClient client = SSLTrustManager.createClient();


    public static APIInterface getAPI() {
        if (APIInterface == null) {

            RestAdapter restAdapter = new RestAdapter.Builder().setClient(new OkClient(client)).setEndpoint(ApiUrl.api_baseUrl).build();
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
            APIInterface = restAdapter.create(APIInterface.class);
        }
        return APIInterface;
    }

    public interface APIInterface {

        @POST(ApiUrl.CITIZEN_REGISTER)
        void RegisterUser(@Body User user, Callback<JsonObject> jsonObjectCallback);

        @FormUrlEncoded
        @POST(ApiUrl.CITIZEN_LOGOUT)
        void Logout(@Field("access_token") String access_token, Callback<JsonObject> jsonObjectCallback);

        @GET(ApiUrl.CITIZEN_GET_MY_COMPLAINT)
        void getMyComplaints(@Path(value = "page", encode = false) String pages, @Path(value = "pageSize", encode = false) String pagesize, @Query(value = "access_token", encodeValue = false) String access_token, Callback<GrievanceAPIResponse> complaintAPIResponseCallback);

        @GET(ApiUrl.COMPLAINT_LATEST)
        void getLatestComplaints(@Path(value = "page", encode = false) String pages, @Path(value = "pageSize", encode = false) String pagesize, @Query(value = "access_token", encodeValue = false) String access_token, Callback<GrievanceAPIResponse> complaintAPIResponseCallback);

        @GET(ApiUrl.COMPLAINT_GET_TYPES)
        void getComplaintTypes(@Query(value = "access_token", encodeValue = false) String access_token, Callback<GrievanceTypeAPIResponse> grievanceTypeAPIResponseCallback);

        @GET(ApiUrl.COMPLAINT_GET_LOCATION_BY_NAME)
        void getComplaintLocation(@Query(value = "locationName", encodeValue = false) String location, @Query(value = "access_token", encodeValue = false) String access_token, Callback<GrievanceLocationAPIResponse> grievanceLocationAPIResponseCallback);

        @POST(ApiUrl.COMPLAINT_CREATE)
        void createComplaint(@Body Complaint complaint, @Query(value = "access_token", encodeValue = false) String access_token, Callback<JsonObject> jsonObjectCallback);

        @FormUrlEncoded
        @POST(ApiUrl.CITIZEN_ACTIVATE)
        void Activate(@Field("userName") String username, @Field("activationCode") String activationcode, Callback<JsonObject> jsonObjectCallback);

        @FormUrlEncoded
        @POST(ApiUrl.CITIZEN_SEND_OTP)
        void SendOTP(@Field("identity") String identity, Callback<JsonObject> jsonObjectCallback);

        @FormUrlEncoded
        @POST(ApiUrl.CITIZEN_PASSWORD_RECOVER)
        void Recover(@Field("identity") String identity, @Field("redirectURL") String redirectURL, Callback<JsonObject> jsonObjectCallback);

        @GET(ApiUrl.CITIZEN_GET_PROFILE)
        void getProfile(@Query(value = "access_token", encodeValue = false) String access_token, Callback<ProfileAPIResponse> profileAPIResponseCallback);

        @PUT(ApiUrl.CITIZEN_UPDATE_PROFILE)
        void updateProfile(@Body Profile profile, @Query(value = "access_token", encodeValue = false) String access_token, Callback<ProfileAPIResponse> profileAPIResponseCallback);
    }


    public static LoginInterface getLoginAPI() {
        if (loginInterface == null) {

            RestAdapter.Builder builder = new RestAdapter.Builder().setClient(new OkClient(client)).setEndpoint(ApiUrl.login_baseUrl);

            builder.setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("Authorization", "Basic ZWdvdi1hcGk6ZWdvd i1hcGk=");
                }
            });
            RestAdapter restAdapter = builder.build();
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
            loginInterface = restAdapter.create(LoginInterface.class);
        }
        return loginInterface;
    }

    public interface LoginInterface {

        @FormUrlEncoded
        @POST(ApiUrl.CITIZEN_LOGIN)
        void Login(@Field("username") String username, @Field("scope") String scope, @Field("password") String password, @Field("grant_type") String grant_type, Callback<JsonObject> jsonObjectCallback);

    }


}

