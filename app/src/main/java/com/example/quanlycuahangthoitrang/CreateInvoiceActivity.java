package com.example.quanlycuahangthoitrang;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycuahangthoitrang.database.DatabaseHelper;
import com.example.quanlycuahangthoitrang.model.Invoice;
import com.example.quanlycuahangthoitrang.model.InvoiceItem;
import com.example.quanlycuahangthoitrang.model.Product;
import com.example.quanlycuahangthoitrang.utils.FormatUtils;

import java.util.ArrayList;
import java.util.List;

public class CreateInvoiceActivity extends AppCompatActivity {

    private EditText edtSearchProduct;
    private RecyclerView rvSearchProducts, rvSelectedItems;
    private TextView tvTotalAmount, btnSaveInvoice, tvEmptyInvoice;

    private InvoiceSearchAdapter searchAdapter;
    private InvoiceSelectedAdapter selectedAdapter;

    private List<InvoiceItem> selectedItems = new ArrayList<>();
    private int totalAmount = 0;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_invoice);

        dbHelper = new DatabaseHelper(this);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        edtSearchProduct = findViewById(R.id.edtSearchProduct);
        rvSearchProducts = findViewById(R.id.rvSearchProducts);
        rvSelectedItems = findViewById(R.id.rvSelectedItems);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnSaveInvoice = findViewById(R.id.btnSaveInvoice);
        tvEmptyInvoice = findViewById(R.id.tvEmptyInvoice);

        rvSearchProducts.setLayoutManager(new LinearLayoutManager(this));
        rvSelectedItems.setLayoutManager(new LinearLayoutManager(this));

        searchAdapter = new InvoiceSearchAdapter(dbHelper.getAllProducts(), product -> {
            if (product.getStock() <= 0) {
                Toast.makeText(this, "Sản phẩm đã hết hàng", Toast.LENGTH_SHORT).show();
                return;
            }
            addOrUpdateItem(product);
        });
        rvSearchProducts.setAdapter(searchAdapter);
        rvSearchProducts.setVisibility(View.VISIBLE);

        selectedAdapter = new InvoiceSelectedAdapter(selectedItems, new InvoiceSelectedAdapter.OnItemInteractionListener() {
            @Override
            public void onIncrease(int position) {
                InvoiceItem item = selectedItems.get(position);
                if (item.getQuantity() < item.getProduct().getStock()) {
                    item.setQuantity(item.getQuantity() + 1);
                    updateTotal();
                } else {
                    Toast.makeText(CreateInvoiceActivity.this, "Số lượng vượt quá tồn kho", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDecrease(int position) {
                InvoiceItem item = selectedItems.get(position);
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                    updateTotal();
                } else {
                    selectedItems.remove(position);
                    updateTotal();
                }
            }

            @Override
            public void onDelete(int position) {
                selectedItems.remove(position);
                updateTotal();
            }
        });
        rvSelectedItems.setAdapter(selectedAdapter);

        edtSearchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    searchAdapter.updateData(dbHelper.getAllProducts());
                } else {
                    searchAdapter.updateData(dbHelper.searchProducts(query));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSaveInvoice.setOnClickListener(v -> {
            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "Hóa đơn đang trống", Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(this)
                    .setTitle("Lưu hóa đơn")
                    .setMessage("Bạn có chắc chắn muốn lưu hóa đơn này không?\nTổng tiền: " + FormatUtils.formatPrice(totalAmount))
                    .setPositiveButton("Lưu", (dialog, which) -> saveInvoice())
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    private void addOrUpdateItem(Product product) {
        for (InvoiceItem item : selectedItems) {
            if (item.getProduct().getId() == product.getId()) {
                if (item.getQuantity() < product.getStock()) {
                    item.setQuantity(item.getQuantity() + 1);
                    updateTotal();
                } else {
                    Toast.makeText(this, "Số lượng vượt quá tồn kho", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
        selectedItems.add(new InvoiceItem(product, 1, product.getPrice()));
        updateTotal();
    }

    private void updateTotal() {
        totalAmount = 0;
        for (InvoiceItem item : selectedItems) {
            totalAmount += item.getQuantity() * item.getUnitPrice();
        }
        tvTotalAmount.setText(FormatUtils.formatPrice(totalAmount));
        selectedAdapter.notifyDataSetChanged();
        if (selectedItems.isEmpty()) {
            tvEmptyInvoice.setVisibility(View.VISIBLE);
        } else {
            tvEmptyInvoice.setVisibility(View.GONE);
        }
    }

    private void saveInvoice() {
        int result = dbHelper.createInvoice(new ArrayList<>(selectedItems), "Admin");
        if (result > 0) {
            Toast.makeText(this, "Lưu hóa đơn thành công", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, InvoiceHistoryActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Lưu hóa đơn thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}
