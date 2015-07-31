package org.egov.android.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.egov.android.R;
import org.egov.android.common.ReflectionUtil;
import org.egov.android.data.cache.Cache;
import org.egov.android.data.cache.CacheDAO;
import org.egov.android.listener.Event;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class ApiClient extends AsyncTask<Void, Integer, ApiResponse> implements IApiClient {

    private final static String TAG = ApiClient.class.getName();

    private List<IApiListener> apiListeners = null;

    private ApiMethod apiMethod = null;
    private Cache cache = null;
    private Context context = null;
    private boolean showSpinner = true;

    Dialog dialog = null;

    public ApiClient(ApiMethod apiMethod) {
        this.apiMethod = apiMethod;
        apiListeners = new ArrayList<IApiListener>();
    }

    public ApiMethod getApiMethod() {
        return apiMethod;
    }

    public ApiClient setApiMethod(ApiMethod apiMethod) {
        this.apiMethod = apiMethod;
        return this;
    }

    public Context getContext() {
        return context;
    }

    public ApiClient setContext(Context context) {
        this.context = context;
        return this;
    }

    public boolean isShowSpinner() {
        return showSpinner;
    }

    public ApiClient setShowSpinner(boolean showSpinner) {
        this.showSpinner = showSpinner;
        return this;
    }

    public ApiClient addListener(IApiListener listener) {
        apiListeners.add(listener);
        return this;
    }

    public Cache getCache() {
        return cache;
    }

    public ApiClient setCache(Cache cache) {
        this.cache = cache;
        return this;
    }

    @Override
    public void call() {
        execute();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ApiStatus.isError = false;
        String url = apiMethod.getFullUrl();
        CacheDAO cacheDao = new CacheDAO();
        if (this.cache != null && cacheDao.hasData(url)) {
            Cache cache = cacheDao.get(url);
            this.triggerEvent(new ApiResponse(cache.getData().toString(), this.apiMethod, "cache"));
            cancel(true);
            return;
        }
        if (isShowSpinner()) {
            if (dialog == null) {
                dialog = new Dialog(context, R.style.DialogTheme);
                dialog.setContentView(R.layout.custom_loading);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.show();
            }
        }
    }

    protected void triggerEvent(ApiResponse response) {
        Event<ApiResponse> event = new Event<ApiResponse>();
        ReflectionUtil.setFieldData(event, "data", response);
        Iterator<IApiListener> it = apiListeners.iterator();
        while (it.hasNext()) {
            IApiListener listener = it.next();
            listener.onResponse(event);
        }
    }

    @Override
    protected void onPostExecute(ApiResponse response) {
        super.onPostExecute(response);
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        /**
         * below has to go into CacheDAO model.
         */
        if (!response.hasData()) {
            return;
        }

        if (this.cache != null && !ApiStatus.isError) {
            CacheDAO cacheDao = new CacheDAO();
            Cache cache = new Cache();
            cache.setUrl(apiMethod.getFullUrl()).setData(response.getResponse()).setDuration(1000);
            cacheDao.setModel(cache);
            cacheDao.save();
        }
        /**
         * get cache duration from config
         */

        this.triggerEvent(response);
    }

    @Override
    protected ApiResponse doInBackground(Void... params) {
        String url = apiMethod.getApiUrl().getUrl(true) + apiMethod.getExtraParam();

        HttpURLConnection con = null;
        RequestMethod method = apiMethod.getMethod();
        String content = "";
        try {

            String urlParams = apiMethod.getQueryParameter();

            if (method.equals(RequestMethod.GET) && !urlParams.equalsIgnoreCase("")) {
                url += "?" + urlParams;
            } else {
                url += "?" + apiMethod.getAccessTokenQry();
            }
            Log.d(TAG, "===================" + url);

            con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod(method.toString());
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setRequestProperty("Content-Type", "application/" + apiMethod.getQueryType());
            con.setRequestProperty("Accept-Encoding", "gzip");

            Set<Entry<String, String>> headerSet = apiMethod.getHeaders().entrySet();
            for (Entry<String, String> obj : headerSet) {
                con.addRequestProperty(obj.getKey(), obj.getValue());
            }

            if (method.equals(RequestMethod.POST) || method.equals(RequestMethod.PUT)) {
                Log.d(TAG, "Params : " + apiMethod.getPostParameter());
                con.setDoOutput(true);
                OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());

                if (apiMethod.getQueryType().equals("json")) {
                    out.write(apiMethod.getPostParameter());
                } else {
                    out.write(apiMethod.getQueryParameter());
                }
                out.close();
            }

            InputStream is = null;
            int status = 0;
            try {
                status = con.getResponseCode();
            } catch (IOException e) {
                status = con.getResponseCode();
            }
            Log.d(TAG, "========status===========" + status);
            String encoding = con.getContentEncoding() == null ? "" : con.getContentEncoding();

            if (status == 200 || status == 201) {
                is = encoding.equalsIgnoreCase("gzip") ? new GZIPInputStream(con.getInputStream())
                        : con.getInputStream();
            } else {
                is = encoding.equalsIgnoreCase("gzip") ? new GZIPInputStream(con.getErrorStream())
                        : con.getErrorStream();
                ApiStatus.isError = true;
            }

            InputStreamReader input = new InputStreamReader(is);

            char[] data = new char[1024];
            int count = 0;
            StringBuffer sb = new StringBuffer();
            while ((count = input.read(data)) != -1) {
                sb.append(data, 0, count);
            }
            input.close();
            content = sb.toString();
            Log.d(TAG, content);
        } catch (Exception e) {
            ApiStatus.isError = true;
            Log.d(TAG, e.getMessage(), e);
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return new ApiResponse(content, this.apiMethod, "live");
    }
}
