package com.example.quanlycuahangthoitrang;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycuahangthoitrang.model.Category;
import com.example.quanlycuahangthoitrang.database.DatabaseHelper;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList;
    private DatabaseHelper dbHelper;
    private OnCategoryInteractionListener listener;

    public interface OnCategoryInteractionListener {
        void onEdit(Category category);
        void onDelete(Category category);
    }

    public CategoryAdapter(List<Category> categoryList, DatabaseHelper dbHelper, OnCategoryInteractionListener listener) {
        this.categoryList = categoryList;
        this.dbHelper = dbHelper;
        this.listener = listener;
    }

    public void updateData(List<Category> newList) {
        this.categoryList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_admin, parent, false);
        return new CategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        
        // Đổ thông tin cơ bản: Tên danh mục và Icon
        holder.tvCategoryName.setText(category.getName());
        holder.tvCategoryIcon.setText(category.getIconName());

        // Đếm xem trong Database có bao nhiêu Sản phẩm thuộc Danh mục này
        // (Ví dụ: Danh mục "Áo" đang có 15 cái áo)
        int count = dbHelper.getProductCountByCategory(category.getName());
        holder.tvProductCount.setText(count + " sản phẩm");

        // Bắt sự kiện bấm nút [Sửa] và [Xóa]
        // Truyền thẳng Object category về cho CategoryActivity xử lý
        holder.btnEditCategory.setOnClickListener(v -> listener.onEdit(category));
        holder.btnDeleteCategory.setOnClickListener(v -> listener.onDelete(category));
    }

    @Override
    public int getItemCount() {
        return categoryList == null ? 0 : categoryList.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName, tvCategoryIcon, tvProductCount;
        View btnEditCategory, btnDeleteCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvCategoryIcon = itemView.findViewById(R.id.tvCategoryIcon);
            tvProductCount = itemView.findViewById(R.id.tvProductCount);
            btnEditCategory = itemView.findViewById(R.id.btnEditCategory);
            btnDeleteCategory = itemView.findViewById(R.id.btnDeleteCategory);
        }
    }
}
