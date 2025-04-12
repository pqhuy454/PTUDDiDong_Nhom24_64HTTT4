package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Adapter.phOrderAdapter;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper.phOrderHelper;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.paFood;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

public class phOrderActivity extends AppCompatActivity {

    private RecyclerView recyclerFoods;
    private Button btnOrder;
    private ImageView btnBack;
    private TextView tvTitle;
    private phOrderAdapter adapter;

    private String tableId, tableName, currentOrderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ph_activity_order);

        recyclerFoods = findViewById(R.id.recyclerFoods);
        btnOrder = findViewById(R.id.btnOrder);
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);

        tableId = getIntent().getStringExtra("tableId");
        tableName = getIntent().getStringExtra("tableName");
        currentOrderId = getIntent().getStringExtra("currentOrderId");

        tvTitle.setText("Chọn món cho " + tableName);

        recyclerFoods.setLayoutManager(new LinearLayoutManager(this));

        btnBack.setOnClickListener(v -> finish());

        btnOrder.setOnClickListener(v -> {
            HashMap<String, Integer> quantityMap = adapter.getQuantityMap();
            List<paFood> allFoods = adapter.getFoodList();

            ArrayList<paFood> selectedFoods = new ArrayList<>();
            ArrayList<Integer> selectedQuantities = new ArrayList<>();

            for (paFood food : allFoods) {
                int quantity = quantityMap.get(food.getId());
                if (quantity > 0) {
                    selectedFoods.add(food);
                    selectedQuantities.add(quantity);
                }
            }

            if (selectedFoods.isEmpty()) {
                Toast.makeText(this, "Bạn chưa chọn món nào!", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("DEBUG_ORDER", "currentOrderId gửi sang là: " + currentOrderId);
            Intent intent = new Intent(phOrderActivity.this, phConfirmOrderActivity.class);
            intent.putExtra("tableId", tableId);
            intent.putExtra("tableName", tableName);
            intent.putExtra("currentOrderId", currentOrderId);
            intent.putExtra("foodList", selectedFoods);
            intent.putExtra("quantities", selectedQuantities);
            startActivity(intent);
        });

        loadFoodList();
    }

    private void loadFoodList() {
        phOrderHelper helper = new phOrderHelper();
        helper.getAllFoods(new phOrderHelper.OnFoodsLoadedListener() {
            @Override
            public void onLoaded(List<paFood> foodList) {
                adapter = new phOrderAdapter(foodList);
                recyclerFoods.setAdapter(adapter);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(phOrderActivity.this, "Lỗi khi tải danh sách món", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
