package com.example.quanlycuahangthoitrang;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycuahangthoitrang.utils.ImageLoader;

import java.util.ArrayList;

public class ProductGalleryAdapter extends RecyclerView.Adapter<ProductGalleryAdapter.ViewHolder> {

    private ArrayList<String> images;

    public ProductGalleryAdapter(ArrayList<String> images) {
        this.images = images;
        // If empty, show a placeholder
        if (this.images == null || this.images.isEmpty()) {
            this.images = new ArrayList<>();
            this.images.add("");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_image_gallery, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Sử dụng công cụ ImageLoader do ta tự viết (Tự động phân biệt Link Web hay File nội bộ)
        // để tải ảnh lên View
        ImageLoader.load(holder.ivGalleryImage, images.get(position));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGalleryImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGalleryImage = itemView.findViewById(R.id.ivGalleryImage);
        }
    }
}
