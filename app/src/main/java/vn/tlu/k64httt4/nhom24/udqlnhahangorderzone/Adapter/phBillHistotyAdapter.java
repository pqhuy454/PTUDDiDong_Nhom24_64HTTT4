package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Activity.phBillDetailActivity;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

public class phBillHistotyAdapter extends RecyclerView.Adapter<phBillHistotyAdapter.BillViewHolder> {

    private Context context;
    private List<Map<String, Object>> billList;

    public phBillHistotyAdapter(Context context, List<Map<String, Object>> billList) {
        this.context = context;
        this.billList = billList;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ph_item_bill_history, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        Map<String, Object> bill = billList.get(position);

        String billId = (String) bill.get("billId");
        String tableName = (String) bill.get("tableName");
        Double total = bill.get("totalPrice") != null ? (Double) bill.get("totalPrice") : 0;
        Date date = (Date) bill.get("timestamp");

        String dateStr = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(date);

        holder.tvBillId.setText("Mã hóa đơn: " + billId);
        holder.tvTableName.setText("Bàn: " + tableName);
        holder.tvTotalPrice.setText("Tổng tiền: " + String.format("%,.0f", total) + "đ");
        holder.tvDate.setText("Ngày: " + dateStr);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, phBillDetailActivity.class);
            intent.putExtra("bill", (java.io.Serializable) bill);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return billList.size();
    }

    public static class BillViewHolder extends RecyclerView.ViewHolder {
        TextView tvBillId, tvTableName, tvDate, tvTotalPrice;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBillId = itemView.findViewById(R.id.tvBillId);
            tvTableName = itemView.findViewById(R.id.tvTableName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
        }
    }
}
