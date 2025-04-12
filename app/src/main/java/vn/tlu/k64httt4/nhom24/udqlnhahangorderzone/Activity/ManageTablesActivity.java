package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.Table;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Adapter.ltTableAdapter;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper.TableHelper;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

import java.util.ArrayList;
import java.util.List;

public class ManageTablesActivity extends AppCompatActivity implements ltTableAdapter.OnTableClickListener {
    private RecyclerView recyclerViewTables;
    private ImageButton btnAddTable;
    private Button backBtn;
    private ltTableAdapter tableAdapter;
    private List<Table> tableList;
    private TableHelper tableHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lt_activity_manage_tables);

        recyclerViewTables = findViewById(R.id.recyclerViewTables);
        btnAddTable = findViewById(R.id.btnAddTable);
        backBtn = findViewById(R.id.backBtn);

        tableList = new ArrayList<>();
        tableAdapter = new ltTableAdapter(tableList, this);
        recyclerViewTables.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewTables.setAdapter(tableAdapter);

        tableHelper = new TableHelper();
        loadTables();

        btnAddTable.setOnClickListener(v -> showAddTableDialog());

        backBtn.setOnClickListener(v -> finish());
    }

    private void loadTables() {
        tableHelper.getAllTables(new TableHelper.OnTablesLoadedListener() {
            @Override
            public void onTablesLoaded(List<Table> tables) {
                tableList.clear();
                tableList.addAll(tables);
                tableAdapter.updateList(tableList);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ManageTablesActivity.this, "Lỗi tải danh sách bàn: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddTableDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.lt_dialog_table);
        dialog.setCancelable(false);

        TextView tvDialogTitle = dialog.findViewById(R.id.tvDialogTitle);
        EditText etTableId = dialog.findViewById(R.id.etTableId);
        EditText etTableName = dialog.findViewById(R.id.etTableName);
        EditText etSeats = dialog.findViewById(R.id.etSeats);
        Button btnSave = dialog.findViewById(R.id.btnSave);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnDelete = dialog.findViewById(R.id.btnDelete);

        tvDialogTitle.setText("THÊM BÀN");
        btnDelete.setVisibility(View.GONE);

        btnSave.setOnClickListener(v -> {
            String tableId = etTableId.getText().toString().trim();
            String tableName = etTableName.getText().toString().trim();
            String seatsStr = etSeats.getText().toString().trim();

            if (tableId.isEmpty() || tableName.isEmpty() || seatsStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            int seats;
            try {
                seats = Integer.parseInt(seatsStr);
                if (seats <= 0) {
                    Toast.makeText(this, "Số ghế phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Số ghế không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            Table table = new Table();
            table.setId(tableId);
            table.setName(tableName);
            table.setSeats(seats);
            table.setStatus("Trống");
            table.setCurrentOrderId("");

            tableHelper.addTable(table,
                    () -> {
                        Toast.makeText(this, "Thêm bàn thành công", Toast.LENGTH_SHORT).show();
                        loadTables();
                        dialog.dismiss();
                    },
                    error -> Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_SHORT).show());
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void onTableClick(Table table) {
        showEditTableDialog(table);
    }

    private void showEditTableDialog(Table table) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.lt_dialog_table);
        dialog.setCancelable(false);

        TextView tvDialogTitle = dialog.findViewById(R.id.tvDialogTitle);
        EditText etTableId = dialog.findViewById(R.id.etTableId);
        EditText etTableName = dialog.findViewById(R.id.etTableName);
        EditText etSeats = dialog.findViewById(R.id.etSeats);
        Button btnSave = dialog.findViewById(R.id.btnSave);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnDelete = dialog.findViewById(R.id.btnDelete);

        tvDialogTitle.setText("SỬA BÀN");
        etTableId.setText(table.getId());
        etTableId.setEnabled(false); // Không cho sửa ID
        etTableName.setText(table.getName());
        etSeats.setText(String.valueOf(table.getSeats()));
        btnDelete.setVisibility(View.VISIBLE);

        btnSave.setOnClickListener(v -> {
            String tableName = etTableName.getText().toString().trim();
            String seatsStr = etSeats.getText().toString().trim();

            if (tableName.isEmpty() || seatsStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            int seats;
            try {
                seats = Integer.parseInt(seatsStr);
                if (seats <= 0) {
                    Toast.makeText(this, "Số ghế phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Số ghế không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            table.setName(tableName);
            table.setSeats(seats);
            // Giữ nguyên status và currentOrderId

            tableHelper.updateTable(table,
                    () -> {
                        Toast.makeText(this, "Sửa bàn thành công", Toast.LENGTH_SHORT).show();
                        loadTables();
                        dialog.dismiss();
                    },
                    error -> Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_SHORT).show());
        });

        btnDelete.setOnClickListener(v -> {
            tableHelper.deleteTable(table.getId(),
                    () -> {
                        Toast.makeText(this, "Xóa bàn thành công", Toast.LENGTH_SHORT).show();
                        loadTables();
                        dialog.dismiss();
                    },
                    error -> Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_SHORT).show());
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}