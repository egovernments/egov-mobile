package org.egovernments.egoverp.api;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Platform;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Logging HTTP REQUEST and RESPONSE
 */

public class Interceptor implements okhttp3.Interceptor {

    public static final String BROADCAST_ERROR = "BROADCAST_ERROR";
    public static final String DATA_UNAUTHORIZED_ERROR = "DATA_UNAUTHORIZED_ERROR";
    public static final String DATA_ERROR_MSG = "DATA_ERROR_MSG";
    public static final String DATA_ERROR_CODE = "DATA_ERROR_CODE";
    public static final String SESSION_ERROR_MSG = "Have we met before? Please reintroduce with your credentials";
    public static final String OTHER_AUTH_ERROR = "We're sorry something went wrong please try to logout and login again";
    public static final String SERVER_DOWN_MESSAGE = "You may addicted to our app. We care about your health and we want give you a break for a few hours Will be back soon!!!";
    public static final String GENERIC_ERROR_MSG = "Something went wrong please try again later";
    public static final String NO_INTERNET_CONNECTIVITY = "No internet connectivity";
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private final Interceptor.Logger logger;
    private Level level = Level.NONE;
    private Context context;

    public Interceptor(Context context) {
        this(Logger.DEFAULT);
        this.context = context;
    }

    private Interceptor(Interceptor.Logger logger) {
        this.logger = logger;
    }

    private static String requestPath(okhttp3.HttpUrl url) {
        String path = url.encodedPath();
        String query = url.encodedQuery();
        return query != null ? (path + '?' + query) : path;
    }

    /**
     * Change the level at which this interceptor logs.
     */
    public void setLevel(Level level) {
        this.level = level;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = null;
        boolean isErrorThrown = false;
        try {
            Interceptor.Level level = this.level;

            Request request = chain.request();

            if (level == Level.NONE) {
                return chain.proceed(request);
            }

            boolean logBody = level == Interceptor.Level.BODY;
            boolean logHeaders = logBody || level == Interceptor.Level.HEADERS;

            RequestBody requestBody = request.body();
            boolean hasRequestBody = requestBody != null;

            String requestStartMessage =
                    "--> " + request.method() + ' ' + requestPath(request.url());
            if (!logHeaders && hasRequestBody) {
                requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
            }
            logger.log(requestStartMessage);

            if (logHeaders) {
                Headers headers = request.headers();
                for (int i = 0, count = headers.size(); i < count; i++) {
                    logger.log(headers.name(i) + ": " + headers.value(i));
                }

                String endMessage = "--> END " + request.method();
                if (logBody && hasRequestBody) {
                    Buffer buffer = new Buffer();
                    requestBody.writeTo(buffer);

                    Charset charset = UTF8;
                    MediaType contentType = requestBody.contentType();
                    if (contentType != null) {
                        contentType.charset(UTF8);
                    }

                    logger.log("");
                    logger.log(buffer.readString(charset));

                    endMessage += " (" + requestBody.contentLength() + "-byte body)";
                }
                logger.log(endMessage);
            }

            long startNs = System.nanoTime();
            response = chain.proceed(request);
            long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

            ResponseBody responseBody = response.body();
            logger.log("<-- " + response.protocol() + ' ' + response.code() + ' '
                    + response.message() + " (" + tookMs + "ms"
                    + (!logHeaders ? ", " + responseBody.contentLength() + "-byte body" : "") + ')');

            if (logHeaders) {
                Headers headers = response.headers();
                for (int i = 0, count = headers.size(); i < count; i++) {
                    logger.log(headers.name(i) + ": " + headers.value(i));
                }

                String endMessage = "<-- END HTTP";
                if (logBody) {
                    BufferedSource source = responseBody.source();
                    source.request(Long.MAX_VALUE); // Buffer the entire body.
                    Buffer buffer = source.buffer();

                    Charset charset = UTF8;
                    MediaType contentType = responseBody.contentType();
                    if (contentType != null) {
                        charset = contentType.charset(UTF8);
                    }

                    if (responseBody.contentLength() != 0) {
                        logger.log("");
                        logger.log(buffer.clone().readString(charset));
                    }

                    endMessage += " (" + buffer.size() + "-byte body)";
                }
                logger.log(endMessage);
            }


            if (!response.isSuccessful()) {
                isErrorThrown = true;
                throw new IOException(errorHandlerFromResponse(response.body().string(), response.code()));
            }

            return response;
        } catch (IOException ex) {
            ex.printStackTrace();
            if (!isErrorThrown) {
                throw errorHandlerFromResponse(ex, (response == null ? 0 : response.code()));
            } else
                throw ex;
        }
    }


