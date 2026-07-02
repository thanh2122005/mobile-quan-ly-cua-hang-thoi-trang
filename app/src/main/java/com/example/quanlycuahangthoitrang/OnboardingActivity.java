package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class OnboardingActivity extends AppCompatActivity {

    // Biến lưu trữ bước hiện tại của Màn hình giới thiệu (Bắt đầu từ 0)
    private int currentStep = 0;

    // Mảng chứa các Tiêu đề của 3 bước giới thiệu
    private final String[] titles = {
            "Khám phá sản phẩm",
            "Đặt hàng nhanh chóng",
            "Quản lý cửa hàng hiệu quả"
    };

    // Mảng chứa các Đoạn văn mô tả chi tiết cho 3 bước giới thiệu
    private final String[] descriptions = {
            "Tìm kiếm và khám phá hàng nghìn sản phẩm thời trang đa dạng.",
            "Mua sắm tiện lợi với quy trình đặt hàng đơn giản và nhanh chóng.",
            "Công cụ quản lý mạnh mẽ cho chủ cửa hàng thời trang."
    };

    // Mảng chứa các Biểu tượng (Emoji) trang trí cho từng bước
    private final String[] emojis = {"🔍", "🛒", "📊"};

    // Khai báo các thành phần giao diện (TextView cho chữ, View cho các dấu chấm)
    private TextView tvTitle, tvDesc, tvEmoji, btnNext;
    private View dot1, dot2, dot3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm này được gọi đầu tiên khi Activity vừa được mở lên
        super.onCreate(savedInstanceState);
        
        // Nạp file giao diện XML (Giao diện màn hình giới thiệu) vào bộ nhớ
        setContentView(R.layout.activity_onboarding);

        // Tìm và liên kết chữ Tiêu đề từ giao diện XML vào biến tvTitle trong Java
        tvTitle = findViewById(R.id.tvOnboardingTitle);
        // Tìm và liên kết chữ Mô tả từ giao diện XML vào biến tvDesc trong Java
        tvDesc = findViewById(R.id.tvOnboardingDesc);
        // Tìm và liên kết Biểu tượng từ giao diện XML vào biến tvEmoji trong Java
        tvEmoji = findViewById(R.id.tvOnboardingEmoji);
        // Tìm và liên kết Nút bấm Tiếp theo từ giao diện XML vào biến btnNext trong Java
        btnNext = findViewById(R.id.btnOnboardingNext);
        
        // Tìm 3 cái dấu chấm nhỏ ở dưới cùng màn hình (dùng để báo hiệu đang ở bước mấy)
        dot1 = findViewById(R.id.dot1);
        dot2 = findViewById(R.id.dot2);
        dot3 = findViewById(R.id.dot3);

        // Gọi hàm updateStep() để hiển thị dữ liệu của Bước 0 lên màn hình ngay khi vừa mở app
        updateStep();

        // Bắt sự kiện khi người dùng bấm vào nút "Tiếp theo"
        btnNext.setOnClickListener(v -> {
            // Tăng biến currentStep lên 1 đơn vị (Chuyển sang bước tiếp theo)
            currentStep++;
            
            // Nếu số bước hiện tại lớn hơn hoặc bằng tổng số bước (Tức là đã xem hết 3 màn hình)
            if (currentStep >= titles.length) {
                // Tạo một "Chuyến xe" (Intent) để chở người dùng từ Màn hình Giới thiệu sang Màn hình Đăng nhập
                Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
                // Lệnh bắt đầu chạy màn hình Đăng nhập
                startActivity(intent);
                // Đóng hoàn toàn màn hình Giới thiệu này lại để người dùng bấm nút Back không bị quay lại đây nữa
                finish();
            } else {
                // Nếu chưa xem hết, tiếp tục gọi hàm updateStep() để vẽ dữ liệu của màn hình tiếp theo
                updateStep();
            }
        });
    }

    // Hàm dùng để cập nhật nội dung chữ, biểu tượng và các dấu chấm tùy theo bước hiện tại
    private void updateStep() {
        // Cập nhật Tiêu đề dựa trên vị trí currentStep trong mảng titles
        tvTitle.setText(titles[currentStep]);
        // Cập nhật Mô tả dựa trên vị trí currentStep trong mảng descriptions
        tvDesc.setText(descriptions[currentStep]);
        // Cập nhật Biểu tượng dựa trên vị trí currentStep trong mảng emojis
        tvEmoji.setText(emojis[currentStep]);

        // CẬP NHẬT 3 DẤU CHẤM (Màu đậm là đang chọn, màu nhạt là chưa chọn)
        // Nếu đang ở Bước 0 (currentStep == 0) thì dấu chấm 1 sẽ tô màu đậm (dot_active), các dấu kia tô màu nhạt
        dot1.setBackgroundResource(currentStep == 0 ? R.drawable.dot_active : R.drawable.dot_inactive);
        dot2.setBackgroundResource(currentStep == 1 ? R.drawable.dot_active : R.drawable.dot_inactive);
        dot3.setBackgroundResource(currentStep == 2 ? R.drawable.dot_active : R.drawable.dot_inactive);

        // Cập nhật chữ hiển thị trên cái nút Bấm
        // Nếu đang ở màn hình cuối cùng (titles.length - 1)
        if (currentStep == titles.length - 1) {
            // Đổi chữ nút thành "Bắt đầu ngay"
            btnNext.setText(getString(R.string.onboarding_start));
        } else {
            // Đổi chữ nút thành "Tiếp theo"
            btnNext.setText(getString(R.string.onboarding_next));
        }
    }
}
