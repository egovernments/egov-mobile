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

package org.egovernments.egoverp.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.config.Config;
import org.egovernments.egoverp.config.SessionManager;
import org.egovernments.egoverp.helper.AppUtils;
import org.egovernments.egoverp.services.DownloadService;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

@SuppressLint("SetJavaScriptEnabled")
public class PaymentGatewayActivity extends AppCompatActivity {

    public static String PAYMENT_GATEWAY_URL="paymentGatewayURL";
    ProgressBar progressBar;
    WebView webView;
    String referrerIp;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_gateway);
        String url=getIntent().getStringExtra(PAYMENT_GATEWAY_URL);
        sessionManager = new SessionManager(getApplicationContext());
        try {
            referrerIp = AppUtils.getConfigManager(getApplicationContext()).getString(Config.REFERER_IP_CONFIG_KEY);
        } catch (IOException e) {
            e.printStackTrace();
        }
        webView = (WebView) findViewById(R.id.webview);

        progressBar=(ProgressBar)findViewById(R.id.progressBar);

        PaymentGatewayInterface paymentGatewayInterface = new PaymentGatewayInterface(this);
        webView.addJavascriptInterface(paymentGatewayInterface, "CitizenApp");

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }
        });

        webView.clearCache(true);
        webView.clearHistory();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.loadUrl(url);

    }


    class PaymentGatewayInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        PaymentGatewayInterface(Context c) {
            mContext = c;
        }

        @SuppressLint("AddJavascriptInterface")
        @SuppressWarnings("unused")
        @JavascriptInterface
        public void downloadReceipt(String receiptNo, String referenceNo) {
            String fileName = receiptNo.replaceAll(Matcher.quoteReplacement(File.separator), "-");
            Intent intent = new Intent(PaymentGatewayActivity.this, DownloadService.class);
            intent.putExtra(DownloadService.DOWNLOAD_FILE_NAME_WITH_EXT, fileName + ".pdf");
            intent.putExtra(DownloadService.REFERRER_IP, referrerIp);
            intent.putExtra(DownloadService.ULB_CODE, String.valueOf(sessionManager.getUrlLocationCode()));
            intent.putExtra(DownloadService.RECEIPT_NO, receiptNo);
            intent.putExtra(DownloadService.REFERENCE_NO, referenceNo);
            startService(intent);
            Toast.makeText(getApplicationContext(), R.string.download_start_msg, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void showSnackBar(String message) {
            final Snackbar snackBar = Snackbar.make(findViewById(R.id.contentView), message, Snackbar.LENGTH_LONG);
            View snackbarView = snackBar.getView();
            TextView tv = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            tv.setMaxLines(5);
            snackBar.setAction(R.string.dismiss, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackBar.dismiss();
                }
            });
            snackBar.show();
        }

    }
}
