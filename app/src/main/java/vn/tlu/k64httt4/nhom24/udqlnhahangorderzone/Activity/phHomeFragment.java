package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.Map;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper.ttPersonnelManagementHelper;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

public class phHomeFragment extends Fragment {

    private Button btnOrder, btnPay;
    private Map<String, Object> userInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ph_fragment_home, container, false);

        btnOrder = view.findViewById(R.id.btnOrder);
        btnPay = view.findViewById(R.id.btnPay);

        // Ẩn toàn bộ ngay từ đầu
        btnOrder.setVisibility(View.GONE);
        btnPay.setVisibility(View.GONE);

        // Gán sự kiện click cho btnOrder
//        btnOrder.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), phOrderingTableActivity.class);
//            startActivity(intent);
//        });

        // Lấy userInfo từ MainActivity
        if (getActivity() instanceof phMainActivity) {
            userInfo = ((phMainActivity) getActivity()).getUserInfo();
        }

        if (userInfo == null) return view;

        String role = (String) userInfo.get("role");
        String email = (String) userInfo.get("email");

        if ("Quản lý".equals(role)) {
            // Quản lý → hiện tất cả
            btnOrder.setVisibility(View.VISIBLE);
            btnPay.setVisibility(View.VISIBLE);
        } else if ("Phục vụ".equals(role)) {
            // Phục vụ → kiểm tra có trong staff không
            ttPersonnelManagementHelper helper = new ttPersonnelManagementHelper(requireContext());
            helper.loadStaffEmails(new ttPersonnelManagementHelper.OnEmailListLoadedListener() {
                @Override
                public void onLoaded(List<String> emailList) {
                    if (emailList.contains(email)) {
                        btnOrder.setVisibility(View.VISIBLE);
                        btnPay.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    // Không hiện gì nếu lỗi xảy ra
                }
            });
        }

        return view;
    }
}
