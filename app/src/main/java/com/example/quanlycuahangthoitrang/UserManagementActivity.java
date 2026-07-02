package com.example.quanlycuahangthoitrang;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycuahangthoitrang.database.DatabaseHelper;
import com.example.quanlycuahangthoitrang.model.User;

import com.example.quanlycuahangthoitrang.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class UserManagementActivity extends AppCompatActivity {
    
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private RecyclerView rvUsers;
    private UserAdapter adapter;
    private List<User> allUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file XML
        setContentView(R.layout.activity_user_management);

        // Khởi tạo bộ công cụ thao tác với CSDL
        dbHelper = new DatabaseHelper(this);
        // Khởi tạo trình quản lý phiên đăng nhập
        sessionManager = new SessionManager(this);

        // Bắt sự kiện bấm nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Ánh xạ view từ XML sang Java
        rvUsers = findViewById(R.id.rvUsers);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));

        // Tải toàn bộ danh sách User từ Database lên
        allUsers = dbHelper.getAllUsers();
        
        // --- TẠO DỮ LIỆU ẢO NẾU DANH SÁCH QUÁ ÍT NGƯỜI DÙNG ---
        if (allUsers.size() <= 2) {
            android.database.sqlite.SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL("INSERT OR IGNORE INTO users (name, email, password, phone, address, role) VALUES ('Trần Văn Hoàng', 'hoang.tran@gmail.com', '123456', '0912345678', 'Đà Nẵng', 'user')");
            db.execSQL("INSERT OR IGNORE INTO users (name, email, password, phone, address, role) VALUES ('Nguyễn Thị Lan', 'lan.nguyen@gmail.com', '123456', '0988112233', 'Hồ Chí Minh', 'user')");
            db.execSQL("INSERT OR IGNORE INTO users (name, email, password, phone, address, role) VALUES ('Lê Hoàng Tuấn', 'user1@gmail.com', '123456', '0909556677', 'Cần Thơ', 'user')");
            allUsers = dbHelper.getAllUsers(); // Tải lại danh sách sau khi thêm
        }
        
        // Khởi tạo Adapter
        // Truyền kèm SessionManager.getEmail() để phân biệt được đâu là "Tài khoản của chính tôi" (Tránh việc Admin tự xóa chính mình)
        adapter = new UserAdapter(allUsers, sessionManager.getEmail(), new UserAdapter.OnUserInteractionListener() {
            
            // SỰ KIỆN: KHI BẤM NÚT [XÓA NGƯỜI DÙNG]
            @Override
            public void onDelete(User user) {
                // RÀO CẢN: Hiển thị Hộp thoại Cảnh báo (Dialog)
                new AlertDialog.Builder(UserManagementActivity.this)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn chắc chắn muốn xóa người dùng " + user.getName() + "?\nHành động này không thể hoàn tác.")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            // Gọi hàm Xóa trong CSDL theo ID
                            if (dbHelper.deleteUser(user.getId())) {
                                Toast.makeText(UserManagementActivity.this, "Đã xóa người dùng", Toast.LENGTH_SHORT).show();
                                // Xóa xong thì tải lại danh sách hiển thị
                                loadUsers();
                            } else {
                                Toast.makeText(UserManagementActivity.this, "Lỗi khi xóa", Toast.LENGTH_SHORT).show();
                            }
                        })
                        // Bấm Hủy thì tắt hộp thoại
                        .setNegativeButton("Hủy", null)
                        .show();
            }

            @Override
            public void onToggleRole(User user) {
                // Kiểm tra xem User này hiện tại đang là Admin hay User
                boolean isAdmin = "admin".equals(user.getRole());
                
                // Nếu đang là Admin thì đổi thành "Hạ cấp" (thành user), nếu là User thì "Thăng cấp" (thành admin)
                String title = isAdmin ? "Hạ cấp" : "Thăng cấp";
                String message = isAdmin ? "Bạn muốn hạ cấp " + user.getName() + " thành Người dùng (User)?" : "Bạn muốn thăng cấp " + user.getName() + " thành Quản trị viên (Admin)?";
                String newRole = isAdmin ? "user" : "admin";
                
                // Hiển thị hộp thoại hỏi lại lần cuối
                new AlertDialog.Builder(UserManagementActivity.this)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("Đồng ý", (dialog, which) -> {
                            // Gọi hàm DB để cập nhật vai trò mới (Role)
                            if (dbHelper.updateUserRole(user.getId(), newRole)) {
                                Toast.makeText(UserManagementActivity.this, "Đã cập nhật quyền thành công", Toast.LENGTH_SHORT).show();
                                loadUsers(); // Cập nhật thành công thì tải lại danh sách hiển thị
                            } else {
                                Toast.makeText(UserManagementActivity.this, "Lỗi khi cập nhật quyền", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });
        rvUsers.setAdapter(adapter);

        // Ánh xạ view từ XML sang Java
        EditText edtSearch = findViewById(R.id.edtSearch);
        if (edtSearch != null) {
            edtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterUsers(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }
    
    // =========================================================================
    // HÀM TÌM KIẾM NGƯỜI DÙNG (Lọc Local)
    // Thay vì chọc xuống Database gọi lệnh SELECT LIKE liên tục, hệ thống sẽ lọc
    // trực tiếp trên danh sách List<User> đang hiển thị trên RAM để tối ưu tốc độ.
    // =========================================================================
    private void filterUsers(String keyword) {
        String query = keyword.toLowerCase().trim();
        List<User> filteredList = new ArrayList<>(); // Danh sách tạm chứa kết quả
        
        // Vòng lặp duyệt qua tất cả người dùng
        for (User user : allUsers) {
            // Nếu Tên hoặc Email có chứa ký tự đang gõ thì bốc bỏ vào danh sách tạm
            if (user.getName().toLowerCase().contains(query) || 
                user.getEmail().toLowerCase().contains(query)) {
                filteredList.add(user);
            }
        }
        // Ép RecyclerView hiển thị danh sách tạm (Kết quả tìm kiếm)
        adapter.updateData(filteredList);
    }
    
    private void loadUsers() {
        allUsers = dbHelper.getAllUsers();
        adapter.updateData(allUsers);
    }
}
