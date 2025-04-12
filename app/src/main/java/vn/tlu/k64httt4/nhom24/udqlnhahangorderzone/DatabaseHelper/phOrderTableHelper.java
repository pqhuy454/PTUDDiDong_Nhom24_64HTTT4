package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.Table;

public class phOrderTableHelper {

    public interface OnTablesLoadedListener {
        void onLoaded(List<Table> tableList);
        void onError(Exception e);
    }

    private final FirebaseFirestore db;

    public phOrderTableHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public void getAllTables(@NonNull final OnTablesLoadedListener listener) {
        CollectionReference tablesRef = db.collection("tables");

        tablesRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Table> tableList = new ArrayList<>();
                        QuerySnapshot result = task.getResult();

                        if (result != null) {
                            for (QueryDocumentSnapshot doc : result) {
                                Table table = doc.toObject(Table.class);
                                tableList.add(table);
                            }
                            listener.onLoaded(tableList);
                        } else {
                            listener.onLoaded(new ArrayList<>());
                        }

                    } else {
                        listener.onError(task.getException());
                        Log.e("Firestore", "Lỗi khi load tables", task.getException());
                    }
                });
    }
}
