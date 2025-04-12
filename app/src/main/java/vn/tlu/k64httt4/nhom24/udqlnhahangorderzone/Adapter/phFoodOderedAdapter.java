package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Adapter;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

public class phFoodOderedAdapter extends RecyclerView.Adapter<phFoodOderedAdapter.StatViewHolder> {

    private List<Pair<String, Integer>> foodStats;

    public phFoodOderedAdapter(List<Pair<String, Integer>> foodStats) {
        this.foodStats = foodStats;
    }

    @NonNull
    @Override
    public StatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ph_item_statistical, parent, false);
        return new StatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatViewHolder holder, int position) {
        Pair<String, Integer> item = foodStats.get(position);
        holder.tvFoodName.setText(item.first);
        holder.tvQuantity.setText("Số lượng: " + item.second);
    }

    @Override
    public int getItemCount() {
        return foodStats.size();
    }

    public static class StatViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodName, tvQuantity;

        public StatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }
    }
}
