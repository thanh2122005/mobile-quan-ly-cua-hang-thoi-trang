package com.example.quanlycuahangthoitrang;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycuahangthoitrang.database.DatabaseHelper;
import com.example.quanlycuahangthoitrang.model.Product;
//tạo
public class AdminProductActivity extends AppCompatActivity {

    private AdminProductAdapter adapter;
    private RecyclerView rvAdminProducts;
    private EditText edtSearch;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product);

        dbHelper = new DatabaseHelper(this);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnAddProduct).setOnClickListener(v -> {
            startActivity(new Intent(AdminProductActivity.this, AddProductActivity.class));
        });

        edtSearch = findViewById(R.id.edtSearch);
        rvAdminProducts = findViewById(R.id.rvAdminProducts);
        rvAdminProducts.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdminProductAdapter(dbHelper.getAllProducts(), new AdminProductAdapter.OnProductInteractionListener() {
            @Override
            public void onEdit(Product product) {
                Intent intent = new Intent(AdminProductActivity.this, EditProductActivity.class);
                intent.putExtra("product_id", product.getId());
                startActivity(intent);
            }

            @Override
            public void onDelete(Product product) {
                new AlertDialog.Builder(AdminProductActivity.this)
                        .setTitle("Xóa sản phẩm")
                        .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này không?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            dbHelper.deleteProduct(product.getId());
                            adapter.updateData(dbHelper.searchProducts(edtSearch.getText().toString()));
                            Toast.makeText(AdminProductActivity.this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });
        rvAdminProducts.setAdapter(adapter);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.updateData(dbHelper.searchProducts(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.updateData(dbHelper.searchProducts(edtSearch.getText().toString()));
        }
    }
}
