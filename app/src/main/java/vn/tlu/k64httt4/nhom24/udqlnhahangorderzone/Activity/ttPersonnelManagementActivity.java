package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Adapter.ttPersonnelManagementAdapter;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper.ttPersonnelManagementHelper;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

public class ttPersonnelManagementActivity extends AppCompatActivity {

    private EditText edtEmail;
    private Button btnAddEmail;
    private RecyclerView recyclerEmails;
    private ttPersonnelManagementAdapter adapter;
    private List<String> emailList;
    private ttPersonnelManagementHelper helper;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tt_activity_personnel_management);

        edtEmail = findViewById(R.id.edtEmail);
        btnAddEmail = findViewById(R.id.btnAddEmail);
        recyclerEmails = findViewById(R.id.recyclerEmails);
        btnBack = findViewById(R.id.btnBack);

        helper = new ttPersonnelManagementHelper(this);
        emailList = new ArrayList<>();

        adapter = new ttPersonnelManagementAdapter(emailList, (email, position) -> {
            showConfirmDialog("Xác nhận", "Bạn có chắc muốn xóa nhân viên này?", () -> {
                helper.deleteStaffEmail(email);
                showAlert("Đã xóa nhân viên", this::loadEmailListFromFirestore);
            });
        });

        recyclerEmails.setLayoutManager(new LinearLayoutManager(this));
        recyclerEmails.setAdapter(adapter);

        btnAddEmail.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                showAlert("Vui lòng nhập email", null);
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showAlert("Email không đúng định dạng", null);
                return;
            }

            if (emailList.contains(email)) {
                showAlert("Email này đã tồn tại", null);
                return;
            }

            helper.addStaffEmail(email);
            showAlert("Đã thêm nhân viên", () -> {
                edtEmail.setText("");
                loadEmailListFromFirestore();
            });
        });

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ttPersonnelManagementActivity.this, phMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        loadEmailListFromFirestore();
    }

    private void loadEmailListFromFirestore() {
        helper.loadStaffEmails(new ttPersonnelManagementHelper.OnEmailListLoadedListener() {
            @Override
            public void onLoaded(List<String> emails) {
                emailList.clear();
                emailList.addAll(emails);
                adapter.setData(emails);
            }

            @Override
            public void onError(String errorMessage) {
                showAlert(errorMessage, null);
            }
        });
    }

    // Hiển thị dialog thông báo
    private void showAlert(String message, Runnable onOk) {
        new AlertDialog.Builder(this)
                .setTitle("Thông báo")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    if (onOk != null) onOk.run();
                })
                .show();
    }

    // Hiển thị dialog xác nhận xóa
    private void showConfirmDialog(String title, String message, Runnable onConfirm) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Xóa", (dialog, which) -> {
                    dialog.dismiss();
                    if (onConfirm != null) onConfirm.run();
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
