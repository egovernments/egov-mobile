package org.egovernments.egoverp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.adapters.PaymentHistoryAdapter;
import org.egovernments.egoverp.api.ApiController;
import org.egovernments.egoverp.config.Config;
import org.egovernments.egoverp.helper.AppUtils;
import org.egovernments.egoverp.helper.SetOnListItemClickListener;
import org.egovernments.egoverp.models.PaymentHistoryRequest;
import org.egovernments.egoverp.models.Transaction;
import org.egovernments.egoverp.services.DownloadService;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tr.xip.errorview.ErrorView;

public class PaymentHistoryActivity extends BaseActivity {

    public static final String CONSUMER_CODE = "CONSUMER_CODE";
    public static final String SERVICE_NAME = "SERVICE_NAME";
    public static final String REFERRER_IP = "REFERRER_IP";
    private static final int PERMISSION_REQUEST_CODE = 152;
    RecyclerView paymentHistoryRecyclerView;
    ProgressBar progressBar;
    SetOnListItemClickListener setOnListItemClickListener;
    PaymentHistoryAdapter paymentHistoryAdapter;
    int downloadRequestedPos = -1;

    ErrorView errorView;

    CardView cardNoPaymentHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_payment_history);
        setContentView(R.layout.activity_payment_history);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String consumerCode = getIntent().getStringExtra(CONSUMER_CODE);
        final PaymentHistoryRequest.ServiceName serviceName = PaymentHistoryRequest.ServiceName.
                valueOf(getIntent().getStringExtra(SERVICE_NAME));
        final String referrerIp = getIntent().getStringExtra(REFERRER_IP);

        paymentHistoryRecyclerView = (RecyclerView) findViewById(R.id.paymentHistoryRecyclerView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        errorView = (ErrorView) findViewById(R.id.errorView);
        cardNoPaymentHistory = (CardView) findViewById(R.id.cardNoPaymentHistory);

        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {
                loadPaymentHistory(referrerIp, consumerCode, serviceName);
            }
        });

        setOnListItemClickListener = new SetOnListItemClickListener() {
            @Override
            public void click(int pos) {
                downloadRequestedPos = pos;
                if (AppUtils.checkPermission(PaymentHistoryActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    startDownloadReceipt(pos);
                } else {
                    AppUtils.requestPermission(PaymentHistoryActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                }
            }
        };

        loadPaymentHistory(referrerIp, consumerCode, serviceName);

    }

    private void changeVisibility(int cardNoHistoryVisibility, int errorViewVisibility, int progressbarVisibility) {
        cardNoPaymentHistory.setVisibility(cardNoHistoryVisibility);
        errorView.setVisibility(errorViewVisibility);
        progressBar.setVisibility(progressbarVisibility);
    }

    void loadPaymentHistory(String referrerIp, String consumerCode, PaymentHistoryRequest.ServiceName serviceName) {

        changeVisibility(View.GONE, View.GONE, View.VISIBLE);

        PaymentHistoryRequest paymentHistoryRequest = new PaymentHistoryRequest(String.format(Locale.getDefault(), "%04d", sessionManager.getUrlLocationCode()),
                "", serviceName, consumerCode);
        Call<ArrayList<Transaction>> paymentHistoryCall = ApiController.getRetrofit2API(getApplicationContext())
                .getPaymentHistory(referrerIp, paymentHistoryRequest);

        paymentHistoryCall.enqueue(new Callback<ArrayList<Transaction>>() {
            @Override
            public void onResponse(Call<ArrayList<Transaction>> call, Response<ArrayList<Transaction>> response) {


                if (response.body().size() == 0 || (response.body().size() == 1 && TextUtils.isEmpty(response.body().get(0).getReferenceNo()))) {
                    paymentHistoryRecyclerView.setVisibility(View.GONE);
                    changeVisibility(View.VISIBLE, View.GONE, View.GONE);
                } else {
                    changeVisibility(View.GONE, View.GONE, View.GONE);
                    paymentHistoryAdapter = new PaymentHistoryAdapter(PaymentHistoryActivity.this, response.body(), setOnListItemClickListener);
                    paymentHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(PaymentHistoryActivity.this));
                    paymentHistoryRecyclerView.setAdapter(paymentHistoryAdapter);
                    paymentHistoryRecyclerView.addItemDecoration(new DividerItemDecoration(PaymentHistoryActivity.this,
                            DividerItemDecoration.VERTICAL));
                }

            }

            @Override
            public void onFailure(Call<ArrayList<Transaction>> call, Throwable t) {
            }
        });

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

    void startDownloadReceipt(final int pos) {

        if (downloadRequestedPos < 0 && paymentHistoryAdapter == null) {
            return;
        }

        try {

            Transaction transaction = paymentHistoryAdapter.getTransactionByIndex(pos);

            String referrerIp = configManager.getString(Config.REFERER_IP_CONFIG_KEY);
            String fileName = transaction.getReceiptNo().replaceAll(Matcher.quoteReplacement(File.separator), "-");

            Intent intent = new Intent(this, DownloadService.class);
            intent.putExtra(DownloadService.DOWNLOAD_FILE_NAME_WITH_EXT, fileName + ".pdf");
            intent.putExtra(DownloadService.REFERRER_IP, referrerIp);
            intent.putExtra(DownloadService.ULB_CODE, String.valueOf(sessionManager.getUrlLocationCode()));
            intent.putExtra(DownloadService.RECEIPT_NO, transaction.getReceiptNo());
            intent.putExtra(DownloadService.REFERENCE_NO, transaction.getReferenceNo());
            startService(intent);
            Toast.makeText(getApplicationContext(), R.string.download_start_msg, Toast.LENGTH_SHORT).show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startDownloadReceipt(downloadRequestedPos);
                } else {
                    showSnackBar(getString(R.string.permission_denied));
                }
                break;
        }
    }

    @Override
    public void errorOccurred(String errorMsg, int errorCode) {
        super.errorOccurred(errorMsg, errorCode);
        if (paymentHistoryRecyclerView.getAdapter() == null) {
            errorView.setError(errorCode);
            changeVisibility(View.GONE, View.VISIBLE, View.GONE);
        }
    }
}
