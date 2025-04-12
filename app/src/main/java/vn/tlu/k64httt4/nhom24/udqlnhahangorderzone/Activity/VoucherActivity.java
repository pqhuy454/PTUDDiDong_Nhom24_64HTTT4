package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Adapter.VoucherAdapter;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper.VoucherHelper;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.Voucher;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

import java.util.ArrayList;
import java.util.List;

public class VoucherActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private VoucherAdapter adapter;
    private List<Voucher> voucherList;
    private VoucherHelper voucherHelper;
    private Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lt_activity_voucher);

        // Ánh xạ các view
        recyclerView = findViewById(R.id.voucherRecyclerView);
        backBtn = findViewById(R.id.backBtn);

        // Kiểm tra ánh xạ
        if (recyclerView == null || backBtn == null) {
            Toast.makeText(this, "Lỗi ánh xạ view", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Khởi tạo VoucherHelper
        voucherHelper = new VoucherHelper();

        // Thiết lập RecyclerView
        voucherList = new ArrayList<>();
        adapter = new VoucherAdapter(voucherList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Tải danh sách voucher từ Firestore
        loadVouchers();

        // Xử lý sự kiện nhấn nút thêm voucher
        findViewById(R.id.addVoucherBtn).setOnClickListener(v -> showAddVoucherDialog());

        // Xử lý sự kiện nhấn nút Back
        backBtn.setOnClickListener(v -> finish());
    }

    private void loadVouchers() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_LONG).show();
            return;
        }
        voucherHelper.getAllVouchers(new VoucherHelper.OnVouchersLoadedListener() {
            @Override
            public void onVouchersLoaded(List<Voucher> vouchers) {
                runOnUiThread(() -> {
                    voucherList.clear();
                    voucherList.addAll(vouchers);
                    adapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(VoucherActivity.this, "Lỗi tải dữ liệu: " + error, Toast.LENGTH_LONG).show());
            }
        });
    }

    private void showAddVoucherDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.lt_dialog_add_voucher);

        EditText idInput = dialog.findViewById(R.id.voucherIdInput);
        EditText nameInput = dialog.findViewById(R.id.voucherNameInput);
        EditText discountInput = dialog.findViewById(R.id.discountPercentInput);
        EditText dayStartInput = dialog.findViewById(R.id.dayStartInput);
        EditText dayEndInput = dialog.findViewById(R.id.dayEndInput);
        Button saveBtn = dialog.findViewById(R.id.saveBtn);
        Button cancelBtn = dialog.findViewById(R.id.cancelBtn);

        saveBtn.setOnClickListener(v -> {
            String id = idInput.getText().toString().trim();
            String name = nameInput.getText().toString().trim();
            String discountStr = discountInput.getText().toString().trim();
            String dayStart = dayStartInput.getText().toString().trim();
            String dayEnd = dayEndInput.getText().toString().trim();

            // Kiểm tra dữ liệu đầu vào
            if (id.isEmpty() || name.isEmpty() || discountStr.isEmpty() || dayStart.isEmpty() || dayEnd.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            double discountPercent;
            try {
                discountPercent = Double.parseDouble(discountStr);
                if (discountPercent < 0 || discountPercent > 100) {
                    Toast.makeText(this, "Phần trăm giảm giá phải từ 0 đến 100", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Phần trăm giảm giá không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Truy vấn món ăn từ Firestore dựa trên document ID
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("items").document(id).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Lấy giá từ trường "price"
                            Double itemPrice = documentSnapshot.getDouble("price");
                            if (itemPrice != null) {
                                double originalPrice = itemPrice;
                                Voucher voucher = new Voucher(id, name, discountPercent, dayStart, dayEnd, originalPrice);

                                // Thêm voucher vào Firestore
                                voucherHelper.addVoucher(voucher,
                                        () -> runOnUiThread(() -> {
                                            Toast.makeText(this, "Thêm voucher thành công", Toast.LENGTH_SHORT).show();
                                            loadVouchers(); // Tải lại danh sách
                                            dialog.dismiss();
                                        }),
                                        error -> runOnUiThread(() -> Toast.makeText(this, "Lỗi khi thêm voucher: " + error, Toast.LENGTH_LONG).show()));
                            } else {
                                Toast.makeText(this, "Giá món ăn không tồn tại trong dữ liệu", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(this, "Không tìm thấy món ăn với ID: " + id, Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(e -> runOnUiThread(() -> Toast.makeText(this, "Lỗi truy vấn món ăn: " + e.getMessage(), Toast.LENGTH_LONG).show()));
        });

        cancelBtn.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVouchers();
    }
}