package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Adapter.phFoodOderedAdapter;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

public class phBillDetailActivity extends AppCompatActivity {

    private TextView tvBillId, tvDate, tvOrderPerson, tvTableName, tvTotalPrice;
    private RecyclerView rvBillItems;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ph_activity_bill_detail);

        // Ánh xạ View
        tvBillId = findViewById(R.id.tvBillId);
        tvDate = findViewById(R.id.tvDate);
        tvOrderPerson = findViewById(R.id.tvOrderPerson);
        tvTableName = findViewById(R.id.tvTableName);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        rvBillItems = findViewById(R.id.rvBillItems);
        btnBack = findViewById(R.id.btnBack);

        rvBillItems.setLayoutManager(new LinearLayoutManager(this));

        // Nhận dữ liệu hóa đơn từ intent
        Map<String, Object> bill = (Map<String, Object>) getIntent().getSerializableExtra("bill");
        if (bill != null) {
            tvBillId.setText("Mã hóa đơn: " + bill.get("billId"));
            Date timestamp = (Date) bill.get("timestamp");
            String dateStr = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(timestamp);
            tvDate.setText("Ngày: " + dateStr);
            tvOrderPerson.setText("Nhân viên: " + bill.get("orderPerson"));
            tvTableName.setText("Bàn: " + bill.get("tableName"));

            double total = bill.get("totalPrice") != null ? (double) bill.get("totalPrice") : 0;
            tvTotalPrice.setText("Tổng tiền: " + String.format("%,.0f", total) + "đ");

            // Lấy danh sách món ăn
            List<Map<String, Object>> items = (List<Map<String, Object>>) bill.get("items");
            List<android.util.Pair<String, Integer>> foodStats = new ArrayList<>();

            if (items != null) {
                for (Map<String, Object> item : items) {
                    String name = (String) item.get("foodName");
                    Long quantity = (Long) item.get("quantity");
                    if (name != null && quantity != null) {
                        foodStats.add(new android.util.Pair<>(name, quantity.intValue()));
                    }
                }
            }

            rvBillItems.setAdapter(new phFoodOderedAdapter(foodStats));
        }

        btnBack.setOnClickListener(v -> finish());
    }
}
