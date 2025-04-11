package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

public class ttPersonnelManagementAdapter extends RecyclerView.Adapter<ttPersonnelManagementAdapter.EmailViewHolder> {

    private final List<String> emailList;
    private final OnEmailActionListener listener;

    public interface OnEmailActionListener {
        void onDelete(String email, int position);
    }

    public ttPersonnelManagementAdapter(List<String> emailList, OnEmailActionListener listener) {
        this.emailList = emailList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EmailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tt_item_email, parent, false);
        return new EmailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmailViewHolder holder, int position) {
        String email = emailList.get(position);
        holder.tvEmail.setText(email);
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(email, position));
    }

    @Override
    public int getItemCount() {
        return emailList.size();
    }

    public static class EmailViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmail;
        ImageView btnDelete;

        public EmailViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public void removeItem(int position) {
        emailList.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(String email) {
        emailList.add(email);
        notifyItemInserted(emailList.size() - 1);
    }

    public void setData(List<String> emails) {
        emailList.clear();
        emailList.addAll(emails);
        notifyDataSetChanged();
    }
}
