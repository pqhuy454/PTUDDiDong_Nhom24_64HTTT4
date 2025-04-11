package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ttPersonnelManagementHelper {

    private final Context context;
    private final FirebaseFirestore db;

    public ttPersonnelManagementHelper(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    // Giao diện callback để trả danh sách email về
    public interface OnEmailListLoadedListener {
        void onLoaded(List<String> emailList);
        void onError(String errorMessage);
    }

    // Thêm email nhân viên
    public void addStaffEmail(String email) {
        if (email == null || email.isEmpty()) {
            Toast.makeText(context, "Email không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("email", email);

        db.collection("staff")
                .document(email) // dùng email làm ID
                .set(data)
                .addOnSuccessListener(unused ->
                        Toast.makeText(context, "Đã thêm nhân viên", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Lỗi khi thêm: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    // Xóa email nhân viên
    public void deleteStaffEmail(String email) {
        if (email == null || email.isEmpty()) return;

        db.collection("staff")
                .document(email)
                .delete()
                .addOnSuccessListener(unused ->
                        Toast.makeText(context, "Đã xóa nhân viên", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Lỗi khi xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    // Load danh sách email từ Firestore
    public void loadStaffEmails(OnEmailListLoadedListener listener) {
        db.collection("staff")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> emails = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String email = doc.getString("email");
                        if (email != null) {
                            emails.add(email);
                        }
                    }
                    listener.onLoaded(emails);
                })
                .addOnFailureListener(e ->
                        listener.onError("Lỗi khi tải danh sách: " + e.getMessage())
                );
    }
}