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

public class InvoiceSelectedAdapter extends RecyclerView.Adapter<InvoiceSelectedAdapter.ViewHolder> {

    private List<InvoiceItem> itemList;
    private OnItemInteractionListener listener;

    public interface OnItemInteractionListener {
        void onIncrease(int position);
        void onDecrease(int position);
        void onDelete(int position);
    }

    public InvoiceSelectedAdapter(List<InvoiceItem> itemList, OnItemInteractionListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    public void updateData(List<InvoiceItem> newList) {
        this.itemList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invoice_selected, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InvoiceItem item = itemList.get(position);
        
        holder.tvInvoiceItemInfo.setText(item.getProduct().getName() + "\n" + FormatUtils.formatPrice(item.getUnitPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        holder.btnPlus.setOnClickListener(v -> listener.onIncrease(position));
        holder.btnMinus.setOnClickListener(v -> listener.onDecrease(position));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(position));
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInvoiceItemInfo, tvQuantity, btnPlus, btnMinus, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInvoiceItemInfo = itemView.findViewById(R.id.tvInvoiceItemInfo);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
