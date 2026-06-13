package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class OnboardingActivity extends AppCompatActivity {

    private int currentStep = 0;

    private final String[] titles = {
            "Khám phá sản phẩm",
            "Đặt hàng nhanh chóng",
            "Quản lý cửa hàng hiệu quả"
    };

    private final String[] descriptions = {
            "Tìm kiếm và khám phá hàng nghìn sản phẩm thời trang đa dạng.",
            "Mua sắm tiện lợi với quy trình đặt hàng đơn giản và nhanh chóng.",
            "Công cụ quản lý mạnh mẽ cho chủ cửa hàng thời trang."
    };

    private final String[] emojis = {"🔍", "🛒", "📊"};

    private TextView tvTitle, tvDesc, tvEmoji, btnNext;
    private View dot1, dot2, dot3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        tvTitle = findViewById(R.id.tvOnboardingTitle);
        tvDesc = findViewById(R.id.tvOnboardingDesc);
        tvEmoji = findViewById(R.id.tvOnboardingEmoji);
        btnNext = findViewById(R.id.btnOnboardingNext);
        dot1 = findViewById(R.id.dot1);
        dot2 = findViewById(R.id.dot2);
        dot3 = findViewById(R.id.dot3);

        updateStep();

        btnNext.setOnClickListener(v -> {
            currentStep++;
            if (currentStep >= titles.length) {
                // Hoàn thành onboarding -> mở Login
                Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                updateStep();
            }
        });
    }

    private void updateStep() {
        tvTitle.setText(titles[currentStep]);
        tvDesc.setText(descriptions[currentStep]);
        tvEmoji.setText(emojis[currentStep]);

        // Cập nhật dots
        dot1.setBackgroundResource(currentStep == 0 ? R.drawable.dot_active : R.drawable.dot_inactive);
        dot2.setBackgroundResource(currentStep == 1 ? R.drawable.dot_active : R.drawable.dot_inactive);
        dot3.setBackgroundResource(currentStep == 2 ? R.drawable.dot_active : R.drawable.dot_inactive);

        // Cập nhật text nút
        if (currentStep == titles.length - 1) {
            btnNext.setText(getString(R.string.onboarding_start));
        } else {
            btnNext.setText(getString(R.string.onboarding_next));
        }
    }
}
