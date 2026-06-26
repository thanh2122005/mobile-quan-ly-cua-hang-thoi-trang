package com.example.quanlycuahangthoitrang;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycuahangthoitrang.model.CartItem;
import com.example.quanlycuahangthoitrang.utils.FormatUtils;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    
    private List<CartItem> cartItems;
    private OnCartItemInteractionListener listener;

    public interface OnCartItemInteractionListener {
        void onIncrease(CartItem item);
        void onDecrease(CartItem item);
        void onRemove(CartItem item);
        void onEditVariant(CartItem item);
        void onSelectionChanged(CartItem item, boolean isChecked);
    }

    public CartAdapter(List<CartItem> cartItems, OnCartItemInteractionListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    public void updateData(List<CartItem> newItems) {
        this.cartItems = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        
        holder.tvProductName.setText(item.getProduct().getName());
        holder.tvProductPrice.setText(FormatUtils.formatPrice(item.getProduct().getPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        
        if (item.isSelected()) {
            holder.cbSelect.setBackgroundResource(R.drawable.bg_checkbox_checked);
            holder.cbSelect.setText("✓");
        } else {
            holder.cbSelect.setBackgroundResource(R.drawable.bg_checkbox_unchecked);
            holder.cbSelect.setText("");
        }

        holder.cbSelect.setOnClickListener(v -> {
            boolean newState = !item.isSelected();
            item.setSelected(newState);
            
            if (newState) {
                holder.cbSelect.setBackgroundResource(R.drawable.bg_checkbox_checked);
                holder.cbSelect.setText("✓");
            } else {
                holder.cbSelect.setBackgroundResource(R.drawable.bg_checkbox_unchecked);
                holder.cbSelect.setText("");
            }
            
            listener.onSelectionChanged(item, newState);
        });
        
        String variantText = "";
        if (item.getSelectedColor() != null && !item.getSelectedColor().isEmpty()) {
            variantText += "Màu: " + item.getSelectedColor();
        }
        if (item.getSelectedSize() != null && !item.getSelectedSize().isEmpty()) {
            if (!variantText.isEmpty()) variantText += " - ";
            variantText += "Size: " + item.getSelectedSize();
        }
        if (!variantText.isEmpty()) {
            variantText += " ✏️";
            holder.tvProductVariant.setText(variantText);
            holder.tvProductVariant.setVisibility(View.VISIBLE);
            holder.tvProductVariant.setOnClickListener(v -> listener.onEditVariant(item));
        } else {
            holder.tvProductVariant.setVisibility(View.GONE);
        }

        com.example.quanlycuahangthoitrang.utils.ImageLoader.load(holder.ivProductImage, item.getProduct().getMainImage());

        holder.btnMinus.setOnClickListener(v -> listener.onDecrease(item));
        holder.btnPlus.setOnClickListener(v -> listener.onIncrease(item));
        holder.btnRemove.setOnClickListener(v -> listener.onRemove(item));
        
        // Mở chi tiết sản phẩm khi click vào ảnh hoặc tên
        android.view.View.OnClickListener openDetailListener = v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), ProductDetailActivity.class);
            intent.putExtra("product_id", item.getProduct().getId());
            v.getContext().startActivity(intent);
        };
        holder.ivProductImage.setOnClickListener(openDetailListener);
        holder.tvProductName.setOnClickListener(openDetailListener);
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductVariant, tvProductPrice, tvQuantity, cbSelect;
        TextView btnMinus, btnPlus, btnRemove;
        android.widget.ImageView ivProductImage;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cbSelect = itemView.findViewById(R.id.cbSelect);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductVariant = itemView.findViewById(R.id.tvProductVariant);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
