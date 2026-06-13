package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycuahangthoitrang.database.DatabaseHelper;
import com.example.quanlycuahangthoitrang.model.Invoice;

import java.util.ArrayList;
import java.util.List;

public class InvoiceHistoryActivity extends AppCompatActivity {

    private InvoiceHistoryAdapter adapter;
    private RecyclerView rvInvoiceHistory;
    private EditText edtSearchInvoice;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_history);

        dbHelper = new DatabaseHelper(this);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        edtSearchInvoice = findViewById(R.id.edtSearchInvoice);
        rvInvoiceHistory = findViewById(R.id.rvInvoiceHistory);
        rvInvoiceHistory.setLayoutManager(new LinearLayoutManager(this));

        adapter = new InvoiceHistoryAdapter(dbHelper.getAllInvoices(), new InvoiceHistoryAdapter.OnInvoiceClickListener() {
            @Override
            public void onDetailClick(Invoice invoice) {
                Intent intent = new Intent(InvoiceHistoryActivity.this, InvoiceDetailActivity.class);
                intent.putExtra("invoice_id", invoice.getId());
                startActivity(intent);
            }
        });
        rvInvoiceHistory.setAdapter(adapter);

        edtSearchInvoice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString();
                List<Invoice> filtered = new ArrayList<>();
                for (Invoice invoice : dbHelper.getAllInvoices()) {
                    if (com.example.quanlycuahangthoitrang.utils.FormatUtils.matchesSearch(keyword, invoice.getCode())) {
                        filtered.add(invoice);
                    }
                }
                adapter.updateData(filtered);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            String keyword = edtSearchInvoice.getText().toString();
            List<Invoice> filtered = new ArrayList<>();
            for (Invoice invoice : dbHelper.getAllInvoices()) {
                if (com.example.quanlycuahangthoitrang.utils.FormatUtils.matchesSearch(keyword, invoice.getCode())) {
                    filtered.add(invoice);
                }
            }
            adapter.updateData(filtered);
        }
    }
}
