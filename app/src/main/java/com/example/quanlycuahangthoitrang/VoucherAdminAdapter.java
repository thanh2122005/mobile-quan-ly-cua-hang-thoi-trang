package com.example.quanlycuahangthoitrang;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycuahangthoitrang.model.Voucher;

import java.util.ArrayList;

public class VoucherAdminAdapter extends RecyclerView.Adapter<VoucherAdminAdapter.VoucherAdminViewHolder> {

    private ArrayList<Voucher> list;
    private Context context;
    private OnVoucherInteractionListener listener;

    public interface OnVoucherInteractionListener {
        void onEdit(Voucher voucher);
        void onDelete(Voucher voucher);
    }

    public VoucherAdminAdapter(Context context, ArrayList<Voucher> list, OnVoucherInteractionListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public void updateData(ArrayList<Voucher> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VoucherAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_voucher_admin, parent, false);
        return new VoucherAdminViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherAdminViewHolder holder, int position) {
        Voucher v = list.get(position);

        // Hiển thị mã giảm giá (VD: TET2024)
        holder.tvCode.setText(v.getCode());
        
        // Phân loại hiển thị theo Loại Voucher
        if (v.getType().equalsIgnoreCase("freeship")) {
            holder.tvType.setText("Miễn phí vận chuyển");
        } else {
            holder.tvType.setText("Giảm giá đơn hàng");
        }

        // Hiển thị Mô tả và Số lượng đã dùng
        holder.tvDesc.setText(v.getDisplayText());
        holder.tvUsage.setText("Đã dùng: " + v.getUsedCount() + " / " + v.getUsageLimit());

        holder.btnEdit.setOnClickListener(view -> {
            if (listener != null) listener.onEdit(v);
        });

        holder.btnDelete.setOnClickListener(view -> {
            if (listener != null) listener.onDelete(v);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class VoucherAdminViewHolder extends RecyclerView.ViewHolder {
        TextView tvCode, tvType, tvDesc, tvUsage;
        ImageView btnEdit, btnDelete;

        public VoucherAdminViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tvCode);
            tvType = itemView.findViewById(R.id.tvType);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvUsage = itemView.findViewById(R.id.tvUsage);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
