package org.egovernments.egoverp.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.helper.SetOnListItemClickListener;
import org.egovernments.egoverp.models.Transaction;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Payment History Page
 */

public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.PaymentHistoryHolder> {

    private Context context;
    private ArrayList<Transaction> transactions;
    private NumberFormat nf1 = NumberFormat.getInstance(new Locale("hi", "IN"));

    private SetOnListItemClickListener setOnListItemClickListener;

    public PaymentHistoryAdapter(Context context, ArrayList<Transaction> transactions, SetOnListItemClickListener setOnListItemClickListener) {
        this.context = context;
        this.transactions = transactions;
        this.setOnListItemClickListener = setOnListItemClickListener;
    }

    @Override
    public PaymentHistoryAdapter.PaymentHistoryHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_payment_history_list, viewGroup, false);
        return new PaymentHistoryHolder(v);
    }

    @Override
    public void onBindViewHolder(final PaymentHistoryHolder holder, int position) {
        final Transaction transaction = transactions.get(position);
        holder.tvPayeeName.setText(transaction.getPayeeName());
        holder.tvPaidAmount.setText(context.getString(R.string.rupee_value, nf1.format(transaction.getAmount())));
        holder.tvPaidDate.setText(transaction.getTxnDate());
        GradientDrawable bgShape = (GradientDrawable) holder.tvStatus.getBackground();
        Integer color = ContextCompat.getColor(context, R.color.yellow);
        holder.tvReceiptInfo.setVisibility(View.GONE);
        holder.btnDownload.setVisibility(View.GONE);

        if (transaction.getReceiptStatus().equals(Transaction.ReceiptStatus.APPROVED.getValue()) ||
                transaction.getReceiptStatus().equals(Transaction.ReceiptStatus.SUBMITTED.getValue()) ||
                transaction.getReceiptStatus().equals(Transaction.ReceiptStatus.REMITTED.getValue()) ||
                transaction.getReceiptStatus().equals(Transaction.ReceiptStatus.TO_BE_SUBMITTED.getValue())) {
            color = ContextCompat.getColor(context, R.color.green);
            holder.tvReceiptInfo.setText("Receipt #" + transaction.getReceiptNo());
            holder.tvReceiptInfo.setVisibility(View.VISIBLE);
            holder.btnDownload.setVisibility(View.VISIBLE);
            holder.btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setOnListItemClickListener.click(holder.getAdapterPosition());
                }
            });

            holder.tvStatus.setText("SUCCESS");

        } else if (transaction.getReceiptStatus().equals(Transaction.ReceiptStatus.FAILED.getValue()) ||
                transaction.getReceiptStatus().equals(Transaction.ReceiptStatus.CANCELLED.getValue())) {
            color = ContextCompat.getColor(context, R.color.red);
            holder.tvStatus.setText("FAILURE");
        } else {
            holder.tvStatus.setText("PENDING FOR RECONSIDERATION");
        }

        bgShape.setColor(color);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public Transaction getTransactionByIndex(int pos) {
        return transactions.get(pos);
    }

    static class PaymentHistoryHolder extends RecyclerView.ViewHolder {

        TextView tvPayeeName;
        TextView tvPaidDate;
        TextView tvReceiptInfo;
        TextView tvPaidAmount;
        TextView tvStatus;

        Button btnDownload;

        PaymentHistoryHolder(View itemView) {
            super(itemView);
            tvPayeeName = (TextView) itemView.findViewById(R.id.tvPayeeName);
            tvPaidDate = (TextView) itemView.findViewById(R.id.tvPaidDate);
            tvReceiptInfo = (TextView) itemView.findViewById(R.id.tvReceiptInfo);
            tvPaidAmount = (TextView) itemView.findViewById(R.id.tvPaidAmount);
            tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);
            btnDownload = (Button) itemView.findViewById(R.id.btnDownload);
        }

    }

}
