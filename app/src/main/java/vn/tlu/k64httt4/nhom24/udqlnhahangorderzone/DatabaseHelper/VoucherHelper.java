package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.Voucher;

import java.util.ArrayList;
import java.util.List;

public class VoucherHelper {

    private final FirebaseFirestore db;
    private static final String COLLECTION_VOUCHERS = "vouchers";

    public VoucherHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public void addVoucher(Voucher voucher, OnSuccessListener onSuccess, OnFailureListener onFailure) {
        if (voucher == null || voucher.getId() == null) {
            onFailure.onFailure("Dữ liệu voucher không hợp lệ");
            return;
        }
        db.collection(COLLECTION_VOUCHERS)
                .document(voucher.getId())
                .set(voucher)
                .addOnSuccessListener(aVoid -> onSuccess.onSuccess())
                .addOnFailureListener(e -> onFailure.onFailure(e.getMessage()));
    }

    public void updateVoucher(Voucher voucher, OnSuccessListener onSuccess, OnFailureListener onFailure) {
        if (voucher == null || voucher.getId() == null) {
            onFailure.onFailure("Dữ liệu voucher không hợp lệ");
            return;
        }
        db.collection(COLLECTION_VOUCHERS)
                .document(voucher.getId())
                .set(voucher)
                .addOnSuccessListener(aVoid -> onSuccess.onSuccess())
                .addOnFailureListener(e -> onFailure.onFailure(e.getMessage()));
    }

    public void deleteVoucher(String voucherId, OnSuccessListener onSuccess, OnFailureListener onFailure) {
        if (voucherId == null || voucherId.isEmpty()) {
            onFailure.onFailure("ID voucher không hợp lệ");
            return;
        }
        db.collection(COLLECTION_VOUCHERS)
                .document(voucherId)
                .delete()
                .addOnSuccessListener(aVoid -> onSuccess.onSuccess())
                .addOnFailureListener(e -> onFailure.onFailure(e.getMessage()));
    }

    public void getAllVouchers(OnVouchersLoadedListener listener) {
        db.collection(COLLECTION_VOUCHERS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Voucher> voucherList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Voucher voucher = document.toObject(Voucher.class);
                                voucherList.add(voucher);
                            } catch (Exception e) {
                                // Bỏ qua document lỗi
                            }
                        }
                        listener.onVouchersLoaded(voucherList);
                    } else {
                        listener.onError(task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định");
                    }
                });
    }

    public interface OnSuccessListener {
        void onSuccess();
    }

    public interface OnFailureListener {
        void onFailure(String error);
    }

    public interface OnVouchersLoadedListener {
        void onVouchersLoaded(List<Voucher> voucherList);
        void onError(String error);
    }
}