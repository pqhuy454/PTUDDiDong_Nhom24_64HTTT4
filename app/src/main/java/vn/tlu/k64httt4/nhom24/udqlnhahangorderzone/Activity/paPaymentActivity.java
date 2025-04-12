package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Adapter.paPayAdapter;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper.paNoticeHelper;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper.paOngoingHelper;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper.paPaymentHelper;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper.paUserHelper;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.paFood;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.paPayment;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class paPaymentActivity extends AppCompatActivity {
    private RecyclerView recyclerOrdered;
    private TextView tvTableName, txtPrice;
    private Button btnThanhToan, btnTichDiem;
    private ImageView btnBack;

    private String tableId, billId, tableName;
    private List<paFood> foodList;
    private List<Integer> quantities;
    private long totalPrice;
    private FirebaseFirestore db;
    private paUserHelper userHelper;
    private String userId;
    private boolean pointsSaved = false; // Biến theo dõi trạng thái tích điểm

    // Activity Result Launcher để nhận kết quả từ paPointsActivity
    private final ActivityResultLauncher<Intent> pointsActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    pointsSaved = result.getData().getBooleanExtra("pointsSaved", false);
                    if (pointsSaved) {
                        Toast.makeText(this, "Đã tích điểm cho khách hàng", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pa_act_pay);

        recyclerOrdered = findViewById(R.id.recyclerOrdered);
        tvTableName = findViewById(R.id.tv_table_nam);
        txtPrice = findViewById(R.id.txtPrice);
        btnThanhToan = findViewById(R.id.btnThanhToan);
        btnTichDiem = findViewById(R.id.btnTichDiem);
        btnBack = findViewById(R.id.r5mgogsypwwu);

        tableId = getIntent().getStringExtra("tableId");
        billId = getIntent().getStringExtra("billId");
        tableName = getIntent().getStringExtra("tableName");

        if (billId == null || billId.trim().isEmpty() || "null".equalsIgnoreCase(billId)) {
            Toast.makeText(this, "Không có hóa đơn để hiển thị!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("PaymentActivity", "billId: " + billId + ", tableId: " + tableId + ", tableName: " + tableName);

        tvTableName.setText(tableName != null ? tableName : "Không xác định");
        recyclerOrdered.setLayoutManager(new LinearLayoutManager(this));

        btnBack.setOnClickListener(v -> finish());

        btnThanhToan.setOnClickListener(v -> processPayment());

        btnTichDiem.setOnClickListener(v -> {
            Intent intent = new Intent(paPaymentActivity.this, PointsActivity.class);
            intent.putExtra("totalPrice", totalPrice);
            pointsActivityResultLauncher.launch(intent);
        });

        db = FirebaseFirestore.getInstance();
        userHelper = new paUserHelper(this);

        // Kiểm tra đăng nhập
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để tiếp tục!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ttLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (userId == null || userId.trim().isEmpty()) {
            Toast.makeText(this, "Không thể xác định người dùng, vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ttLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        loadBill();
    }

    private void loadBill() {
        paOngoingHelper helper = new paOngoingHelper();
        helper.getBillById(billId, new paOngoingHelper.OnBillLoadedListener() {
            @Override
            public void onSuccess(String tableNameResult, List<paFood> foodListResult, List<Integer> quantitiesResult) {
                if (foodListResult == null || quantitiesResult == null || foodListResult.isEmpty()) {
                    Toast.makeText(paPaymentActivity.this, "Hóa đơn không có dữ liệu", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                HashMap<String, paFood> foodMap = new HashMap<>();
                HashMap<String, Integer> quantityMap = new HashMap<>();

                for (int i = 0; i < foodListResult.size(); i++) {
                    paFood food = foodListResult.get(i);
                    int qty = quantitiesResult.get(i);
                    String foodId = food.getId();
                    if (foodMap.containsKey(foodId)) {
                        quantityMap.put(foodId, quantityMap.get(foodId) + qty);
                    } else {
                        foodMap.put(foodId, food);
                        quantityMap.put(foodId, qty);
                    }
                }

                foodList = new ArrayList<>(foodMap.values());
                quantities = new ArrayList<>();
                totalPrice = 0;
                for (paFood food : foodList) {
                    String foodId = food.getId();
                    int qty = quantityMap.containsKey(foodId) ? quantityMap.get(foodId) : 0;
                    quantities.add(qty);
                    totalPrice += (long) (food.getPrice() * qty);
                    Log.d("PaymentActivity", "Food: " + food.getName() + ", Price: " + food.getPrice() + ", Quantity: " + qty);
                }

                paPayAdapter adapter = new paPayAdapter(foodList, quantities);
                recyclerOrdered.setAdapter(adapter);
                txtPrice.setText(NumberFormat.getNumberInstance(Locale.getDefault()).format(totalPrice) + "đ");
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("PaymentActivity", "Error loading bill: " + e.getMessage());
                Toast.makeText(paPaymentActivity.this, "Lỗi khi tải hóa đơn: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void processPayment() {
        if (foodList == null || quantities == null || foodList.isEmpty()) {
            Toast.makeText(this, "Dữ liệu thanh toán không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        userHelper.getUserEmail(userId, new paUserHelper.OnUserLoadedListener() {
            @Override
            public void onLoaded(String email) {
                paPayment payment = new paPayment();
                payment.setBillId(billId);
                payment.setTableId(tableId);
                payment.setTableName(tableName);
                payment.setFoodList(foodList);
                payment.setQuantities(quantities);
                payment.setOrderPerson(email);
                payment.setTimestamp(Timestamp.now());
                payment.setTotalPrice(totalPrice);

                Log.d("PaymentActivity", "Saving payment with timestamp: " + payment.getTimestamp());

                paPaymentHelper paymentHelper = new paPaymentHelper();
                paymentHelper.savePayment(payment, new paPaymentHelper.OnPaymentSavedListener() {
                    @Override
                    public void onSuccess() {
                        db.collection("bills").document(billId)
                                .update("status", "đã thanh toán")
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(paPaymentActivity.this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();

                                    String noticeTitle = "Thanh toán hóa đơn";
                                    String noticeContent = email + " đã thanh toán hóa đơn ID " + billId;
                                    paNoticeHelper noticeHelper = new paNoticeHelper();
                                    noticeHelper.addNotice(noticeTitle, noticeContent, userId, success -> {
                                        if (success) {
                                            Log.d("PaymentActivity", "Payment notice added successfully");
                                            sendNotificationToAllDevices(noticeTitle, noticeContent);
                                        } else {
                                            Log.e("PaymentActivity", "Failed to add payment notice");
                                        }
                                    });

                                    Intent intent = new Intent(paPaymentActivity.this, paOngoing.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("PaymentActivity", "Error updating bill status: " + e.getMessage());
                                    Toast.makeText(paPaymentActivity.this, "Lỗi khi cập nhật trạng thái hóa đơn: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("PaymentActivity", "Error saving payment: " + e.getMessage());
                        Toast.makeText(paPaymentActivity.this, "Lỗi khi thanh toán: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(paPaymentActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendNotificationToAllDevices(String title, String content) {
        FirebaseFirestore.getInstance().collection("devices")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        for (String token : queryDocumentSnapshots.getDocuments()
                                .stream()
                                .map(doc -> doc.getString("token"))
                                .toList()) {
                            new paAddNoticeActivity.SendNotificationTask(this).execute(token, title, content);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.w("FCM", "Error fetching tokens", e));
    }
}