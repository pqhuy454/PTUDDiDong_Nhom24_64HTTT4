package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Adapter.phOrderAdapter;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper.phOrderedHelper;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.paFood;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

public class phOrderedActivity extends AppCompatActivity {

    private RecyclerView recyclerFoods;
    private Button btnAddFood; // ✅ Đổi kiểu từ ImageView → Button
    private ImageView btnBack;

    private String tableId, billId, tableName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ph_activity_ordered);

        recyclerFoods = findViewById(R.id.recyclerFoods);
        btnAddFood = findViewById(R.id.btnAddFood); // ✅ fix lỗi cast
        btnBack = findViewById(R.id.btnBack);

        tableId = getIntent().getStringExtra("tableId");
        billId = getIntent().getStringExtra("billId");

        if (billId == null || billId.trim().isEmpty() || "null".equalsIgnoreCase(billId)) {
            Toast.makeText(this, "Không có hóa đơn để hiển thị!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerFoods.setLayoutManager(new LinearLayoutManager(this));

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(phOrderedActivity.this, phOrderTableActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnAddFood.setOnClickListener(v -> {
            Intent intent = new Intent(phOrderedActivity.this, phOrderActivity.class);
            intent.putExtra("tableId", tableId);
            intent.putExtra("tableName", tableName);
            intent.putExtra("currentOrderId", billId);
            startActivity(intent);
        });

        loadBill();
    }

    private void loadBill() {
        phOrderedHelper helper = new phOrderedHelper();
        helper.getBillById(billId, new phOrderedHelper.OnBillLoadedListener() {
            @Override
            public void onSuccess(String tableNameResult, List<paFood> foodList, List<Integer> quantities) {
                tableName = tableNameResult;
                phOrderAdapter adapter = new phOrderAdapter(foodList, quantities);
                recyclerFoods.setAdapter(adapter);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(phOrderedActivity.this, "Lỗi khi tải hóa đơn", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
