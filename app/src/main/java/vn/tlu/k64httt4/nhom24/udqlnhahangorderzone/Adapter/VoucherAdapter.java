package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper.VoucherHelper;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.Voucher;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

import java.util.List;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {
    private List<Voucher> voucherList;
    private VoucherHelper voucherHelper;
    private Activity activity; // Thêm biến Activity

    public VoucherAdapter(List<Voucher> voucherList, Activity activity) {
        this.voucherList = voucherList;
        this.voucherHelper = new VoucherHelper();
        this.activity = activity; // Khởi tạo Activity
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lt_item_voucher, parent, false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        Voucher voucher = voucherList.get(position);
        holder.voucherName.setText(voucher.getName());
        holder.voucherPrice.setText(String.format("$%.2f $%.2f", voucher.getOriginalPrice(), voucher.getDiscountedPrice()));
        holder.voucherDate.setText(voucher.getDayStart() + " - " + voucher.getDayEnd());
        holder.voucherId.setText("#" + voucher.getId());

        holder.deleteBtn.setOnClickListener(v -> {
            voucherHelper.deleteVoucher(voucher.getId(),
                    () -> {
                        // Xóa thành công, cập nhật danh sách
                        voucherList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, voucherList.size());
                    },
                    error -> activity.runOnUiThread(() -> // Sử dụng activity để chạy trên UI thread
                            Toast.makeText(activity, "Lỗi khi xóa: " + error, Toast.LENGTH_SHORT).show()));
        });
    }

    @Override
    public int getItemCount() {
        return voucherList.size();
    }

    static class VoucherViewHolder extends RecyclerView.ViewHolder {
        TextView voucherName, voucherPrice, voucherDate, voucherId;
        Button deleteBtn;

        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            voucherName = itemView.findViewById(R.id.voucherName);
            voucherPrice = itemView.findViewById(R.id.voucherPrice);
            voucherDate = itemView.findViewById(R.id.voucherDate);
            voucherId = itemView.findViewById(R.id.voucherId);
            deleteBtn = itemView.findViewById(R.id.deleteVoucherBtn);
        }
    }
}