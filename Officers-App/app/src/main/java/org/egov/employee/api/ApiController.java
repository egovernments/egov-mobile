package org.egov.employee.api;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.egov.employee.config.AppPreference;
import org.egov.employee.data.ComplaintViewAPIResponse;
import org.egov.employee.data.TaskAPIResponse;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
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

        Log.v("URL --------->", preference.getActiveCityUrl());

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

        @GET(ApiUrl.PGR_COMPLAINT_DETAILS)
        void getComplaintDetails(@Path(value = "complaintNo") String complaintNo,
                                 @Query(value = "access_token") String access_token,
                                 Callback<ComplaintViewAPIResponse> grievanceCommentAPIResponseCallback);


    }


}