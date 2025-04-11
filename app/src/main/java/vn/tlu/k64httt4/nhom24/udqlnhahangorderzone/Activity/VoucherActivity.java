package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Activity;

import android.app.Dialog;
import android.content.Intent;
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

        recyclerView = findViewById(R.id.voucherRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        voucherList = new ArrayList<>();
        adapter = new VoucherAdapter(voucherList);
        recyclerView.setAdapter(adapter);

        voucherHelper = new VoucherHelper();
        loadVouchers();

        // Xử lý sự kiện nhấn nút thêm voucher
        findViewById(R.id.addVoucherBtn).setOnClickListener(v -> showAddVoucherDialog());

        // Xử lý sự kiện nhấn nút Back
        backBtn = findViewById(R.id.backBtn);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                Intent intent = new Intent(VoucherActivity.this, phMainActivity.class);
                startActivity(intent);
                finish();
            });
        } else {
            Toast.makeText(this, "Không tìm thấy nút Back. Vui lòng kiểm tra layout.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadVouchers() {
        voucherHelper.getAllVouchers(new VoucherHelper.OnVouchersLoadedListener() {
            @Override
            public void onVouchersLoaded(List<Voucher> vouchers) {
                voucherList.clear();
                voucherList.addAll(vouchers);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(VoucherActivity.this, "Lỗi tải voucher: " + error, Toast.LENGTH_SHORT).show();
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

            // Truy vấn giá gốc từ Firestore
            FirebaseFirestore.getInstance().collection("items").document(id).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Double itemPrice = documentSnapshot.getDouble("price");
                            if (itemPrice != null) {
                                double originalPrice = itemPrice;
                                Voucher voucher = new Voucher(id, name, discountPercent, dayStart, dayEnd, originalPrice);

                                voucherHelper.addVoucher(voucher,
                                        () -> {
                                            Toast.makeText(this, "Thêm voucher thành công", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        },
                                        error -> Toast.makeText(this, "Lỗi khi lưu: " + error, Toast.LENGTH_SHORT).show());
                            } else {
                                Toast.makeText(this, "Dữ liệu món ăn không đầy đủ", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Không tìm thấy món ăn với ID: " + id, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi truy vấn giá: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        cancelBtn.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}