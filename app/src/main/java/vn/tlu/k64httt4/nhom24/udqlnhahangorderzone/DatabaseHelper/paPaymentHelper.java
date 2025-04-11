package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper;

import android.util.Log;

import androidx.annotation.NonNull;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.paFood;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.paPayment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class paPaymentHelper {
    private final FirebaseFirestore db;

    public paPaymentHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public interface OnPaymentSavedListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface OnPaymentsLoadedListener {
        void onSuccess(List<paPayment> paymentList);
        void onFailure(Exception e);
    }

    public void savePayment(paPayment payment, @NonNull OnPaymentSavedListener listener) {
        if (payment == null || payment.getFoodList() == null || payment.getQuantities() == null) {
            listener.onFailure(new IllegalArgumentException("Dữ liệu thanh toán không hợp lệ"));
            return;
        }

        db.collection("payment").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int newId = queryDocumentSnapshots.size();
                    payment.setId(String.valueOf(newId));

                    Map<String, Object> paymentData = new HashMap<>();
                    paymentData.put("id", payment.getId());
                    paymentData.put("billId", payment.getBillId());
                    paymentData.put("tableId", payment.getTableId());
                    paymentData.put("tableName", payment.getTableName());
                    paymentData.put("orderPerson", payment.getOrderPerson());
                    paymentData.put("timestamp", payment.getTimestamp());
                    paymentData.put("totalPrice", payment.getTotalPrice());

                    List<Map<String, Object>> items = new ArrayList<>();
                    for (int i = 0; i < payment.getFoodList().size(); i++) {
                        paFood food = payment.getFoodList().get(i);
                        Map<String, Object> item = new HashMap<>();
                        item.put("foodId", food.getId());
                        item.put("foodName", food.getName());
                        item.put("imageUrl", food.getImageUrl());
                        item.put("price", food.getPrice());
                        item.put("quantity", payment.getQuantities().get(i));
                        items.add(item);
                    }
                    paymentData.put("items", items);

                    db.collection("payment").document(payment.getId())
                            .set(paymentData)
                            .addOnSuccessListener(aVoid -> {
                                Map<String, Object> tableUpdates = new HashMap<>();
                                tableUpdates.put("currentOrderId", "");
                                tableUpdates.put("status", "Trống");

                                db.collection("tables").document(payment.getTableId())
                                        .update(tableUpdates)
                                        .addOnSuccessListener(aVoid1 -> listener.onSuccess())
                                        .addOnFailureListener(listener::onFailure);
                            })
                            .addOnFailureListener(listener::onFailure);
                })
                .addOnFailureListener(listener::onFailure);
    }

    public void getAllPayments(@NonNull OnPaymentsLoadedListener listener) {
        db.collection("payment").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<paPayment> paymentList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            paPayment payment = new paPayment();
                            payment.setId(safeGetString(document.getString("id")));
                            payment.setBillId(safeGetString(document.getString("billId")));
                            payment.setTableId(safeGetString(document.getString("tableId")));
                            payment.setTableName(safeGetString(document.getString("tableName")));
                            payment.setOrderPerson(safeGetString(document.getString("orderPerson")));
                            payment.setTotalPrice(document.getLong("totalPrice") != null ? document.getLong("totalPrice") : 0);
                            payment.setTimestamp(document.getTimestamp("timestamp"));

                            List<paFood> foodList = new ArrayList<>();
                            List<Integer> quantities = new ArrayList<>();
                            List<?> items = document.get("items") instanceof List ? (List<?>) document.get("items") : null;
                            if (items != null) {
                                for (Object obj : items) {
                                    if (obj instanceof Map) {
                                        Map<?, ?> itemMap = (Map<?, ?>) obj;
                                        paFood food = new paFood();
                                        food.setId(safeGetString(itemMap.get("foodId")));
                                        food.setName(safeGetString(itemMap.get("foodName")));
                                        food.setImageUrl(safeGetString(itemMap.get("imageUrl")));
                                        food.setPrice(safeGetInt(itemMap.get("price")));
                                        foodList.add(food);
                                        quantities.add(safeGetInt(itemMap.get("quantity")));
                                    }
                                }
                            }
                            payment.setFoodList(foodList);
                            payment.setQuantities(quantities);
                            paymentList.add(payment);
                        } catch (Exception e) {
                            Log.e("PaymentHelper", "Error parsing payment: " + e.getMessage());
                        }
                    }
                    listener.onSuccess(paymentList);
                })
                .addOnFailureListener(listener::onFailure);
    }

    private String safeGetString(Object value) {
        return value != null ? value.toString() : "";
    }

    private int safeGetInt(Object value) {
        if (value instanceof Long) {
            return ((Long) value).intValue();
        } else if (value instanceof Integer) {
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