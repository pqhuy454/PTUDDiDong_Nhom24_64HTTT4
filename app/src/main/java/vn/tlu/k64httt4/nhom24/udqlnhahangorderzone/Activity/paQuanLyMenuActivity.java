package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Adapter.paFoodAdapter;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper.paMenuHelper;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.paFood;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

import java.util.ArrayList;
import java.util.List;

public class paQuanLyMenuActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMenu;
    private ImageView imgThemMonAn;
    private ImageView btnBack; // Thêm biến cho nút back
    private paFoodAdapter adapter;
    private List<paFood> foodList;
    private paMenuHelper firestoreHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pa_act_quan_li_menu);

        // Ánh xạ các view
        recyclerViewMenu = findViewById(R.id.recyclerViewMenu);
        imgThemMonAn = findViewById(R.id.imgThemMonAn);
        btnBack = findViewById(R.id.btnback); // Ánh xạ nút back

        // Kiểm tra ánh xạ
        if (recyclerViewMenu == null || imgThemMonAn == null || btnBack == null) {
            Toast.makeText(this, "Lỗi ánh xạ view", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Khởi tạo FirestoreHelper
        firestoreHelper = new paMenuHelper();

        // Thiết lập RecyclerView
        foodList = new ArrayList<>();
        adapter = new paFoodAdapter(foodList, this::goToEditFoodActivity);
        recyclerViewMenu.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewMenu.setAdapter(adapter);

        // Tải danh sách món ăn từ Firestore
        loadFoods();

        // Xử lý sự kiện nhấn nút Thêm Món Ăn
        imgThemMonAn.setOnClickListener(v -> {
            Intent intent = new Intent(paQuanLyMenuActivity.this, paThemMonAnActivity.class);
            startActivity(intent);
        });

        // Xử lý sự kiện nhấn nút Back
        btnBack.setOnClickListener(v -> finish()); // Quay lại màn hình trước đó
    }

    private void loadFoods() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_LONG).show();
            return;
        }
        firestoreHelper.getAllFoods(new paMenuHelper.OnFoodsLoadedListener() {
            @Override
            public void onFoodsLoaded(List<paFood> foods) {
                runOnUiThread(() -> {
                    foodList.clear();
                    foodList.addAll(foods);
                    adapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(paQuanLyMenuActivity.this, "Lỗi tải dữ liệu: " + error, Toast.LENGTH_LONG).show());
            }
        });
    }

    private void goToEditFoodActivity(paFood food) {
        Intent intent = new Intent(paQuanLyMenuActivity.this, paSuaXoaMonAnActivity.class);
        intent.putExtra("food", food);
        startActivity(intent);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFoods();
    }
}