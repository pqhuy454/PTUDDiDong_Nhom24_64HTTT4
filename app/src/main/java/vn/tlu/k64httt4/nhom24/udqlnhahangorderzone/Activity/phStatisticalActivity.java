package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.Locale;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper.phStatisticalHelper;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

public class phStatisticalActivity extends AppCompatActivity {

    private EditText etDate, etMonth;
    private Button btnFilterDate, btnFilterMonth;
    private TextView tvTotalBills, tvTotalRevenue, tvAverageRevenue;
    private RecyclerView rvFoodStats;
    private phStatisticalHelper helper;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ph_activity_statistical);

        // Ánh xạ View
        btnBack = findViewById(R.id.btnBack);
        etDate = findViewById(R.id.etDate);
        etMonth = findViewById(R.id.etMonth);
        btnFilterDate = findViewById(R.id.btnFilterDate);
        btnFilterMonth = findViewById(R.id.btnFilterMonth);
        tvTotalBills = findViewById(R.id.tvTotalBills);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvAverageRevenue = findViewById(R.id.tvAverageRevenue);
        rvFoodStats = findViewById(R.id.rvFoodStats);
        rvFoodStats.setLayoutManager(new LinearLayoutManager(this));

        helper = new phStatisticalHelper(this);

        // Nút back
        btnBack.setOnClickListener(v -> finish());

        // Chọn ngày
        etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (view, y, m, d) -> {
                        // Sửa: đảm bảo định dạng dd/MM/yyyy
                        String formatted = String.format(Locale.getDefault(), "%02d/%02d/%d", d, m + 1, y);
                        etDate.setText(formatted);
                    },
                    year, month, day
            );
            dialog.show();
        });

        // Chọn tháng
        etMonth.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (view, y, m, d) -> {
                        // Sửa: đảm bảo định dạng MM/yyyy
                        String formatted = String.format(Locale.getDefault(), "%02d/%d", m + 1, y);
                        etMonth.setText(formatted);
                    },
                    year, month, 1
            );

            int dayId = getResources().getIdentifier("day", "id", "android");
            View dayView = dialog.getDatePicker().findViewById(dayId);
            if (dayView != null) {
                dayView.setVisibility(View.GONE);
            }

            dialog.show();
        });

        // Thống kê theo ngày
        btnFilterDate.setOnClickListener(v -> {
            String selectedDate = etDate.getText().toString().trim();
            if (!selectedDate.isEmpty()) {
                helper.getStatsByDate(selectedDate, tvTotalBills, tvTotalRevenue, tvAverageRevenue, rvFoodStats);
            } else {
                Toast.makeText(this, "Vui lòng chọn ngày!", Toast.LENGTH_SHORT).show();
            }
        });

        // Thống kê theo tháng
        btnFilterMonth.setOnClickListener(v -> {
            String selectedMonth = etMonth.getText().toString().trim();
            if (!selectedMonth.isEmpty()) {
                helper.getStatsByMonth(selectedMonth, tvTotalBills, tvTotalRevenue, tvAverageRevenue, rvFoodStats);
            } else {
                Toast.makeText(this, "Vui lòng chọn tháng!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
