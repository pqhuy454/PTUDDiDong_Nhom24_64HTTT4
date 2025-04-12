package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.paFood;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

public class phOrderAdapter extends RecyclerView.Adapter<phOrderAdapter.FoodViewHolder> {

    private List<paFood> foodList;
    private HashMap<String, Integer> quantityMap = new HashMap<>();
    private boolean isReadOnly = false;

    // Dùng trong màn chọn món (cho bấm +/-)
    public phOrderAdapter(List<paFood> foodList) {
        this.foodList = foodList;
        for (paFood food : foodList) {
            quantityMap.put(food.getId(), 0);
        }
    }

    // Dùng trong màn xác nhận (chỉ hiển thị, không thao tác)
    public phOrderAdapter(List<paFood> foodList, List<Integer> quantities) {
        this.foodList = foodList;
        for (int i = 0; i < foodList.size(); i++) {
            quantityMap.put(foodList.get(i).getId(), quantities.get(i));
        }
        isReadOnly = true;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ph_item_food_ordered, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        paFood food = foodList.get(position);
        int quantity = quantityMap.get(food.getId());

        holder.tvFoodName.setText(food.getName());
        holder.tvQuantity.setText(String.valueOf(quantity));

        String priceFormatted = NumberFormat.getNumberInstance(Locale.getDefault()).format(food.getPrice()) + "đ";
        holder.tvFoodPrice.setText("Giá: " + priceFormatted);

        // TODO: Glide/Picasso nếu cần load ảnh
        // Glide.with(holder.itemView.getContext()).load(food.getImageUrl()).into(holder.imgFood);

        if (isReadOnly) {
            holder.btnPlus.setVisibility(View.GONE);
            holder.btnMinus.setVisibility(View.GONE);
        } else {
            holder.btnPlus.setVisibility(View.VISIBLE);
            holder.btnMinus.setVisibility(View.VISIBLE);

            holder.btnPlus.setOnClickListener(v -> {
                int current = quantityMap.get(food.getId());
                quantityMap.put(food.getId(), current + 1);
                notifyItemChanged(position);
            });

            holder.btnMinus.setOnClickListener(v -> {
                int current = quantityMap.get(food.getId());
                if (current > 0) {
                    quantityMap.put(food.getId(), current - 1);
                    notifyItemChanged(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return foodList != null ? foodList.size() : 0;
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodName, tvFoodPrice, tvQuantity;
        ImageView btnPlus, btnMinus, imgFood;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvFoodPrice = itemView.findViewById(R.id.tvFoodPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            imgFood = itemView.findViewById(R.id.imgFood);
        }
    }

    public HashMap<String, Integer> getQuantityMap() {
        return quantityMap;
    }

    public List<paFood> getFoodList() {
        return foodList;
    }
}
