package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ttLoginHelper {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Context context;

    // Constructor
    public ttLoginHelper(Context context) {
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    // Đăng nhập người dùng và lấy thông tin
    public void loginUser(String email, String password, OnUserInfoRetrievedListener listener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Đăng nhập thành công, lấy UID và thông tin người dùng
                        String userId = mAuth.getCurrentUser().getUid();
                        db.collection("users").document(userId)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        Map<String, Object> userInfo = new HashMap<>();
                                        userInfo.put("name", documentSnapshot.getString("name"));
                                        userInfo.put("email", documentSnapshot.getString("email"));
                                        userInfo.put("phone", documentSnapshot.getString("phone"));
                                        userInfo.put("role", documentSnapshot.getString("role"));
                                        listener.onUserInfoRetrieved(userInfo);
                                    } else {
                                        showAlertDialog("Lỗi", "Không tìm thấy thông tin người dùng");
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    showAlertDialog("Lỗi", "Lỗi khi lấy thông tin: " + e.getMessage());
                                });
                    } else {
                        // Đăng nhập thất bại
                        showAlertDialog("Đăng nhập thất bại", "Lỗi: " + task.getException().getMessage());
                    }
                });
    }

    // Phương thức hiển thị AlertDialog
    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    // Interface để trả về thông tin người dùng
    public interface OnUserInfoRetrievedListener {
        void onUserInfoRetrieved(Map<String, Object> userInfo);
    }
}