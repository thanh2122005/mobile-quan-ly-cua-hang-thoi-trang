package com.example.quanlycuahangthoitrang;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycuahangthoitrang.model.Invoice;
import com.example.quanlycuahangthoitrang.utils.FormatUtils;

import java.util.List;

public class InvoiceHistoryAdapter extends RecyclerView.Adapter<InvoiceHistoryAdapter.ViewHolder> {

    private List<Invoice> invoiceList;
    private OnInvoiceClickListener listener;

    public interface OnInvoiceClickListener {
        void onDetailClick(Invoice invoice);
    }

    public InvoiceHistoryAdapter(List<Invoice> invoiceList, OnInvoiceClickListener listener) {
        this.invoiceList = invoiceList;
        this.listener = listener;
    }

    public void updateData(List<Invoice> newList) {
        this.invoiceList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invoice_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Invoice invoice = invoiceList.get(position);
        holder.tvInvoiceCode.setText(invoice.getCode());
        holder.tvInvoiceDate.setText("Ngày tạo: " + invoice.getDate());
        holder.tvInvoiceTotal.setText("Tổng: " + FormatUtils.formatPrice(invoice.getTotal()));
        holder.tvInvoiceStatus.setText(invoice.getStatus());

        holder.btnInvoiceDetail.setOnClickListener(v -> listener.onDetailClick(invoice));
    }

    @Override
    public int getItemCount() {
        return invoiceList == null ? 0 : invoiceList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInvoiceCode, tvInvoiceDate, tvInvoiceTotal, tvInvoiceStatus, btnInvoiceDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInvoiceCode = itemView.findViewById(R.id.tvInvoiceCode);
            tvInvoiceDate = itemView.findViewById(R.id.tvInvoiceDate);
            tvInvoiceTotal = itemView.findViewById(R.id.tvInvoiceTotal);
            tvInvoiceStatus = itemView.findViewById(R.id.tvInvoiceStatus);
            btnInvoiceDetail = itemView.findViewById(R.id.btnInvoiceDetail);
        }
    }
}
