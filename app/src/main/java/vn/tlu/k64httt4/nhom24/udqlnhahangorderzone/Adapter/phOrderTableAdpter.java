package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.Table;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

public class phOrderTableAdpter extends RecyclerView.Adapter<phOrderTableAdpter.TableViewHolder> {

    private List<Table> tableList;
    private OnTableClickListener listener;

    public interface OnTableClickListener {
        void onTableClick(Table table);
    }

    public phOrderTableAdpter(List<Table> tableList, OnTableClickListener listener) {
        this.tableList = tableList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ph_item_table, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        Table table = tableList.get(position);
        holder.tvName.setText("Tên bàn: " + table.getName());
        holder.tvSeats.setText("Số chỗ: " + table.getSeats());
        holder.tvStatus.setText("Trạng thái: " + table.getStatus());

        // Đổi nền nếu Có khách
        if ("Có khách".equalsIgnoreCase(table.getStatus())) {
            holder.itemView.setBackgroundResource(R.drawable.background_table);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.button_border);
        }

        // Gán sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTableClick(table);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tableList != null ? tableList.size() : 0;
    }

    public static class TableViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSeats, tvStatus;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvTableName);
            tvSeats = itemView.findViewById(R.id.tvTableSeats);
            tvStatus = itemView.findViewById(R.id.tvTableStatus);
        }
    }
}
