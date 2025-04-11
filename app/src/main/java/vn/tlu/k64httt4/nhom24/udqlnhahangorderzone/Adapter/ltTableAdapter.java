package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.Table;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

import java.util.List;

public class ltTableAdapter extends RecyclerView.Adapter<ltTableAdapter.TableViewHolder> {
    private List<Table> tableList;
    private OnTableClickListener listener;

    public interface OnTableClickListener {
        void onTableClick(Table table);
    }

    public ltTableAdapter(List<Table> tableList, OnTableClickListener listener) {
        this.tableList = tableList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lt_item_table, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        Table table = tableList.get(position);
        holder.tvTableNumber.setText(table.getName());
        holder.tvCapacity.setText(String.valueOf(table.getSeats()));
        holder.itemView.setOnClickListener(v -> listener.onTableClick(table));
    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }

    public void updateList(List<Table> newList) {
        this.tableList = newList;
        notifyDataSetChanged();
    }

    static class TableViewHolder extends RecyclerView.ViewHolder {
        TextView tvTableNumber, tvCapacity;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTableNumber = itemView.findViewById(R.id.tvTableNumber);
            tvCapacity = itemView.findViewById(R.id.tvCapacity);
        }
    }
}