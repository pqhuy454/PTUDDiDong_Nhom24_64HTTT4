package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Activity.ttLoginActivity;

public class ttSignupHelper {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Context context;

    // Constructor
    public ttSignupHelper(Context context) {
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    // Đăng ký người dùng và lưu dữ liệu
    public void signupAndSaveUser(String email, String password, String name, String phone, String role) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Đăng ký thành công
                        String userId = mAuth.getCurrentUser().getUid();
                        saveUserToFirestore(userId, name, email, phone, role);
                    } else {
                        // Đăng ký thất bại
                        showAlertDialog("Đăng ký thất bại", "Lỗi: " + task.getException().getMessage(), false);
                    }
                });
    }

    // Lưu thông tin người dùng vào Firestore
    private void saveUserToFirestore(String userId, String name, String email, String phone, String role) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("phone", phone);
        user.put("role", role);

        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    showAlertDialog("Thông báo", "Đăng ký thành công!", true);
                })
                .addOnFailureListener(e -> {
                    showAlertDialog("Lỗi", "Lỗi khi lưu dữ liệu: " + e.getMessage(), false);
                });
    }

    // Phương thức hiển thị AlertDialog
    private void showAlertDialog(String title, String message, boolean redirect) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    if (redirect) {
                        Intent intent = new Intent(context, ttLoginActivity.class);
                        context.startActivity(intent);
                        ((AppCompatActivity) context).finish();
                    }
                })
                .setCancelable(false)
                .show();
    }
}