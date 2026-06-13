package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycuahangthoitrang.data.MockData;
import com.example.quanlycuahangthoitrang.model.Product;
import com.example.quanlycuahangthoitrang.utils.CartManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private ProductUserAdapter adapter;
    private List<Product> currentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        String categoryExtra = getIntent().getStringExtra("category");
        final String category = categoryExtra == null ? "Tất cả" : categoryExtra;

        TextView tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        if (category.equals("Tất cả")) {
            tvCategoryTitle.setText("Tất cả sản phẩm");
        } else {
            tvCategoryTitle.setText("Sản phẩm: " + category);
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        currentList = MockData.getProductsByCategory(category);

        RecyclerView rvProductsList = findViewById(R.id.rvProductsList);
        rvProductsList.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ProductUserAdapter(currentList, new ProductUserAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
                intent.putExtra("product_id", product.getId());
                startActivity(intent);
            }

            @Override
            public void onAddToCartClick(Product product) {
                CartManager.addToCart(product, 1);
                Toast.makeText(ProductListActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });
        rvProductsList.setAdapter(adapter);

        // Sorting
        findViewById(R.id.chipSortNew).setOnClickListener(v -> {
            currentList = MockData.getProductsByCategory(category);
            adapter.setProducts(currentList);
            updateChipStyles(R.id.chipSortNew);
        });

        findViewById(R.id.chipSortLow).setOnClickListener(v -> {
            Collections.sort(currentList, Comparator.comparingInt(Product::getPrice));
            adapter.notifyDataSetChanged();
            updateChipStyles(R.id.chipSortLow);
        });

        findViewById(R.id.chipSortHigh).setOnClickListener(v -> {
            Collections.sort(currentList, (p1, p2) -> Integer.compare(p2.getPrice(), p1.getPrice()));
            adapter.notifyDataSetChanged();
            updateChipStyles(R.id.chipSortHigh);
        });
    }

    private void updateChipStyles(int selectedId) {
        TextView chipSortNew = findViewById(R.id.chipSortNew);
        TextView chipSortLow = findViewById(R.id.chipSortLow);
        TextView chipSortHigh = findViewById(R.id.chipSortHigh);

        chipSortNew.setBackgroundResource(R.drawable.bg_chip_normal);
        chipSortNew.setTextColor(getResources().getColor(R.color.text_secondary));
        
        chipSortLow.setBackgroundResource(R.drawable.bg_chip_normal);
        chipSortLow.setTextColor(getResources().getColor(R.color.text_secondary));
        
        chipSortHigh.setBackgroundResource(R.drawable.bg_chip_normal);
        chipSortHigh.setTextColor(getResources().getColor(R.color.text_secondary));

        TextView selected = findViewById(selectedId);
        selected.setBackgroundResource(R.drawable.bg_chip_selected);
        selected.setTextColor(getResources().getColor(R.color.white));
    }
}
