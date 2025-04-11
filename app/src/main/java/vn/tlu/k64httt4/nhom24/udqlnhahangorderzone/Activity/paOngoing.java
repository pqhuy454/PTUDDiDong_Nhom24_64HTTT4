package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Adapter.paOngoingAdapter;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper.paPaymentHelper;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.paBill;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.paFood;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.paPayment;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class paOngoing extends AppCompatActivity {
    private RecyclerView recyclerOrder;
    private Button btnOngoing;
    private ImageView btnBack;
    private FirebaseFirestore db;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    initializeUI();
                } else {
                    Toast.makeText(this, "Quyền thông báo bị từ chối. Một số tính năng có thể không hoạt động.", Toast.LENGTH_LONG).show();
                    initializeUI();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                initializeUI();
            }
        } else {
            initializeUI();
        }
    }

    private void initializeUI() {
        try {
            setContentView(R.layout.pa_act_my_order);
        } catch (Exception e) {
            Log.e("phMyOrderActivity", "Error setting content view: " + e.getMessage());
            Toast.makeText(this, "Lỗi giao diện: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        recyclerOrder = findViewById(R.id.recyclerOrder);
        btnOngoing = findViewById(R.id.btnOngoing);
        btnBack = findViewById(R.id.r5mgogsypwwu);

        db = FirebaseFirestore.getInstance();
        recyclerOrder.setLayoutManager(new LinearLayoutManager(this));

        btnBack.setOnClickListener(v -> finish());

        btnOngoing.setOnClickListener(v -> {
            btnOngoing.setTextColor(getResources().getColor(R.color.orange));
            loadOngoingBills();
        });



        btnOngoing.performClick();
    }

    private void loadOngoingBills() {
        db.collection("bills")
                .whereEqualTo("status", "Đang phục vụ")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("phMyOrderActivity", "Số lượng document trả về: " + queryDocumentSnapshots.size());
                    List<paBill> billList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            Log.d("phMyOrderActivity", "Document ID: " + document.getId() + ", Status: " + document.getString("status"));
                            paBill bill = new paBill();
                            bill.setId(document.getId());
                            bill.setTableId(document.getString("tableId"));
                            bill.setTableName(document.getString("tableName"));
                            bill.setStatus(document.getString("status"));

                            String timestampStr;
                            Object timestampObj = document.get("timestamp");
                            if (timestampObj instanceof Timestamp) {
                                Timestamp timestamp = (Timestamp) timestampObj;
                                SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy 'at' h:mm:ss a 'UTC'Z", Locale.getDefault());
                                timestampStr = sdf.format(timestamp.toDate());
                            } else if (timestampObj instanceof String) {
                                timestampStr = (String) timestampObj;
                            } else {
                                timestampStr = "Unknown";
                            }
                            bill.setTimestamp(timestampStr);

                            List<paFood> foodList = new ArrayList<>();
                            List<Integer> quantities = new ArrayList<>();
                            List<?> items = (List<?>) document.get("items");
                            if (items != null) {
                                for (Object obj : items) {
                                    if (obj instanceof Map) {
                                        Map<?, ?> itemMap = (Map<?, ?>) obj;
                                        paFood food = new paFood();
                                        food.setId((String) itemMap.get("foodId"));
                                        food.setName((String) itemMap.get("foodName"));
                                        food.setImageUrl((String) itemMap.get("imageUrl"));
                                        food.setPrice((itemMap.get("price") instanceof Long) ? ((Long) itemMap.get("price")).intValue() : 0);
                                        foodList.add(food);

                                        int qty = (itemMap.get("quantity") instanceof Long) ? ((Long) itemMap.get("quantity")).intValue() : 0;
                                        quantities.add(qty);
                                    }
                                }
                            }
                            bill.setFoodList(foodList);
                            bill.setQuantities(quantities);

                            long totalPrice = 0;
                            for (int i = 0; i < foodList.size(); i++) {
                                totalPrice += foodList.get(i).getPrice() * quantities.get(i);
                            }
                            bill.setTotalPrice(totalPrice);

                            billList.add(bill);
                        } catch (Exception e) {
                            Log.e("phMyOrderActivity", "Error parsing bill: " + e.getMessage());
                        }
                    }

                    Log.d("phMyOrderActivity", "Số lượng bill trong billList: " + billList.size());
                    paOngoingAdapter adapter = new paOngoingAdapter(this, billList);
                    recyclerOrder.setAdapter(adapter);
                    recyclerOrder.getAdapter().notifyDataSetChanged();

                    if (billList.isEmpty()) {
                        Toast.makeText(this, "Không có bản ghi", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("phMyOrderActivity", "Error loading bills: " + e.getMessage());
                    Toast.makeText(this, "Lỗi khi tải danh sách hóa đơn: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}