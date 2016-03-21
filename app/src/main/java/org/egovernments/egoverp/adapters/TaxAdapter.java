/*
 *    eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (c) 2016  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egovernments.egoverp.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.models.TaxDetail;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TaxAdapter extends BaseAdapter {

    private WeakReference<Context> contextWeakReference;
    private List<TaxDetail> taxDetails;

    public TaxAdapter(List<TaxDetail> taxDetails, Context context) {
        this.taxDetails = taxDetails;
        this.contextWeakReference = new WeakReference<>(context);
    }

    @Override
    public int getCount() {
        return taxDetails.size();
    }

    @Override
    public Object getItem(int position) {
        return taxDetails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TaxViewHolder taxViewHolder = null;
        View view = convertView;
        if (convertView == null) {

            view = LayoutInflater.from(contextWeakReference.get()).inflate(R.layout.item_tax, parent, false);

            taxViewHolder = new TaxViewHolder();
            taxViewHolder.taxAmount = (TextView) view.findViewById(R.id.propertytax_taxamount);
            /*taxViewHolder.taxChequePenalty = (TextView) view.findViewById(R.id.propertytax_chequepenalty);*/
            taxViewHolder.taxPenalty = (TextView) view.findViewById(R.id.propertytax_penalty);
            taxViewHolder.taxInstallment = (TextView) view.findViewById(R.id.propertytax_installment);
            /*taxViewHolder.taxRebate = (TextView) view.findViewById(R.id.propertytax_rebate);*/
            taxViewHolder.taxTotal = (TextView) view.findViewById(R.id.propertytax_total);

            view.setTag(taxViewHolder);
        }

        if (taxViewHolder == null) {
            taxViewHolder = (TaxViewHolder) view.getTag();
        }
        TaxDetail taxDetail = (TaxDetail) getItem(position);

        NumberFormat nf1 = NumberFormat.getInstance(new Locale("hi","IN"));
        nf1.setMinimumFractionDigits(2);
        nf1.setMaximumFractionDigits(2);

        taxViewHolder.taxInstallment.setText(taxDetail.getInstallment());
        taxViewHolder.taxAmount.setText(nf1.format(taxDetail.getTaxAmount()));
        /*taxViewHolder.taxChequePenalty.setText("Cheque Bounce Penalty: Rs. " + taxDetail.getChqBouncePenalty());*/
        taxViewHolder.taxPenalty.setText(nf1.format(taxDetail.getPenalty()+taxDetail.getChqBouncePenalty()));
        /*taxViewHolder.taxRebate.setText("Rebate: Rs. " + taxDetail.getRebate());*/
        taxViewHolder.taxTotal.setText(nf1.format(taxDetail.getTotalAmount()));

        return view;
    }

    public static class TaxViewHolder {

        private TextView taxAmount;
        private TextView taxChequePenalty;
        private TextView taxPenalty;
        private TextView taxInstallment;
        private TextView taxRebate;
        private TextView taxTotal;
    }
}