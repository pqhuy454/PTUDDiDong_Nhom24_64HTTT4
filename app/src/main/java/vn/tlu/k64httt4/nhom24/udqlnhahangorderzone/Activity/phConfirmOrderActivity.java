package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Adapter.phOrderAdapter;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper.phConfirmOrderHelper;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.paFood;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

public class phConfirmOrderActivity extends AppCompatActivity {

    private RecyclerView recyclerFoods;
    private ImageView btnBack;
    private Button btnConfirm;

    private ArrayList<paFood> foodList;
    private ArrayList<Integer> quantities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ph_activity_confirm_order);

        recyclerFoods = findViewById(R.id.recyclerFoods);
        btnBack = findViewById(R.id.btnBack);
        btnConfirm = findViewById(R.id.btnConfirm);

        recyclerFoods.setLayoutManager(new LinearLayoutManager(this));

        foodList = (ArrayList<paFood>) getIntent().getSerializableExtra("foodList");
        quantities = (ArrayList<Integer>) getIntent().getSerializableExtra("quantities");

        phOrderAdapter adapter = new phOrderAdapter(foodList, quantities);
        recyclerFoods.setAdapter(adapter);

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, phOrderTableActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnConfirm.setOnClickListener(v -> {
            String tableId = getIntent().getStringExtra("tableId");
            String tableName = getIntent().getStringExtra("tableName");
            String currentOrderId = getIntent().getStringExtra("currentOrderId"); // ✅

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String email = (user != null) ? user.getEmail() : "Không xác định";

            phConfirmOrderHelper helper = new phConfirmOrderHelper();
            helper.confirmOrder(tableId, tableName, foodList, quantities, email, currentOrderId, new phConfirmOrderHelper.OnConfirmListener() {
                @Override
                public void onSuccess(String billId) {
                    Intent intent = new Intent(phConfirmOrderActivity.this, phOrderedActivity.class);
                    intent.putExtra("tableId", tableId);
                    intent.putExtra("billId", billId);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(phConfirmOrderActivity.this, "Lỗi khi xác nhận đơn", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }
}
