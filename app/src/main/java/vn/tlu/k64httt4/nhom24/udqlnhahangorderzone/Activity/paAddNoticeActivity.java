package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.List;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import android.util.Base64;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper.paNoticeHelper;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

public class paAddNoticeActivity extends AppCompatActivity {
    private paNoticeHelper noticeHelper;
    private EditText etTieuDe;
    private EditText etThongTin;
    private static final String FCM_API = "https://fcm.googleapis.com/v1/projects/quanlynhahang-42f0b/messages:send";
    private String userId; // Biến lưu userId

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pa_act_add_noti);

        noticeHelper = new paNoticeHelper();
        etTieuDe = findViewById(R.id.etTieuDe);
        etThongTin = findViewById(R.id.etThongtin);

        ShapeableImageView backButton = findViewById(R.id.r3rk27dbznwb);
        backButton.setOnClickListener(v -> finish());

        // Kiểm tra xem người dùng đã đăng nhập chưa
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để tiếp tục!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ttLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Lấy userId từ Firebase Authentication
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (userId == null || userId.trim().isEmpty()) {
            Toast.makeText(this, "Không thể xác định người dùng, vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ttLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        Button btnLuu = findViewById(R.id.btnLuu);
        btnLuu.setOnClickListener(v -> {
            String title = etTieuDe.getText().toString().trim();
            String content = etThongTin.getText().toString().trim();

            if (!title.isEmpty() && !content.isEmpty()) {
                noticeHelper.addNotice(title, content, userId, success -> {
                    if (success) {
                        Toast.makeText(this, "Đã lưu thông báo", Toast.LENGTH_SHORT).show();
                        sendNotificationToAllDevices(title, content);
                        finish();
                    } else {
                        Toast.makeText(this, "Lỗi khi lưu", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnHuy = findViewById(R.id.btnHuy);
        btnHuy.setOnClickListener(v -> finish());
    }

    private void sendNotificationToAllDevices(String title, String content) {
        FirebaseFirestore.getInstance().collection("devices")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> tokens = queryDocumentSnapshots.getDocuments()
                            .stream()
                            .map(doc -> doc.getString("token"))
                            .toList();
                    Log.d("FCM", "Số lượng token: " + tokens.size());
                    for (String token : tokens) {
                        new SendNotificationTask(this).execute(token, title, content);
                    }
                })
                .addOnFailureListener(e -> Log.w("FCM", "Error fetching tokens", e));
    }

    public static class SendNotificationTask extends AsyncTask<String, Void, Void> {
        private final Context context;

        public SendNotificationTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(String... params) {
            String token = params[0];
            String title = params[1];
            String content = params[2];

            try {
                String accessToken = getAccessToken();
                if (accessToken == null) {
                    Log.e("FCM", "Failed to get access token");
                    return null;
                }

                URL url = new URL(FCM_API);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + accessToken);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject message = new JSONObject();
                JSONObject notification = new JSONObject();
                notification.put("title", title);
                notification.put("body", content);
                message.put("notification", notification);
                message.put("token", token);

                JSONObject json = new JSONObject();
                json.put("message", message);

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes(StandardCharsets.UTF_8));
                os.close();

                int responseCode = conn.getResponseCode();
                Log.d("FCM", "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("FCM", "Thông báo gửi thành công đến " + token);
                } else {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    Log.e("FCM", "Lỗi gửi thông báo: " + response.toString());
                }

                conn.disconnect();
            } catch (Exception e) {
                Log.e("FCM", "Error sending notification", e);
            }
            return null;
        }

        private String getAccessToken() {
            try {
                InputStream inputStream = context.getResources().openRawResource(R.raw.service_account);
                if (inputStream == null) {
                    Log.e("FCM", "Service account file not found in raw resources");
                    return null;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder jsonString = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonString.append(line);
                }
                reader.close();
                inputStream.close();

                JSONObject serviceAccount = new JSONObject(jsonString.toString());
                String clientEmail = serviceAccount.getString("client_email");
                String privateKey = serviceAccount.getString("private_key");

                String privateKeyCleaned = privateKey
                        .replace("-----BEGIN PRIVATE KEY-----", "")
                        .replace("-----END PRIVATE KEY-----", "")
                        .replaceAll("\\n", "")
                        .trim();

                byte[] privateKeyBytes = Base64.decode(privateKeyCleaned, Base64.DEFAULT);
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PrivateKey key = keyFactory.generatePrivate(keySpec);

                long now = System.currentTimeMillis() / 1000;
                String jwt = Jwts.builder()
                        .setIssuer(clientEmail)
                        .setAudience("https://oauth2.googleapis.com/token")
                        .setSubject(clientEmail)
                        .claim("scope", "https://www.googleapis.com/auth/firebase.messaging")
                        .setIssuedAt(new java.util.Date(now * 1000))
                        .setExpiration(new java.util.Date((now + 3600) * 1000))
                        .signWith(SignatureAlgorithm.RS256, key)
                        .compact();

                URL url = new URL("https://oauth2.googleapis.com/token");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                String grantType = "urn:ietf:params:oauth:grant-type:jwt-bearer";
                String data = "grant_type=" + grantType + "&assertion=" + jwt;

                OutputStream os = conn.getOutputStream();
                os.write(data.getBytes(StandardCharsets.UTF_8));
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    String accessToken = jsonResponse.getString("access_token");
                    Log.d("FCM", "Access Token: " + accessToken);
                    return accessToken;
                } else {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    Log.e("FCM", "Failed to get access token, response code: " + responseCode + ", response: " + response.toString());
                }

                conn.disconnect();
            } catch (Exception e) {
                Log.e("FCM", "Error getting access token: " + e.getMessage(), e);
            }
            return null;
        }
    }
}