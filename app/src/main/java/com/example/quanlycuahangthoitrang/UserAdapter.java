package com.example.quanlycuahangthoitrang;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycuahangthoitrang.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    
    private List<User> users;
    private OnUserInteractionListener listener;
    private String currentUserEmail;

    public interface OnUserInteractionListener {
        void onDelete(User user);
        void onToggleRole(User user);
    }

    public UserAdapter(List<User> users, String currentUserEmail, OnUserInteractionListener listener) {
        this.users = users;
        this.currentUserEmail = currentUserEmail;
        this.listener = listener;
    }

    public void updateData(List<User> newUsers) {
        this.users = newUsers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        
        holder.tvUserName.setText(user.getName());
        holder.tvUserEmail.setText(user.getEmail());
        holder.tvUserRole.setText("Vai trò: " + user.getRole());
        
        // =========================================================================
        // THUẬT TOÁN BẢO VỆ ADMIN: Chống việc tự sát quyền (Tự xóa tài khoản chính mình)
        // =========================================================================
        // So sánh email của dòng hiện tại với email đang đăng nhập (currentUserEmail)
        if (user.getEmail().equals(currentUserEmail)) {
            // Nếu là tài khoản đang đăng nhập -> Giấu luôn nút Xóa và nút Đổi quyền
            holder.btnDeleteUser.setVisibility(View.GONE);
            holder.btnToggleRole.setVisibility(View.GONE);
        } else {
            // Nếu là tài khoản người khác -> Hiện bình thường
            holder.btnDeleteUser.setVisibility(View.VISIBLE);
            holder.btnToggleRole.setVisibility(View.VISIBLE);
            
            // Xử lý nút Đổi quyền (Thăng chức / Giáng chức)
            if ("admin".equals(user.getRole())) {
                holder.btnToggleRole.setText("⬇️"); // Nếu đang là admin -> Nút có icon mũi tên xuống (Giáng chức thành User)
            } else {
                holder.btnToggleRole.setText("⭐"); // Nếu đang là user -> Nút có icon ngôi sao (Thăng chức làm Admin)
            }
        }

        holder.btnDeleteUser.setOnClickListener(v -> listener.onDelete(user));
        holder.btnToggleRole.setOnClickListener(v -> listener.onToggleRole(user));
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUserEmail, tvUserRole, btnDeleteUser, btnToggleRole;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserRole = itemView.findViewById(R.id.tvUserRole);
            btnDeleteUser = itemView.findViewById(R.id.btnDeleteUser);
            btnToggleRole = itemView.findViewById(R.id.btnToggleRole);
        }
    }
}
