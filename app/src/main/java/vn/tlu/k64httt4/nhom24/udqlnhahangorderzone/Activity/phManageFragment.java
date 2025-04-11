package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

public class phManageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ph_fragment_manage, container, false);

        Button btnManageStaff = view.findViewById(R.id.btnManageStaff);
        btnManageStaff.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ttPersonnelManagementActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
