package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Adapter.phOrderTableAdpter;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper.phOrderTableHelper;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.Table;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

public class phOrderTableActivity extends AppCompatActivity {

    private RecyclerView recyclerTables;
    private phOrderTableAdpter adapter;
    private ImageView btnBack;
    private phOrderTableHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ph_activity_order_table);

        recyclerTables = findViewById(R.id.recyclerTables);
        recyclerTables.setLayoutManager(new GridLayoutManager(this, 2));

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        helper = new phOrderTableHelper();
        loadTables();
    }

    private void loadTables() {
        helper.getAllTables(new phOrderTableHelper.OnTablesLoadedListener() {
            @Override
            public void onLoaded(List<Table> tableList) {
                adapter = new phOrderTableAdpter(tableList, table -> {
                    String status = table.getStatus();
                    String tableId = table.getId();
                    String tableName = table.getName();
                    String currentOrderId = table.getCurrentOrderId();

                    Log.d("TableClick", "Bàn: " + tableName + ", Trạng thái: " + status + ", OrderID: " + currentOrderId);

                    if ("Trống".equalsIgnoreCase(status)) {
                        Intent intent = new Intent(phOrderTableActivity.this, phOrderActivity.class);
                        intent.putExtra("tableId", tableId);
                        intent.putExtra("tableName", tableName);
                        intent.putExtra("currentOrderId", "");
                        startActivity(intent);
                    } else if ("Có khách".equalsIgnoreCase(status)) {
                        if (currentOrderId == null || currentOrderId.trim().isEmpty() || "null".equalsIgnoreCase(currentOrderId)) {
                            Toast.makeText(phOrderTableActivity.this, "Không tìm thấy hóa đơn của bàn!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(phOrderTableActivity.this, phOrderedActivity.class);
                        intent.putExtra("tableId", tableId);
                        intent.putExtra("billId", currentOrderId);
                        startActivity(intent);
                    } else {
                        Toast.makeText(phOrderTableActivity.this, "Trạng thái bàn không hợp lệ!", Toast.LENGTH_SHORT).show();
                    }
                });

                recyclerTables.setAdapter(adapter);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(phOrderTableActivity.this, "Lỗi khi tải danh sách bàn", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