    private void sendErrorToBroadCast(String errorMessage, int errorCode, Boolean isUnauthorizedError) {

        if (this.context != null) {
            //send error to broadcast listeners
            Intent broadCastIntent = new Intent(BROADCAST_ERROR);
            broadCastIntent.putExtra(DATA_UNAUTHORIZED_ERROR, isUnauthorizedError);
            broadCastIntent.putExtra(DATA_ERROR_MSG, errorMessage);
            broadCastIntent.putExtra(DATA_ERROR_CODE, errorCode);
            LocalBroadcastManager.getInstance(this.context).sendBroadcast(broadCastIntent);
        }

    }

    private IOException errorHandlerFromResponse(IOException ex, int errorCode) {
        //send error to broadcast listeners
        String errorMessage = NO_INTERNET_CONNECTIVITY;
        if (ex != null && ex instanceof UnknownHostException) {
            errorMessage = NO_INTERNET_CONNECTIVITY;
        } else if (ex != null && !TextUtils.isEmpty(ex.getMessage()) && ex.getMessage().equals("Canceled")) {
            errorMessage = "Canceled";
        }
        sendErrorToBroadCast(errorMessage, errorCode, errorCode == 401);
        return ex;
    }

    private String errorHandlerFromResponse(String errorBody, int responseCode) {
        String errorMsg = errorBody;

        boolean isSessionExpired = false;

        JsonObject jsonObject = null;
        try {
            jsonObject = new JsonParser().parse(errorBody).getAsJsonObject();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        switch (responseCode) {
            case 400:
                if (jsonObject != null) {
                    //If failure due to invalid access token
                    if (jsonObject.has("error_description")) {
                        errorMsg = jsonObject.get("error_description").getAsString();
                    } else {
                        if (jsonObject.has("status")) {
                            errorMsg = jsonObject.get("status").getAsJsonObject().get("message").getAsString();
                        }
                    }
                }
                break;
            case 401:
                if (jsonObject != null) {
                    //If failure due to invalid access token
                    errorMsg = jsonObject.get("error_description").toString().trim();
                    if (errorMsg.contains("Invalid access token")) {
                        errorMsg = SESSION_ERROR_MSG;
                        isSessionExpired = true;
                    } else {
                        errorMsg = OTHER_AUTH_ERROR;
                    }
                }
                break;
            case 404:
                errorMsg = SERVER_DOWN_MESSAGE;
                break;
            case 503:
                errorMsg = SERVER_DOWN_MESSAGE;
                break;
            case 504:
                errorMsg = SERVER_DOWN_MESSAGE;
                break;
            default:
                errorMsg = GENERIC_ERROR_MSG;
                break;
        }

        sendErrorToBroadCast(errorMsg, responseCode, isSessionExpired);

        return errorMsg;

    }

    enum Level {
        /**
         * No logs.
         */
        NONE,
        /**
         * Logs request and response lines.
         * <p>
         * Example:
         * <pre>{@code
         * --> POST /greeting HTTP/1.1 (3-byte body)
         *
         * <-- HTTP/1.1 200 OK (22ms, 6-byte body)
         * }</pre>
         */
        BASIC,
        /**
         * Logs request and response lines and their respective headers.
         * <p>
         * Example:
         * <pre>{@code
         * --> POST /greeting HTTP/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         * --> END POST
         *
         * <-- HTTP/1.1 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         * <-- END HTTP
         * }</pre>
         */
        HEADERS,
        /**
         * Logs request and response lines and their respective headers and bodies (if present).
         * <p>
         * Example:
         * <pre>{@code
         * --> POST /greeting HTTP/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         *
         * Hi?
         * --> END GET
         *
         * <-- HTTP/1.1 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         *
         * Hello!
         * <-- END HTTP
         * }</pre>
         */
        BODY
    }


    interface Logger {
        /**
         * A {@link Interceptor.Logger} defaults output appropriate for the current platform.
         */
        Logger DEFAULT = new Logger() {
            @Override
            public void log(String message) {
                Platform.get().log(Log.INFO, message, null);
            }
        };

        void log(String message);
    }

    public interface ErrorListener {
        void errorOccurred(String errorMsg, int errorCode);

        void sessionTimeOutError();
    }
}
