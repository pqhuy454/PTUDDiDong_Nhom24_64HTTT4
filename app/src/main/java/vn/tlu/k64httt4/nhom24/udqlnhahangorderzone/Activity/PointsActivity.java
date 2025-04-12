package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

public class PointsActivity extends AppCompatActivity {

    private EditText edtCustomerName, edtPhoneNumber;
    private Button btnSavePoints;
    private FirebaseFirestore db;
    private long totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lt_points_activity);

        // Ánh xạ view
        edtCustomerName = findViewById(R.id.edtCustomerName);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        btnSavePoints = findViewById(R.id.btnSavePoints);

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Nhận totalPrice từ Intent
        totalPrice = getIntent().getLongExtra("totalPrice", 0);

        // Sự kiện nhấn nút Lưu
        btnSavePoints.setOnClickListener(v -> savePoints());
    }

    private void savePoints() {
        String customerName = edtCustomerName.getText().toString().trim();
        String phoneNumber = edtPhoneNumber.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào
        if (customerName.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ họ tên và số điện thoại", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tính điểm (giả định: 1000 VNĐ = 1 điểm)
        int points = (int) (totalPrice / 1000);

        // Tạo dữ liệu điểm
        Map<String, Object> pointData = new HashMap<>();
        pointData.put("name", customerName);
        pointData.put("points", points);
        pointData.put("last_updated", com.google.firebase.Timestamp.now());

        // Lưu vào Firestore
        db.collection("points").document(phoneNumber)
                .set(pointData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Tích điểm thành công! Điểm: " + points, Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("pointsSaved", true);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi lưu điểm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}