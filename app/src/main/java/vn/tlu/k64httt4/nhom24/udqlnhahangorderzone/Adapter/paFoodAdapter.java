package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.paFood;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

import java.util.List;

public class paFoodAdapter extends RecyclerView.Adapter<paFoodAdapter.FoodViewHolder> {

    private List<paFood> foodList;
    private OnItemClickListener listener;

    public paFoodAdapter(List<paFood> foodList, OnItemClickListener listener) {
        this.foodList = foodList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pa_item_mon_an, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        paFood food = foodList.get(position);
        if (food == null) return;

        holder.nameTextView.setText(food.getName() != null ? food.getName() : "");
        holder.idTextView.setText(food.getId() != null ? "#" + food.getId() : "");
        holder.priceTextView.setText(String.format("$%d", food.getPrice()));

        Glide.with(holder.itemView.getContext())
                .load(food.getImageUrl() != null ? food.getImageUrl() : "")
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder.foodImageView);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(food);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList != null ? foodList.size() : 0;
    }

    static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView foodImageView, additionalImageView;
        TextView nameTextView, idTextView, priceTextView;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImageView = itemView.findViewById(R.id.imgFood);
            additionalImageView = itemView.findViewById(R.id.icAddCart);
            nameTextView = itemView.findViewById(R.id.txtName);
            idTextView = itemView.findViewById(R.id.txtID);
            priceTextView = itemView.findViewById(R.id.txtGia);

            // Kiểm tra ánh xạ
            if (foodImageView == null || additionalImageView == null ||
                    nameTextView == null || idTextView == null || priceTextView == null) {
                // Log lỗi nếu cần
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(paFood food);
    }
}