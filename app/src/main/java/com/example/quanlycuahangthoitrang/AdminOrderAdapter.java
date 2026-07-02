package com.example.quanlycuahangthoitrang;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycuahangthoitrang.model.Order;
import com.example.quanlycuahangthoitrang.utils.FormatUtils;

import java.util.ArrayList;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Order> orderList;

    public AdminOrderAdapter(Context context, ArrayList<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    public void updateList(ArrayList<Order> newList) {
        this.orderList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Đổ dữ liệu đơn hàng lên các ô Text trên giao diện thẻ (card)
        holder.tvOrderCode.setText("Mã ĐH: " + order.getCode());
        holder.tvOrderDate.setText(order.getCreatedAt());
        holder.tvReceiverName.setText(order.getReceiverName());
        holder.tvReceiverPhone.setText(order.getPhone());
        holder.tvOrderStatus.setText(order.getStatus());
        holder.tvOrderTotal.setText("Tổng: " + FormatUtils.formatPrice(order.getTotal()));

        // =========================================================================
        // THUẬT TOÁN ĐỔI MÀU TRẠNG THÁI ĐƠN HÀNG (UX/UI)
        // Mục đích: Giúp Admin lướt nhanh qua hàng trăm đơn vẫn nhận diện được
        // trạng thái dựa vào màu sắc mà không cần đọc chữ.
        // =========================================================================
        if ("Đã hủy".equals(order.getStatus())) {
            // Đơn hủy -> Chữ màu ĐỎ cảnh báo
            holder.tvOrderStatus.setTextColor(context.getResources().getColor(R.color.error_red));
        } else if ("Hoàn thành".equals(order.getStatus())) {
            // Đơn xong -> Chữ màu XANH LÁ an toàn
            holder.tvOrderStatus.setTextColor(context.getResources().getColor(R.color.success_green));
        } else {
            // Các trạng thái đang xử lý (Chờ xác nhận, Đang giao) -> Chữ màu CAM chờ đợi
            holder.tvOrderStatus.setTextColor(context.getResources().getColor(R.color.accent_orange));
        }

        // Bắt sự kiện khi Admin bấm nút [Xem chi tiết]
        holder.btnViewDetails.setOnClickListener(v -> {
            // Đóng gói ID của đơn hàng này ném sang màn hình AdminOrderDetailActivity
            Intent intent = new Intent(context, AdminOrderDetailActivity.class);
            intent.putExtra("order_id", order.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderCode, tvOrderDate, tvReceiverName, tvReceiverPhone, tvOrderStatus, tvOrderTotal, btnViewDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderCode = itemView.findViewById(R.id.tvOrderCode);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvReceiverName = itemView.findViewById(R.id.tvReceiverName);
            tvReceiverPhone = itemView.findViewById(R.id.tvReceiverPhone);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}
