package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Activity.paPaymentActivity;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.paBill;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class paOngoingAdapter extends RecyclerView.Adapter<paOngoingAdapter.BillViewHolder> {
    private Context context;
    private List<paBill> billList;

    public paOngoingAdapter(Context context, List<paBill> billList) {
        this.context = context;
        this.billList = billList;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pa_item_ongoing, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        paBill bill = billList.get(position);

        holder.tableName.setText(bill.getTableName());
        holder.orderNumber.setText("#" + bill.getId());
        holder.price.setText(NumberFormat.getNumberInstance(Locale.getDefault()).format(bill.getTotalPrice()) + "đ");
        holder.date.setText(bill.getTimestamp());
        holder.countItem.setText(bill.getQuantities().size() + " items");

        holder.btnThanhToan.setOnClickListener(v -> {
            Intent intent = new Intent(context, paPaymentActivity.class);
            intent.putExtra("billId", bill.getId());
            intent.putExtra("tableId", bill.getTableId());
            intent.putExtra("tableName", bill.getTableName());
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return billList != null ? billList.size() : 0;
    }

    public static class BillViewHolder extends RecyclerView.ViewHolder {
        TextView tableName, orderNumber, price, date, countItem;
        View btnThanhToan;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            tableName = itemView.findViewById(R.id.table_name);
            orderNumber = itemView.findViewById(R.id.order_number);
            price = itemView.findViewById(R.id.price);
            date = itemView.findViewById(R.id.date);
            countItem = itemView.findViewById(R.id.count_item);
            btnThanhToan = itemView.findViewById(R.id.btnThanhtoan);
        }
    }
}