package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper;

import android.content.Context;
import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;

public class paUserHelper {
    private FirebaseFirestore db;
    private Context context;

    public paUserHelper(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    // Callback interface để trả về thông tin người dùng
    public interface OnUserLoadedListener {
        void onLoaded(String email);
        void onError(String errorMessage);
    }

    // Phương thức lấy email của người dùng dựa trên userId
    public void getUserEmail(String userId, OnUserLoadedListener listener) {
        if (userId == null || userId.trim().isEmpty()) {
            listener.onError("User ID không hợp lệ");
            return;
        }

        db.collection("users") // Giả sử thông tin người dùng được lưu trong collection "users"
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String email = documentSnapshot.getString("email");
                        if (email != null && !email.trim().isEmpty()) {
                            listener.onLoaded(email);
                        } else {
                            listener.onError("Email không tồn tại trong dữ liệu người dùng");
                        }
                    } else {
                        listener.onError("Không tìm thấy người dùng với ID: " + userId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("paUserHelper", "Lỗi khi lấy thông tin người dùng: " + e.getMessage());
                    listener.onError("Lỗi khi lấy thông tin người dùng: " + e.getMessage());
                });
    }
}