package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper.ttSignupHelper;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

public class ttSignupActivity extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPhone, edtPassword, edtConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private ttSignupHelper signupHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tt_activity_signup);

        // Khởi tạo RegisterHelper
        signupHelper = new ttSignupHelper(this);

        // Ánh xạ các view từ XML
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        // Sự kiện nhấn nút Đăng ký
        btnRegister.setOnClickListener(v -> signupUser());

        // Sự kiện nhấn TextView "Đăng nhập ngay"
        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ttSignupActivity.this, ttLoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void signupUser() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();
        String role = "Phục vụ";  // Set default role to "Phục vụ"

        // Kiểm tra dữ liệu đầu vào
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showErrorDialog("Vui lòng điền đầy đủ thông tin");
            return;
        }

        // Kiểm tra định dạng email
        if (!isValidEmail(email)) {
            showErrorDialog("Email không đúng định dạng");
            return;
        }

        // Kiểm tra định dạng số điện thoại
        if (!isValidPhoneNumber(phone)) {
            showErrorDialog("Số điện thoại không đúng định dạng");
            return;
        }

        // Kiểm tra mật khẩu có khớp không
        if (!password.equals(confirmPassword)) {
            showErrorDialog("Mật khẩu không khớp");
            return;
        }

        // Kiểm tra độ dài mật khẩu
        if (password.length() < 6) {
            showErrorDialog("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }

        // Gọi RegisterHelper để đăng ký và lưu dữ liệu
        signupHelper.signupAndSaveUser(email, password, name, phone, role);
    }

    // Phương thức kiểm tra định dạng email
    private boolean isValidEmail(String email) {
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(emailPattern, email);
    }

    // Phương thức kiểm tra định dạng số điện thoại
    private boolean isValidPhoneNumber(String phone) {
        String phonePattern = "^0[0-9]{9}$";
        return Pattern.matches(phonePattern, phone);
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