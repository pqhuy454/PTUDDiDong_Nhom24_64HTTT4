package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.paFood;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class paPayAdapter extends RecyclerView.Adapter<paPayAdapter.FoodViewHolder> {

    private List<paFood> foodList;
    private HashMap<String, Integer> quantityMap = new HashMap<>();
    private boolean isReadOnly = false;

    public paPayAdapter(List<paFood> foodList) {
        this.foodList = foodList != null ? foodList : new ArrayList<>();
        for (paFood food : this.foodList) {
            quantityMap.put(food.getId(), 0);
        }
    }

    public paPayAdapter(List<paFood> foodList, List<Integer> quantities) {
        this.foodList = foodList != null ? foodList : new ArrayList<>();
        this.quantityMap = new HashMap<>();
        if (quantities != null && foodList != null) {
            for (int i = 0; i < Math.min(foodList.size(), quantities.size()); i++) {
                quantityMap.put(foodList.get(i).getId(), quantities.get(i));
            }
        }
        isReadOnly = true;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pa_item_pay, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        paFood food = foodList.get(position);
        int quantity = quantityMap.getOrDefault(food.getId(), 0);

        Log.d("phOrderAdapter", "Food ID: " + food.getId() + ", Name: " + food.getName() + ", Quantity: " + quantity);

        holder.txtSoLuong.setText(String.valueOf(quantity));
        holder.itemName.setText(food.getName() != null ? food.getName() : "Không xác định");

        String priceFormatted = NumberFormat.getNumberInstance(Locale.getDefault()).format(food.getPrice()) + " VNĐ";
        holder.price.setText(priceFormatted);
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView txtSoLuong, itemName, price;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSoLuong = itemView.findViewById(R.id.txtSoLuong);
            itemName = itemView.findViewById(R.id.item_name);
            price = itemView.findViewById(R.id.price);
        }
    }

    public HashMap<String, Integer> getQuantityMap() {
        return quantityMap;
    }

    public List<paFood> getFoodList() {
        return foodList;
    }
}