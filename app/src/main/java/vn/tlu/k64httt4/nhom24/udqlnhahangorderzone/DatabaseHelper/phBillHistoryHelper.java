package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Adapter.phBillHistotyAdapter;

public class phBillHistoryHelper {

    private final Context context;
    private final FirebaseFirestore db;

    public phBillHistoryHelper(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    public void loadBillHistory(RecyclerView recyclerView) {
        db.collection("payment")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Map<String, Object>> billList = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        try {
                            Map<String, Object> bill = new HashMap<>();
                            bill.put("billId", doc.getId());
                            bill.put("timestamp", doc.getDate("timestamp"));
                            bill.put("tableName", doc.getString("tableName"));
                            bill.put("orderPerson", doc.getString("orderPerson"));
                            bill.put("totalPrice", doc.getDouble("totalPrice"));
                            bill.put("items", doc.get("items"));
                            billList.add(bill);
                        } catch (Exception e) {
                            Log.e("BillHelper", "Lỗi đọc bill: " + e.getMessage());
                        }
                    }

                    // ✅ Sắp xếp theo thời gian giảm dần (mới nhất → cũ nhất)
                    Collections.sort(billList, new Comparator<Map<String, Object>>() {
                        @Override
                        public int compare(Map<String, Object> a, Map<String, Object> b) {
                            Date t1 = (Date) a.get("timestamp");
                            Date t2 = (Date) b.get("timestamp");
                            return t2.compareTo(t1);
                        }
                    });

                    recyclerView.setAdapter(new phBillHistotyAdapter(context, billList));
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Không tải được dữ liệu hóa đơn", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }
}
