package com.egovernments.egov.application;

import android.app.Application;

import com.egovernments.egov.network.SSLTrustManager;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Response;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class EGovApp extends Application {

    private final OkHttpClient client = SSLTrustManager.createClient();

    @Override
    public void onCreate() {
        super.onCreate();

        Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .header("Cache-Control", "max-age=60")
                        .build();
            }
        };

        File cacheDir = this.getExternalCacheDir();
        if (cacheDir == null) {
            // Fall back to using the internal cache directory
            cacheDir = this.getCacheDir();
        }
        client.setCache(new Cache(cacheDir, 100 * 1024 * 1024));
        client.setProtocols(Collections.singletonList(Protocol.HTTP_1_1));
        client.networkInterceptors().add(REWRITE_CACHE_CONTROL_INTERCEPTOR);

        Picasso picasso = new Picasso.Builder(this)
                .downloader(new OkHttpDownloader(client))
                .build();

        Picasso.setSingletonInstance(picasso);

    }
}