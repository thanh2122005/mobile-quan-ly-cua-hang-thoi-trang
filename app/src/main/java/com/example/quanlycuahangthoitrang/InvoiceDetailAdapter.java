package com.example.quanlycuahangthoitrang;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycuahangthoitrang.model.InvoiceItem;
import com.example.quanlycuahangthoitrang.utils.FormatUtils;

import java.util.List;

public class InvoiceDetailAdapter extends RecyclerView.Adapter<InvoiceDetailAdapter.ViewHolder> {

    private List<InvoiceItem> itemList;

    public InvoiceDetailAdapter(List<InvoiceItem> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invoice_detail_product, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InvoiceItem item = itemList.get(position);
        holder.tvItemName.setText(item.getProduct().getName());
        holder.tvItemQuantityAndPrice.setText(item.getQuantity() + " x " + FormatUtils.formatPrice(item.getUnitPrice()));
        holder.tvItemTotal.setText(FormatUtils.formatPrice(item.getQuantity() * item.getUnitPrice()));
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvItemQuantityAndPrice, tvItemTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemQuantityAndPrice = itemView.findViewById(R.id.tvItemQuantityAndPrice);
            tvItemTotal = itemView.findViewById(R.id.tvItemTotal);
        }
    }
}
