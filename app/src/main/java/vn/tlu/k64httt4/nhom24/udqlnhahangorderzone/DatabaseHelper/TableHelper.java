package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.Table;

import java.util.ArrayList;
import java.util.List;

public class TableHelper {

    private final FirebaseFirestore db;
    private static final String COLLECTION_TABLES = "tables";

    public TableHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public void addTable(Table table, OnSuccessListener onSuccess, OnFailureListener onFailure) {
        if (table == null || table.getId() == null || table.getId().isEmpty()) {
            onFailure.onFailure("Dữ liệu bàn không hợp lệ");
            return;
        }
        db.collection(COLLECTION_TABLES)
                .document(table.getId())
                .set(table)
                .addOnSuccessListener(aVoid -> onSuccess.onSuccess())
                .addOnFailureListener(e -> onFailure.onFailure(e.getMessage()));
    }

    public void updateTable(Table table, OnSuccessListener onSuccess, OnFailureListener onFailure) {
        if (table == null || table.getId() == null || table.getId().isEmpty()) {
            onFailure.onFailure("Dữ liệu bàn không hợp lệ");
            return;
        }
        db.collection(COLLECTION_TABLES)
                .document(table.getId())
                .set(table)
                .addOnSuccessListener(aVoid -> onSuccess.onSuccess())
                .addOnFailureListener(e -> onFailure.onFailure(e.getMessage()));
    }

    public void deleteTable(String tableId, OnSuccessListener onSuccess, OnFailureListener onFailure) {
        if (tableId == null || tableId.isEmpty()) {
            onFailure.onFailure("ID bàn không hợp lệ");
            return;
        }
        db.collection(COLLECTION_TABLES)
                .document(tableId)
                .delete()
                .addOnSuccessListener(aVoid -> onSuccess.onSuccess())
                .addOnFailureListener(e -> onFailure.onFailure(e.getMessage()));
    }

    public void getAllTables(OnTablesLoadedListener listener) {
        db.collection(COLLECTION_TABLES)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Table> tableList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Table table = document.toObject(Table.class);
                                tableList.add(table);
                            } catch (Exception e) {
                                // Bỏ qua document lỗi
                            }
                        }
                        listener.onTablesLoaded(tableList);
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

    public interface OnTablesLoadedListener {
        void onTablesLoaded(List<Table> tableList);
        void onError(String error);
    }
}