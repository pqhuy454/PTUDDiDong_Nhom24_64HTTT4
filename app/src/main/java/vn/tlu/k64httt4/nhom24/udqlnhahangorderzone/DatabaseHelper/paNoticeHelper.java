package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper;

import android.util.Log;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.paNotice;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class paNoticeHelper {
    private FirebaseFirestore db;

    public paNoticeHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public void addNotice(String title, String content, String senderId, OnNoticeAddedListener listener) {
        // Nếu senderId là null, gán giá trị mặc định
        if (senderId == null || senderId.trim().isEmpty()) {
            senderId = "Anonymous";
        }
        paNotice notice = new paNotice(title, content, senderId);
        db.collection("notices")
                .add(notice)
                .addOnSuccessListener(documentReference -> listener.onSuccess(true))
                .addOnFailureListener(e -> {
                    Log.e("NoticeHelper", "Error adding notice", e);
                    listener.onSuccess(false);
                });
    }

    public void getNotices(NoticeCallback callback) {
        db.collection("notices")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<paNotice> notices = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        paNotice notice = doc.toObject(paNotice.class);
                        if (notice != null) {
                            notice.setId(doc.getId());
                            notices.add(notice);
                        }
                    }
                    callback.onComplete(notices);
                })
                .addOnFailureListener(e -> {
                    Log.e("NoticeHelper", "Error getting notices", e);
                    callback.onComplete(new ArrayList<>());
                });
    }

    public interface OnNoticeAddedListener {
        void onSuccess(boolean success);
    }

    public interface NoticeCallback {
        void onComplete(List<paNotice> notices);
    }
}