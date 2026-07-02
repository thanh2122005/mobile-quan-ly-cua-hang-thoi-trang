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

    public List<CartItem> getItems() {
        return cartItems;
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
        
        // Đổ dữ liệu chữ lên màn hình
        holder.tvProductName.setText(item.getProduct().getName());
        holder.tvProductPrice.setText(FormatUtils.formatPrice(item.getProduct().getPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        
        // =========================================================================
        // XỬ LÝ HỘP KIỂM (CHECKBOX) CHỌN SẢN PHẨM TRONG GIỎ HÀNG
        // Thuật toán vẽ giao diện:
        // Thay vì dùng CheckBox mặc định xấu xí của Android, ta tự vẽ lại bằng TextView (cbSelect)
        // để tạo ra hình tròn đẹp mắt. Dưới đây là logic thay đổi hình dạng nút bấm.
        // =========================================================================
        if (item.isSelected()) {
            // Đổi hình nền thành hình tròn màu cam (Đã chọn)
            holder.cbSelect.setBackgroundResource(R.drawable.bg_checkbox_checked);
            // In thêm dấu tick chữ v vào giữa hình tròn
            holder.cbSelect.setText("✓");
        } else {
            // Đổi hình nền thành vòng tròn viền xám rỗng (Chưa chọn)
            holder.cbSelect.setBackgroundResource(R.drawable.bg_checkbox_unchecked);
            // Xóa rỗng chữ bên trong
            holder.cbSelect.setText("");
        }

        // Bắt sự kiện khi người dùng bấm chạm ngón tay vào cái vòng tròn
        holder.cbSelect.setOnClickListener(v -> {
            // Lật ngược trạng thái (Đang chọn -> Bỏ chọn, và ngược lại)
            boolean newState = !item.isSelected();
            // Lưu trạng thái mới vào bộ nhớ
            item.setSelected(newState);
            
            // Đổi luôn giao diện tức thì (Cho mượt)
            if (newState) {
                holder.cbSelect.setBackgroundResource(R.drawable.bg_checkbox_checked);
                holder.cbSelect.setText("✓");
            } else {
                holder.cbSelect.setBackgroundResource(R.drawable.bg_checkbox_unchecked);
                holder.cbSelect.setText("");
            }
            
            // Bắn tín hiệu sang Activity gốc (CartActivity) để nó tính lại tổng tiền
            listener.onSelectionChanged(item, newState);
        });
        
        // =========================================================================
        // XỬ LÝ HIỂN THỊ THUỘC TÍNH SẢN PHẨM (MÀU SẮC, KÍCH CỠ)
        // =========================================================================
        String variantText = "";
        // Nếu có chọn màu thì nối thêm chữ Màu vào
        if (item.getSelectedColor() != null && !item.getSelectedColor().isEmpty()) {
            variantText += "Màu: " + item.getSelectedColor();
        }
        // Nếu có chọn Size thì nối thêm chữ Size vào
        if (item.getSelectedSize() != null && !item.getSelectedSize().isEmpty()) {
            // Nếu đằng trước đã có Màu rồi thì thêm dấu gạch ngang phân cách
            if (!variantText.isEmpty()) variantText += " - ";
            variantText += "Size: " + item.getSelectedSize();
        }
        
        // Hiển thị chuỗi vừa ghép ra màn hình
        if (!variantText.isEmpty()) {
            variantText += " ✏️"; // Gắn thêm cái icon cây bút chì cho trực quan
            holder.tvProductVariant.setText(variantText);
            // Hiển thị ô văn bản
            holder.tvProductVariant.setVisibility(View.VISIBLE);
            // Khi bấm vào cái dòng Màu - Size này, bắn tín hiệu sang Activity mở hộp thoại cho sửa
            holder.tvProductVariant.setOnClickListener(v -> listener.onEditVariant(item));
        } else {
            // Ẩn hoàn toàn ô chữ đi nếu sản phẩm này không có Màu hay Size
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
