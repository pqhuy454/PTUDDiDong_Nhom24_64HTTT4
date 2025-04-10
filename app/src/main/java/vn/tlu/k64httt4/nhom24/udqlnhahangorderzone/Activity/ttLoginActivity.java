package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper.ttLoginHelper;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

public class ttLoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView tvSignup, tvForgotPassword;
    private ttLoginHelper loginHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tt_activity_login);

        // Khởi tạo LoginHelper
        loginHelper = new ttLoginHelper(this);

        // Ánh xạ các view từ XML
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // Sự kiện nhấn nút Đăng nhập
        btnLogin.setOnClickListener(v -> loginUser());

        // Sự kiện nhấn TextView "Đăng ký ngay"
        tvSignup.setOnClickListener(v -> {
            Intent intent = new Intent(ttLoginActivity.this, ttSignupActivity.class);
            startActivity(intent);
            finish();
        });

        // Sự kiện nhấn TextView "Quên mật khẩu"
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(ttLoginActivity.this, ttForgotpassswordActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loginUser() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào
        if (email.isEmpty() || password.isEmpty()) {
            showErrorDialog("Vui lòng điền đầy đủ email và mật khẩu");
            return;
        }

        // Kiểm tra định dạng email
        if (!isValidEmail(email)) {
            showErrorDialog("Email không đúng định dạng");
            return;
        }

        // Gọi LoginHelper để đăng nhập
        loginHelper.loginUser(email, password, new ttLoginHelper.OnUserInfoRetrievedListener() {
            @Override
            public void onUserInfoRetrieved(Map<String, Object> userInfo) {
                // Đăng nhập thành công, chuyển hướng sang phMainActivity
                Intent intent = new Intent(ttLoginActivity.this, phMainActivity.class);
                intent.putExtra("userInfo", new HashMap<>(userInfo));
                startActivity(intent);
                finish();
            }
        });
    }

    // Phương thức kiểm tra định dạng email
    private boolean isValidEmail(String email) {
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(emailPattern, email);
    }

    // Phương thức hiển thị AlertDialog với nút OK
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Lỗi")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }
}