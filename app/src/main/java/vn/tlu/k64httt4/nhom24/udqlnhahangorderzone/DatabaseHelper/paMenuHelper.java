package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.paFood;

import java.util.ArrayList;
import java.util.List;

public class paMenuHelper {

    private final FirebaseFirestore db;
    private static final String COLLECTION_FOODS = "foods";

    public paMenuHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public void addFood(paFood food, OnSuccessListener onSuccess, OnFailureListener onFailure) {
        if (food == null || food.getId() == null) {
            onFailure.onFailure("Dữ liệu món ăn không hợp lệ");
            return;
        }
        db.collection(COLLECTION_FOODS)
                .document(food.getId())
                .set(food)
                .addOnSuccessListener(aVoid -> onSuccess.onSuccess())
                .addOnFailureListener(e -> onFailure.onFailure(e.getMessage()));
    }

    public void updateFood(paFood food, OnSuccessListener onSuccess, OnFailureListener onFailure) {
        if (food == null || food.getId() == null) {
            onFailure.onFailure("Dữ liệu món ăn không hợp lệ");
            return;
        }
        db.collection(COLLECTION_FOODS)
                .document(food.getId())
                .set(food)
                .addOnSuccessListener(aVoid -> onSuccess.onSuccess())
                .addOnFailureListener(e -> onFailure.onFailure(e.getMessage()));
    }

    public void deleteFood(String foodId, OnSuccessListener onSuccess, OnFailureListener onFailure) {
        if (foodId == null || foodId.isEmpty()) {
            onFailure.onFailure("ID món ăn không hợp lệ");
            return;
        }
        db.collection(COLLECTION_FOODS)
                .document(foodId)
                .delete()
                .addOnSuccessListener(aVoid -> onSuccess.onSuccess())
                .addOnFailureListener(e -> onFailure.onFailure(e.getMessage()));
    }

    public void getAllFoods(OnFoodsLoadedListener listener) {
        db.collection(COLLECTION_FOODS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<paFood> foodList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                paFood food = document.toObject(paFood.class);
                                foodList.add(food);
                            } catch (Exception e) {
                                // Bỏ qua document lỗi
                            }
                        }
                        listener.onFoodsLoaded(foodList);
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

    public interface OnFoodsLoadedListener {
        void onFoodsLoaded(List<paFood> foodList);
        void onError(String error);
    }
}