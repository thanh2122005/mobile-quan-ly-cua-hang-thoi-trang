package com.example.quanlycuahangthoitrang;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycuahangthoitrang.model.Invoice;
import com.example.quanlycuahangthoitrang.utils.FormatUtils;
import com.example.quanlycuahangthoitrang.utils.InvoiceManager;

public class InvoiceDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_detail);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        com.example.quanlycuahangthoitrang.database.DatabaseHelper dbHelper = new com.example.quanlycuahangthoitrang.database.DatabaseHelper(this);
        int invoiceId = getIntent().getIntExtra("invoice_id", -1);
        Invoice invoice = dbHelper.getInvoiceById(invoiceId);

        if (invoice == null) {
            Toast.makeText(this, "Không tìm thấy hóa đơn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView tvInvoiceCode = findViewById(R.id.tvInvoiceCode);
        TextView tvInvoiceDate = findViewById(R.id.tvInvoiceDate);
        TextView tvInvoiceCreator = findViewById(R.id.tvInvoiceCreator);
        TextView tvInvoiceStatus = findViewById(R.id.tvInvoiceStatus);
        TextView tvInvoiceTotal = findViewById(R.id.tvInvoiceTotal);
        RecyclerView rvInvoiceItems = findViewById(R.id.rvInvoiceItems);

        tvInvoiceCode.setText("Mã HD: " + invoice.getCode());
        tvInvoiceDate.setText("Ngày tạo: " + invoice.getDate());
        tvInvoiceCreator.setText("Người tạo: Admin");
        tvInvoiceStatus.setText("Trạng thái: " + invoice.getStatus());
        tvInvoiceTotal.setText(FormatUtils.formatPrice(invoice.getTotal()));

        rvInvoiceItems.setLayoutManager(new LinearLayoutManager(this));
        rvInvoiceItems.setAdapter(new InvoiceDetailAdapter(invoice.getItems()));
    }
}
