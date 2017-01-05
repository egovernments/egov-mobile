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

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.helper.AppUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class PdfViewerActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_DOWNLOAD = 548;
    public static String PDF_URL="pdfURL";
    public static String PAGE_TITLE="pageTitle";

    ProgressBar progressBar;
    String url;
    String contentDisposition;
    String mimetype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        progressBar=(ProgressBar)findViewById(R.id.pbloading);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        http://mozilla.github.io/pdf.js/web/viewer.html


        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String pageTitle=getIntent().getStringExtra(PAGE_TITLE);

        getSupportActionBar().setTitle(pageTitle);*/

        String url="https://docs.google.com/viewer?url="+getIntent().getStringExtra(PDF_URL);

        WebView webView=(WebView)findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {

                if (AppUtils.checkPermission(PdfViewerActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    downloadFile(url, contentDisposition, mimetype);
                } else {
                    PdfViewerActivity.this.url = url;
                    PdfViewerActivity.this.contentDisposition = contentDisposition;
                    PdfViewerActivity.this.mimetype = mimetype;
                    AppUtils.requestPermission(PdfViewerActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_DOWNLOAD);
                }

            }
        });

        webView.loadUrl(url);
    }

    private void downloadFile(String url, String contentDisposition, String mimetype) {
        try {
            url = URLDecoder.decode(url, "UTF-8");

            Log.v("DOWNLOAD", url);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final String filename = URLUtil.guessFileName(url, contentDisposition, mimetype);

        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(url));

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        dm.enqueue(request);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT); //This is important!
        intent.addCategory(Intent.CATEGORY_OPENABLE); //CATEGORY.OPENABLE
        intent.setType("*/*");//any application,any extension
        Toast.makeText(getApplicationContext(), "Downloading File", //To notify the Client that the file is being downloaded
                Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @Nullable String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_DOWNLOAD:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadFile(this.url, this.contentDisposition, this.mimetype);
                } else {
                    Snackbar snackBar = Snackbar.make(findViewById(R.id.contentView), R.string.permission_denied, Snackbar.LENGTH_LONG);
                    snackBar.show();
                }
                break;
        }
    }


}
