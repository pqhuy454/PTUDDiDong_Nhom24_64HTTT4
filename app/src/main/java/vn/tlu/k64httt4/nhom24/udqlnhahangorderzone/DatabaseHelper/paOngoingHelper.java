package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper;

import android.util.Log;

import androidx.annotation.NonNull;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.paFood;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class paOngoingHelper {

    private final FirebaseFirestore db;

    public paOngoingHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public interface OnBillLoadedListener {
        void onSuccess(String tableName, List<paFood> foodList, List<Integer> quantities);
        void onFailure(Exception e);
    }

    public void getBillById(@NonNull String billId, @NonNull OnBillLoadedListener listener) {
        db.collection("bills").document(billId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        listener.onFailure(new Exception("Hóa đơn không tồn tại"));
                        return;
                    }

                    String tableName = documentSnapshot.getString("tableName");

                    ArrayList<paFood> foodList = new ArrayList<>();
                    ArrayList<Integer> quantities = new ArrayList<>();

                    List<?> items = documentSnapshot.get("items") instanceof List ? (List<?>) documentSnapshot.get("items") : null;
                    if (items != null) {
                        for (Object obj : items) {
                            if (obj instanceof Map) {
                                Map<?, ?> itemMap = (Map<?, ?>) obj;

                                paFood food = new paFood();
                                food.setId(safeGetString(itemMap.get("foodId")));
                                food.setName(safeGetString(itemMap.get("foodName")));
                                food.setImageUrl(safeGetString(itemMap.get("imageUrl")));
                                food.setPrice(safeGetInt(itemMap.get("price")));

                                int qty = safeGetInt(itemMap.get("quantity"));

                                foodList.add(food);
                                quantities.add(qty);
                            }
                        }
                    }

                    listener.onSuccess(tableName, foodList, quantities);
                })
                .addOnFailureListener(e -> {
                    Log.e("phOrderedHelper", "Error loading bill: " + e.getMessage());
                    listener.onFailure(e);
                });
    }

    private String safeGetString(Object value) {
        return value != null ? value.toString() : "";
    }

    private int safeGetInt(Object value) {
        if (value instanceof Long) {
            return ((Long) value).intValue();
        }  else if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
}