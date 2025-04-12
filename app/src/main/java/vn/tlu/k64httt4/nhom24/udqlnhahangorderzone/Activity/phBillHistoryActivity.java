package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper.phBillHistoryHelper;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

public class phBillHistoryActivity extends AppCompatActivity {

    private RecyclerView rvBillHistory;
    private ImageView btnBack;
    private phBillHistoryHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ph_activity_bill_history);

        rvBillHistory = findViewById(R.id.rvBillHistory);
        btnBack = findViewById(R.id.btnBack);
        helper = new phBillHistoryHelper(this);

        rvBillHistory.setLayoutManager(new LinearLayoutManager(this));
        helper.loadBillHistory(rvBillHistory);

        btnBack.setOnClickListener(v -> finish());
    }
}
