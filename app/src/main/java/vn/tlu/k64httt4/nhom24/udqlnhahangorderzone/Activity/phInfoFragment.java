package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

public class phInfoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ph_fragment_info, container, false);

        // Ánh xạ các TextView
        TextView textViewFullName = view.findViewById(R.id.textViewFullNameDetail);
        TextView textViewEmail = view.findViewById(R.id.textViewEmail);
        TextView textViewPhone = view.findViewById(R.id.textViewPhone);
        TextView textViewRole = view.findViewById(R.id.textViewRole);
        Button btnSignout = view.findViewById(R.id.btnSignout); // Gán nút đăng xuất

        // Lấy thông tin từ Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            Map<String, Object> userInfo = (Map<String, Object>) bundle.getSerializable("userInfo");
            if (userInfo != null) {
                textViewFullName.setText((String) userInfo.get("name"));
                textViewEmail.setText((String) userInfo.get("email"));
                textViewPhone.setText((String) userInfo.get("phone"));
                textViewRole.setText((String) userInfo.get("role"));
            }
        }

        // Xử lý đăng xuất
        btnSignout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut(); // Đăng xuất Firebase

            // Trở về màn hình đăng nhập
            Intent intent = new Intent(getActivity(), ttLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa ngăn xếp
            startActivity(intent);
        });

        return view;
    }
}
