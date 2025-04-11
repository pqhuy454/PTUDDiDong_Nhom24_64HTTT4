package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper.paUserHelper;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.paNotice;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

import java.util.List;

public class paNoticeAdapter extends RecyclerView.Adapter<paNoticeAdapter.NoticeViewHolder> {
    private List<paNotice> notices;
    private paUserHelper userHelper;

    public paNoticeAdapter(List<paNotice> notices, Context context) {
        this.notices = notices;
        this.userHelper = new paUserHelper(context); // Khởi tạo paUserHelper
    }

    @Override
    public NoticeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pa_item_noti, parent, false);
        return new NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoticeViewHolder holder, int position) {
        paNotice notice = notices.get(position);
        holder.txtTieuDe.setText(notice.getTitle());
        holder.txtNoiDung.setText(notice.getContent());

        // Lấy email từ senderId
        String senderId = notice.getSenderId() != null ? notice.getSenderId() : "Anonymous";
        userHelper.getUserEmail(senderId, new paUserHelper.OnUserLoadedListener() {
            @Override
            public void onLoaded(String email) {
                holder.txtTenNguoiGui.setText(email); // Hiển thị email
            }

            @Override
            public void onError(String errorMessage) {
                holder.txtTenNguoiGui.setText("Không xác định"); // Hiển thị mặc định nếu lỗi
            }
        });
    }

    @Override
    public int getItemCount() {
        return notices != null ? notices.size() : 0;
    }

    public void updateNotices(List<paNotice> newNotices) {
        this.notices = newNotices;
        notifyDataSetChanged();
    }

    static class NoticeViewHolder extends RecyclerView.ViewHolder {
        TextView txtTenNguoiGui, txtTieuDe, txtNoiDung;

        NoticeViewHolder(View itemView) {
            super(itemView);
            txtTenNguoiGui = itemView.findViewById(R.id.txtTenNguoiGui);
            txtTieuDe = itemView.findViewById(R.id.txtTieuDe);
            txtNoiDung = itemView.findViewById(R.id.txtNoiDung);
        }
    }
}