package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.paFood;

public class phConfirmOrderHelper {

    private final FirebaseFirestore db;

    public phConfirmOrderHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public interface OnConfirmListener {
        void onSuccess(String billId);
        void onFailure(Exception e);
    }

    public void confirmOrder(
            String tableId,
            String tableName,
            List<paFood> foodList,
            List<Integer> quantities,
            String orderPerson,
            String currentOrderId,
            @NonNull OnConfirmListener listener
    ) {
        Log.d("DEBUG_ORDER", "confirmOrder() with billId = " + currentOrderId);
        if (currentOrderId != null && !currentOrderId.trim().isEmpty() && !"null".equalsIgnoreCase(currentOrderId)) {
            updateExistingBill(currentOrderId, foodList, quantities, orderPerson, listener);
        } else {
            createNewBill(tableId, tableName, foodList, quantities, orderPerson, listener);
        }
    }

    private void createNewBill(String tableId, String tableName, List<paFood> foodList,
                               List<Integer> quantities, String orderPerson, OnConfirmListener listener) {
        String billId = db.collection("bills").document().getId();
        HashMap<String, Object> billData = new HashMap<>();
        billData.put("id", billId);
        billData.put("tableId", tableId);
        billData.put("tableName", tableName);
        billData.put("status", "Đang phục vụ");
        billData.put("timestamp", FieldValue.serverTimestamp());

        List<String> orderPersons = new ArrayList<>();
        orderPersons.add(orderPerson);
        billData.put("orderPerson", orderPersons);

        ArrayList<Map<String, Object>> items = new ArrayList<>();
        for (int i = 0; i < foodList.size(); i++) {
            paFood food = foodList.get(i);
            int quantity = quantities.get(i);

            HashMap<String, Object> item = new HashMap<>();
            item.put("foodId", food.getId());
            item.put("foodName", food.getName());
            item.put("price", food.getPrice());
            item.put("imageUrl", food.getImageUrl());
            item.put("quantity", quantity);
            items.add(item);
        }

        billData.put("items", items);

        db.collection("bills").document(billId).set(billData)
                .addOnSuccessListener(unused -> db.collection("tables").document(tableId)
                        .update("status", "Có khách", "currentOrderId", billId)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("DEBUG_ORDER", "Tạo bill mới thành công: " + billId);
                            listener.onSuccess(billId);
                        })
                        .addOnFailureListener(e -> {
                            Log.e("DEBUG_ORDER", "Lỗi update bàn: " + e.getMessage());
                            listener.onFailure(e);
                        }))
                .addOnFailureListener(e -> {
                    Log.e("DEBUG_ORDER", "Lỗi tạo bill mới: " + e.getMessage());
                    listener.onFailure(e);
                });
    }

    private void updateExistingBill(String billId, List<paFood> newFoods, List<Integer> newQuantities,
                                    String orderPerson, OnConfirmListener listener) {
        Log.d("DEBUG_ORDER", "Đang cập nhật billId: " + billId);
        db.collection("bills").document(billId).get()
                .addOnSuccessListener(snapshot -> {
                    List<Map<String, Object>> oldItems = (List<Map<String, Object>>) snapshot.get("items");

                    // ✅ Xử lý orderPerson là String hoặc List
                    Object orderPersonObj = snapshot.get("orderPerson");
                    List<String> oldPersons = new ArrayList<>();

                    if (orderPersonObj instanceof String) {
                        oldPersons.add((String) orderPersonObj);
                    } else if (orderPersonObj instanceof List) {
                        try {
                            oldPersons = (List<String>) orderPersonObj;
                        } catch (ClassCastException e) {
                            Log.e("DEBUG_ORDER", "orderPerson list cast failed: " + e.getMessage());
                            oldPersons = new ArrayList<>();
                        }
                    }

                    if (!oldPersons.contains(orderPerson)) {
                        oldPersons.add(orderPerson);
                    }

                    HashMap<String, Map<String, Object>> itemMap = new HashMap<>();

                    if (oldItems != null) {
                        for (Map<String, Object> item : oldItems) {
                            String foodId = (String) item.get("foodId");
                            itemMap.put(foodId, new HashMap<>(item));
                        }
                    }

                    for (int i = 0; i < newFoods.size(); i++) {
                        paFood food = newFoods.get(i);
                        int quantity = newQuantities.get(i);

                        if (itemMap.containsKey(food.getId())) {
                            Object qtyObj = itemMap.get(food.getId()).get("quantity");
                            int oldQty = 0;
                            if (qtyObj instanceof Long) {
                                oldQty = ((Long) qtyObj).intValue();
                            } else if (qtyObj instanceof Double) {
                                oldQty = ((Double) qtyObj).intValue();
                            }
                            itemMap.get(food.getId()).put("quantity", oldQty + quantity);
                        } else {
                            HashMap<String, Object> item = new HashMap<>();
                            item.put("foodId", food.getId());
                            item.put("foodName", food.getName());
                            item.put("price", food.getPrice());
                            item.put("imageUrl", food.getImageUrl());
                            item.put("quantity", quantity);
                            itemMap.put(food.getId(), item);
                        }
                    }

                    db.collection("bills").document(billId)
                            .update("items", new ArrayList<>(itemMap.values()), "orderPerson", oldPersons)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("DEBUG_ORDER", "Cập nhật bill thành công.");
                                listener.onSuccess(billId);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("DEBUG_ORDER", "Lỗi khi cập nhật bill: " + e.getMessage());
                                listener.onFailure(e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("DEBUG_ORDER", "Lỗi khi đọc bill cũ: " + e.getMessage());
                    listener.onFailure(e);
                });
    }
}
