package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

public class phManageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ph_fragment_manage, container, false);

        Button btnManageStaff = view.findViewById(R.id.btnManageStaff);
        Button btnQuanLyThucDon = view.findViewById(R.id.btnQuanLyThucDon);

        // Kiểm tra ánh xạ
        if (btnManageStaff == null || btnQuanLyThucDon == null) {
            Toast.makeText(getContext(), "Lỗi ánh xạ giao diện", Toast.LENGTH_LONG).show();
            return view;
        }

        btnManageStaff.setOnClickListener(v -> {
            if (getActivity() == null) {
                Toast.makeText(getContext(), "Lỗi context", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(getActivity(), ttPersonnelManagementActivity.class);
            startActivity(intent);
        });

        btnQuanLyThucDon.setOnClickListener(v -> {
            if (getActivity() == null) {
                Toast.makeText(getContext(), "Lỗi context", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(getActivity(), paQuanLyMenuActivity.class);
            startActivity(intent);
        });

        Button btnManageTables = view.findViewById(R.id.btnManageTables);
        btnManageTables.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ManageTablesActivity.class);
            startActivity(intent);
        });

        Button btnManageVoucher = view.findViewById(R.id.btnManageVouchers);
        btnManageVoucher.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), VoucherActivity.class);
            startActivity(intent);
        });


        return view;
    }
}