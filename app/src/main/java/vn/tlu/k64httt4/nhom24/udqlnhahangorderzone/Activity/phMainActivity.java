package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

public class phMainActivity extends AppCompatActivity {

    private Map<String, Object> userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ph_activity_main);

        // Nhận thông tin người dùng từ Intent
        userInfo = (HashMap<String, Object>) getIntent().getSerializableExtra("userInfo");

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            String role = (String) userInfo.get("role");

            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new phHomeFragment();
            } else if (itemId == R.id.nav_manage) {
                if ("Quản lý".equals(role)) {
                    selectedFragment = new phManageFragment();
                } else {
                    showDialog("Bạn không có quyền truy cập chức năng này");
                    return false;
                }
            } else if (itemId == R.id.nav_notification) {
                selectedFragment = new phNotificationFragment();
            } else if (itemId == R.id.nav_info) {
                selectedFragment = new phInfoFragment();
                // Truyền thông tin người dùng vào phInfoFragment
                Bundle bundle = new Bundle();
                bundle.putSerializable("userInfo", new HashMap<>(userInfo));
                selectedFragment.setArguments(bundle);
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // Load default fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new phHomeFragment())
                .commit();
    }

    public Map<String, Object> getUserInfo() {
        return userInfo;
    }

    private void showDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Thông báo")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }
}