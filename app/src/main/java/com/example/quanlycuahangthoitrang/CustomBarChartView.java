package com.example.quanlycuahangthoitrang;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// =========================================================
// CUSTOM BAR CHART VIEW
// =========================================================
public class CustomBarChartView extends View {

    // Đối tượng Paint để vẽ cột biểu đồ
    private Paint barPaint;
    
    // Đối tượng Paint để vẽ text (số tiền, ngày tháng)
    private Paint textPaint;

    // Danh sách trục X (Ngày)
    private List<String> labels = new ArrayList<>();
    
    // Danh sách trục Y (Doanh thu)
    private List<Integer> values = new ArrayList<>();

    // Giá trị lớn nhất để làm mốc chia tỷ lệ chiều cao
    private int maxValue = 0;

    // Constructor khi tạo bằng code Java
    public CustomBarChartView(Context context) {
        super(context);
        init();
    }

    // Constructor khi tạo từ XML
    public CustomBarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    // Cọ vẽ lưới ngang
    private Paint gridPaint;
    // Cọ vẽ background nhẹ
    private Paint bgPaint;

    // Khởi tạo các thông số cọ vẽ
    private void init() {
        // Cấu hình cọ vẽ cột
        barPaint = new Paint();
        barPaint.setColor(Color.parseColor("#FF6B00")); // Màu cam đẹp hơn
        barPaint.setStyle(Paint.Style.FILL);
        barPaint.setAntiAlias(true);

        // Cấu hình cọ vẽ text
        textPaint = new Paint();
        textPaint.setColor(Color.parseColor("#666666"));
        textPaint.setTextSize(32f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
        
        // Cọ vẽ lưới ngang
        gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#E0E0E0"));
        gridPaint.setStrokeWidth(2f);
        gridPaint.setPathEffect(new android.graphics.DashPathEffect(new float[]{10f, 10f}, 0f));
        gridPaint.setAntiAlias(true);
        
        // Cọ vẽ nền
        bgPaint = new Paint();
        bgPaint.setColor(Color.parseColor("#FAFAFA"));
        bgPaint.setStyle(Paint.Style.FILL);
    }

    // Nhận dữ liệu và cập nhật biểu đồ
    public void setData(Map<String, Integer> data) {
        labels.clear();
        values.clear();
        maxValue = 0;

        if (data == null || data.isEmpty()) return;

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            labels.add(entry.getKey());
            values.add(entry.getValue());
            
            if (entry.getValue() > maxValue) {
                maxValue = entry.getValue();
            }
        }

        // Yêu cầu vẽ lại view (gọi onDraw)
        invalidate();
    }

    // Hàm thực hiện vẽ nội dung biểu đồ lên Canvas
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (values.isEmpty()) {
            canvas.drawText("Chưa có dữ liệu doanh thu", getWidth() / 2f, getHeight() / 2f, textPaint);
            return;
        }

        int width = getWidth();
        int height = getHeight();

        // Vẽ nền mờ
        canvas.drawRoundRect(0, 0, width, height, 30f, 30f, bgPaint);

        // Lề dưới cho trục X
        int bottomMargin = 120;
        
        // Lề trên cho trục Y
        int topMargin = 80;

        // Chiều cao khả dụng để vẽ cột
        int chartHeight = height - bottomMargin - topMargin;
        
        // Vẽ lưới ngang (3 đường)
        for (int i = 0; i <= 3; i++) {
            int gridY = topMargin + (chartHeight * i / 3);
            canvas.drawLine(50, gridY, width - 50, gridY, gridPaint);
        }

        // Chia đều chiều rộng cho số lượng cột
        int sectionWidth = width / values.size();

        // Độ rộng của từng cột (chiếm 50% không gian section, gọn hơn)
        int barWidth = (int) (sectionWidth * 0.5);

        // Vẽ từng cột
        for (int i = 0; i < values.size(); i++) {
            // Tính tỷ lệ chiều cao so với giá trị lớn nhất (bảo vệ lỗi chia 0)
            float ratio = maxValue > 0 ? (float) values.get(i) / maxValue : 0;

            // Tính chiều cao pixel của cột (tối thiểu 10px để thấy được vạch)
            int barHeight = Math.max((int) (chartHeight * ratio), 10);

            // Tính toán 4 tọa độ của hình chữ nhật
            int left = (i * sectionWidth) + (sectionWidth - barWidth) / 2;
            int right = left + barWidth;
            int bottom = height - bottomMargin;
            int top = bottom - barHeight;

            // 1. Vẽ thân cột bo tròn góc trên
            android.graphics.RectF rectF = new android.graphics.RectF(left, top, right, bottom);
            // Gradient màu
            android.graphics.Shader shader = new android.graphics.LinearGradient(
                0, top, 0, bottom,
                Color.parseColor("#FF8F00"), Color.parseColor("#FF5722"),
                android.graphics.Shader.TileMode.CLAMP);
            barPaint.setShader(shader);
            
            // Vẽ cột bo góc 15px
            canvas.drawRoundRect(rectF, 15f, 15f, barPaint);

            // Xóa bớt phần bo góc ở đáy (để đáy phẳng)
            canvas.drawRect(left, bottom - 15f, right, bottom, barPaint);

            // 2. Vẽ text nhãn trục X (Ngày)
            String dateLabel = labels.get(i);
            if (dateLabel.length() >= 5) {
                dateLabel = dateLabel.substring(0, 5); // Hiển thị kiểu "15/06"
            }
            // In đậm chữ ngày
            textPaint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD));
            textPaint.setTextSize(30f);
            canvas.drawText(dateLabel, left + (barWidth / 2f), height - 50, textPaint);

            // 3. Vẽ text giá trị trên đỉnh cột
            textPaint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL));
            textPaint.setTextSize(28f);
            String valueLabel = "";
            if (values.get(i) >= 1000000) {
                valueLabel = String.format("%.1fM", values.get(i) / 1000000f);
            } else if (values.get(i) >= 1000) {
                valueLabel = String.valueOf(values.get(i) / 1000) + "k";
            } else {
                valueLabel = String.valueOf(values.get(i));
            }
            canvas.drawText(valueLabel, left + (barWidth / 2f), top - 15, textPaint);
        }
    }
}
