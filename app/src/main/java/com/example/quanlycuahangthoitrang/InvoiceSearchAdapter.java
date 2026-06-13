package com.example.quanlycuahangthoitrang;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycuahangthoitrang.model.Product;
import com.example.quanlycuahangthoitrang.utils.FormatUtils;

import java.util.List;

public class InvoiceSearchAdapter extends RecyclerView.Adapter<InvoiceSearchAdapter.ViewHolder> {

    private List<Product> productList;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public InvoiceSearchAdapter(List<Product> productList, OnProductClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    public void updateData(List<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invoice_product_search, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product p = productList.get(position);
        holder.tvName.setText(p.getName());
        holder.tvDetail.setText(FormatUtils.formatPrice(p.getPrice()) + " - Kho: " + p.getStock());
        holder.ivProductImage.setImageResource(p.getImageResId());
        
        holder.itemView.setOnClickListener(v -> listener.onProductClick(p));
        holder.btnAdd.setOnClickListener(v -> listener.onProductClick(p));
    }

    @Override
    public int getItemCount() {
        return productList == null ? 0 : productList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetail, btnAdd;
        android.widget.ImageView ivProductImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDetail = itemView.findViewById(R.id.tvDetail);
            btnAdd = itemView.findViewById(R.id.btnAdd);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
        }
    }
}
