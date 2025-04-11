package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper.paMenuHelper;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.paFood;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class paSuaXoaMonAnActivity extends AppCompatActivity {

    private EditText etMaMonAn, etTenMonAn, etGia;
    private ImageView imageViewSelectPhotoMonAn;
    private Button btnUpdate, btnXoa, btnHuy;
    private paMenuHelper firestoreHelper;
    private paFood food;
    private Uri imageUri;
    private OkHttpClient client;

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    imageUri = uri;
                    imageViewSelectPhotoMonAn.setImageURI(imageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pa_act_suaxoa_mon_an);

        // Ánh xạ các view
        etMaMonAn = findViewById(R.id.etMaMonAn);
        etTenMonAn = findViewById(R.id.etTenMonAn);
        etGia = findViewById(R.id.etGia);
        imageViewSelectPhotoMonAn = findViewById(R.id.imageViewSelectPhotoMonAn);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnXoa = findViewById(R.id.btnXoa);
        btnHuy = findViewById(R.id.btnHuy);

        // Kiểm tra ánh xạ
        if (etMaMonAn == null || etTenMonAn == null || etGia == null ||
                imageViewSelectPhotoMonAn == null || btnUpdate == null ||
                btnXoa == null || btnHuy == null) {
            Toast.makeText(this, "Lỗi ánh xạ giao diện", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Khởi tạo FirestoreHelper và OkHttpClient
        firestoreHelper = new paMenuHelper();
        client = new OkHttpClient();

        // Lấy dữ liệu món ăn từ Intent
        food = (paFood) getIntent().getSerializableExtra("food");
        if (food == null) {
            Toast.makeText(this, "Không tìm thấy dữ liệu món ăn", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Gán dữ liệu vào view
        etMaMonAn.setText(food.getId() != null ? food.getId() : "");
        etTenMonAn.setText(food.getName() != null ? food.getName() : "");
        etGia.setText(String.valueOf(food.getPrice()));
        Glide.with(this)
                .load(food.getImageUrl() != null ? food.getImageUrl() : "")
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(imageViewSelectPhotoMonAn);

        // Sự kiện chọn ảnh
        imageViewSelectPhotoMonAn.setOnClickListener(v -> {
            String permissionToCheck = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                    ? Manifest.permission.READ_MEDIA_IMAGES
                    : Manifest.permission.READ_EXTERNAL_STORAGE;

            if (ContextCompat.checkSelfPermission(this, permissionToCheck) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Vui lòng cấp quyền truy cập bộ nhớ", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } else {
                pickImageLauncher.launch("image/*");
            }
        });

        // Sự kiện nút Cập nhật
        btnUpdate.setOnClickListener(v -> updateFood());

        // Sự kiện nút Xóa
        btnXoa.setOnClickListener(v -> deleteFood());

        // Sự kiện nút Hủy
        btnHuy.setOnClickListener(v -> finish());
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private String imageToBase64(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream == null) return null;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            byte[] imageBytes = outputStream.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            runOnUiThread(() -> Toast.makeText(this, "Lỗi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_LONG).show());
            return null;
        }
    }

    private void uploadImageToImgur(String base64Image, Callback callback) {
        String clientId = "22b4c7b814c93ec";
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", base64Image)
                .build();

        Request request = new Request.Builder()
                .url("https://api.imgur.com/3/image")
                .addHeader("Authorization", "Client-ID " + clientId)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(callback);
    }

    private void updateFood() {
        String id = etMaMonAn.getText().toString().trim();
        String name = etTenMonAn.getText().toString().trim();
        String priceStr = etGia.getText().toString().trim();

        if (id.isEmpty() || name.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        int price;
        try {
            price = Integer.parseInt(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá phải là số nguyên", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_LONG).show();
            return;
        }

        if (imageUri == null) {
            paFood updatedFood = new paFood(id, name, price, food.getImageUrl());
            firestoreHelper.updateFood(updatedFood,
                    () -> runOnUiThread(() -> {
                        Toast.makeText(this, "Cập nhật món ăn thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    }),
                    error -> runOnUiThread(() -> Toast.makeText(this, "Lỗi cập nhật: " + error, Toast.LENGTH_LONG).show()));
        } else {
            String base64Image = imageToBase64(imageUri);
            if (base64Image == null) return;

            uploadImageToImgur(base64Image, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(paSuaXoaMonAnActivity.this, "Lỗi upload ảnh: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        try {
                            JSONObject json = new JSONObject(responseBody);
                            String imageUrl = json.getJSONObject("data").getString("link");
                            paFood updatedFood = new paFood(id, name, price, imageUrl);
                            firestoreHelper.updateFood(updatedFood,
                                    () -> runOnUiThread(() -> {
                                        Toast.makeText(paSuaXoaMonAnActivity.this, "Cập nhật món ăn thành công", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }),
                                    error -> runOnUiThread(() -> Toast.makeText(paSuaXoaMonAnActivity.this, "Lỗi Firestore: " + error, Toast.LENGTH_LONG).show()));
                        } catch (Exception e) {
                            runOnUiThread(() -> Toast.makeText(paSuaXoaMonAnActivity.this, "Lỗi phân tích Imgur: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(paSuaXoaMonAnActivity.this, "Lỗi Imgur: " + response.message(), Toast.LENGTH_LONG).show());
                    }
                }
            });
        }
    }

    private void deleteFood() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_LONG).show();
            return;
        }

        firestoreHelper.deleteFood(food.getId(),
                () -> runOnUiThread(() -> {
                    Toast.makeText(this, "Xóa món ăn thành công", Toast.LENGTH_SHORT).show();
                    finish();
                }),
                error -> runOnUiThread(() -> Toast.makeText(this, "Lỗi xóa: " + error, Toast.LENGTH_LONG).show()));
    }
}