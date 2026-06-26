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

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private OnProductInteractionListener listener;

    public interface OnProductInteractionListener {
        void onEdit(Product product);
        void onDelete(Product product);
    }

    public AdminProductAdapter(List<Product> productList, OnProductInteractionListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    public void updateData(List<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_admin, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvProductName.setText(product.getName());
        holder.tvProductCategory.setText("Danh mục: " + product.getCategory());
        holder.tvProductPriceStock.setText("Giá: " + FormatUtils.formatPrice(product.getPrice()) + " | Kho: " + product.getStock());
        com.example.quanlycuahangthoitrang.utils.ImageLoader.load(holder.ivProductImage, product.getMainImage());

        if (product.getStock() <= 3) {
            holder.tvProductStatus.setText("Sắp hết (" + product.getStock() + ")");
            holder.tvProductStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.warning_amber));
        } else {
            holder.tvProductStatus.setText("Còn hàng");
            holder.tvProductStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.success_green));
        }

        holder.btnEditItem.setOnClickListener(v -> listener.onEdit(product));
        holder.btnDeleteItem.setOnClickListener(v -> listener.onDelete(product));
    }

    @Override
    public int getItemCount() {
        return productList == null ? 0 : productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductCategory, tvProductPriceStock, tvProductStatus;
        TextView btnEditItem, btnDeleteItem;
        android.widget.ImageView ivProductImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductCategory = itemView.findViewById(R.id.tvProductCategory);
            tvProductPriceStock = itemView.findViewById(R.id.tvProductPriceStock);
            tvProductStatus = itemView.findViewById(R.id.tvProductStatus);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            btnEditItem = itemView.findViewById(R.id.btnEditItem);
            btnDeleteItem = itemView.findViewById(R.id.btnDeleteItem);
        }
    }
}
