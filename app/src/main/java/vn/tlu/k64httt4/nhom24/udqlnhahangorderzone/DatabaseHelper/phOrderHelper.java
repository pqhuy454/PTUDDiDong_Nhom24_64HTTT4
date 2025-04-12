package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.paFood;

public class phOrderHelper {

    public interface OnFoodsLoadedListener {
        void onLoaded(List<paFood> foodList);
        void onError(Exception e);
    }

    private final FirebaseFirestore db;

    public phOrderHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public void getAllFoods(@NonNull final OnFoodsLoadedListener listener) {
        CollectionReference foodRef = db.collection("foods");

        foodRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<paFood> foodList = new ArrayList<>();
                QuerySnapshot result = task.getResult();

                if (result != null) {
                    for (QueryDocumentSnapshot doc : result) {
                        paFood food = doc.toObject(paFood.class);
                        foodList.add(food);
                    }
                    listener.onLoaded(foodList);
                } else {
                    listener.onLoaded(new ArrayList<>());
                }
            } else {
                listener.onError(task.getException());
                Log.e("Firestore", "Lỗi khi load foods", task.getException());
            }
        });
    }
}
