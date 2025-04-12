package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Adapter.phFoodOderedAdapter;

public class phStatisticalHelper {

    private final FirebaseFirestore db;
    private final CollectionReference paymentRef;
    private final Context context;

    public phStatisticalHelper(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
        paymentRef = db.collection("payment");
    }

    public void getStatsByDate(String selectedDate, TextView tvTotalBills, TextView tvTotalRevenue, TextView tvAvgRevenue, RecyclerView recyclerView) {
        paymentRef.get().addOnSuccessListener(querySnapshot -> {
            int totalBill = 0;
            double totalRevenue = 0;
            Map<String, Integer> foodStats = new HashMap<>();

            for (QueryDocumentSnapshot doc : querySnapshot) {
                try {
                    Date timestampDate = doc.getDate("timestamp");
                    if (timestampDate == null) continue;

                    String dateStr = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(timestampDate);
                    Log.d("DEBUG_DATE", "Bill date: " + dateStr + " vs selected: " + selectedDate);
                    if (!dateStr.equals(selectedDate)) continue;

                    totalBill++;
                    totalRevenue += doc.getDouble("totalPrice") != null ? doc.getDouble("totalPrice") : 0;

                    Object foodObj = doc.get("items");
                    if (foodObj instanceof List) {
                        List<Map<String, Object>> foods = (List<Map<String, Object>>) foodObj;
                        for (Map<String, Object> item : foods) {
                            String name = (String) item.get("foodName");
                            Long quantity = (Long) item.get("quantity");
                            if (name != null && quantity != null) {
                                int currentQuantity = foodStats.containsKey(name) ? foodStats.get(name) : 0;
                                foodStats.put(name, currentQuantity + quantity.intValue());
                            }
                        }
                    }

                } catch (Exception e) {
                    Log.e("Firestore", "Lỗi đọc document: " + e.getMessage());
                }
            }

            double avg = totalRevenue; // trung bình/ngày theo ngày = tổng

            tvTotalBills.setText("Tổng số bill: " + totalBill);
            tvTotalRevenue.setText("Tổng doanh thu: " + (int) totalRevenue + "đ");
            tvAvgRevenue.setText("Doanh thu trung bình/ngày: " + (int) avg + "đ");

            setFoodStatsToRecycler(foodStats, recyclerView);
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Lỗi Firestore: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        });
    }

    public void getStatsByMonth(String selectedMonth, TextView tvTotalBills, TextView tvTotalRevenue, TextView tvAvgRevenue, RecyclerView recyclerView) {
        paymentRef.get().addOnSuccessListener(querySnapshot -> {
            int totalBill = 0;
            double totalRevenue = 0;
            Map<String, Integer> foodStats = new HashMap<>();
            Set<String> distinctDays = new HashSet<>();

            for (QueryDocumentSnapshot doc : querySnapshot) {
                try {
                    Date timestampDate = doc.getDate("timestamp");
                    if (timestampDate == null) continue;

                    String docMonth = new SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(timestampDate);
                    if (!docMonth.equals(selectedMonth)) continue;

                    String docDay = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(timestampDate);
                    distinctDays.add(docDay);

                    totalBill++;
                    totalRevenue += doc.getDouble("totalPrice") != null ? doc.getDouble("totalPrice") : 0;

                    Object foodObj = doc.get("items");
                    if (foodObj instanceof List) {
                        List<Map<String, Object>> foods = (List<Map<String, Object>>) foodObj;
                        for (Map<String, Object> item : foods) {
                            String name = (String) item.get("foodName");
                            Long quantity = (Long) item.get("quantity");
                            if (name != null && quantity != null) {
                                int currentQuantity = foodStats.containsKey(name) ? foodStats.get(name) : 0;
                                foodStats.put(name, currentQuantity + quantity.intValue());
                            }
                        }
                    }

                } catch (Exception e) {
                    Log.e("Firestore", "Lỗi đọc document: " + e.getMessage());
                }
            }

            double avg = distinctDays.size() > 0 ? (totalRevenue / distinctDays.size()) : 0;

            tvTotalBills.setText("Tổng số bill: " + totalBill);
            tvTotalRevenue.setText("Tổng doanh thu: " + (int) totalRevenue + "đ");
            tvAvgRevenue.setText("Doanh thu trung bình/ngày: " + (int) avg + "đ");

            setFoodStatsToRecycler(foodStats, recyclerView);
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Lỗi Firestore: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        });
    }

    private void setFoodStatsToRecycler(Map<String, Integer> foodStats, RecyclerView recyclerView) {
        List<Pair<String, Integer>> foodList = new ArrayList<>();
        for (String name : foodStats.keySet()) {
            foodList.add(new Pair<>(name, foodStats.get(name)));
        }

        // ✅ Sắp xếp từ món bán nhiều nhất xuống ít nhất (API 23-compatible)
        Collections.sort(foodList, new Comparator<Pair<String, Integer>>() {
            @Override
            public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
                return o2.second.compareTo(o1.second); // giảm dần
            }
        });

        Log.d("DEBUG", "Số lượng món thống kê (đã sắp xếp): " + foodList.size());

        phFoodOderedAdapter adapter = new phFoodOderedAdapter(foodList);
        recyclerView.setAdapter(adapter);

        if (foodList.isEmpty()) {
            Toast.makeText(context, "Không có dữ liệu thống kê!", Toast.LENGTH_SHORT).show();
        }
    }
}
