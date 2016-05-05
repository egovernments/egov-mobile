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

package org.egovernments.egoverp.application;

import android.app.Application;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Response;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.egovernments.egoverp.network.SSLTrustManager;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

/**
 * Performs setup for some frequently used functions of the app
 **/

public class EgovAppConfig extends Application {

    private final OkHttpClient client = SSLTrustManager.createClient();

    @Override
    public void onCreate() {
        super.onCreate();

        //Creates an interceptor to force image caching no matter the server instructions
        Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .header("Cache-Control", "max-age=60")
                        .build();
            }
        };

        //Retrieves the app cache directory and sets up a cache for the OkHttpClient
        File cacheDir = this.getExternalCacheDir();
        if (cacheDir == null) {
            // Fall back to using the internal cache directory
            cacheDir = this.getCacheDir();
        }
        client.setCache(new Cache(cacheDir, 100 * 1024 * 1024));
        client.setProtocols(Collections.singletonList(Protocol.HTTP_1_1));
        client.networkInterceptors().add(REWRITE_CACHE_CONTROL_INTERCEPTOR);

        //Sets up the picasso global singleton instance
        Picasso.Builder builder = new Picasso.Builder(this);
        Picasso picasso = builder
                .downloader(new OkHttpDownloader(client))
                .build();

        Picasso.setSingletonInstance(picasso);

    }
}