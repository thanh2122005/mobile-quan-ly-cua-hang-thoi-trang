package com.example.quanlycuahangthoitrang;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quanlycuahangthoitrang.model.Order;
import com.example.quanlycuahangthoitrang.utils.FormatUtils;
import java.util.List;

public class UserOrderAdapter extends RecyclerView.Adapter<UserOrderAdapter.ViewHolder> {

    private List<Order> orderList;
    private final OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onDetailClick(Order order);
    }

    public UserOrderAdapter(List<Order> orderList, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    public void updateData(List<Order> newOrders) {
        this.orderList = newOrders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tvOrderCode.setText(order.getCode());
        holder.tvOrderStatus.setText(order.getStatus());
        holder.tvOrderDate.setText("Ngày đặt: " + order.getCreatedAt());
        holder.tvOrderTotal.setText("Tổng: " + FormatUtils.formatPrice(order.getTotal()));

        holder.btnOrderDetail.setOnClickListener(v -> listener.onDetailClick(order));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderCode, tvOrderStatus, tvOrderDate, tvOrderTotal, btnOrderDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderCode = itemView.findViewById(R.id.tvOrderCode);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            btnOrderDetail = itemView.findViewById(R.id.btnOrderDetail);
        }
    }
}
