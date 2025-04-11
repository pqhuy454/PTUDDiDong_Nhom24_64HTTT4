package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Adapter.paNoticeAdapter;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.DatabaseHelper.paNoticeHelper;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model.paNotice;
import vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class paNotificationFragment extends Fragment {
    private static final int NOTIFICATION_PERMISSION_CODE = 100;
    private RecyclerView recyclerView;
    private paNoticeHelper noticeHelper;
    private paNoticeAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pa_act_noti, container, false);

        // Initialize Firebase
        try {
            FirebaseApp.initializeApp(requireContext());
            Log.d("FCM", "Firebase initialized successfully");
        } catch (Exception e) {
            Log.e("FCM", "Error initializing Firebase: " + e.getMessage(), e);
        }

        // Setup RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewTB);
        noticeHelper = new paNoticeHelper();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Check notification permission
        checkNotificationPermission();

        // Load notices
        loadNotices();

        // Setup add notice button
        ShapeableImageView imgThemThongBao = view.findViewById(R.id.imgThemThongBao);
        imgThemThongBao.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), paAddNoticeActivity.class);
            startActivity(intent);
        });

        // Handle FCM token
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                        Toast.makeText(requireContext(), "Không lấy được token FCM: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    String token = task.getResult();
                    Log.d("FCM", "FCM Token: " + token);
                    Toast.makeText(requireContext(), "Token: " + token, Toast.LENGTH_LONG).show();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> device = new HashMap<>();
                    device.put("token", token);
                    db.collection("devices")
                            .document(token)
                            .set(device)
                            .addOnSuccessListener(aVoid -> Log.d("FCM", "Token saved to Firestore"))
                            .addOnFailureListener(e -> {
                                Log.w("FCM", "Error saving token", e);
                                Toast.makeText(requireContext(), "Lỗi lưu token: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                });

        // Setup real-time listener
        setupRealtimeListener();

        return view;
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Quyền thông báo đã được cấp", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Ứng dụng cần quyền thông báo để hoạt động", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadNotices() {
        noticeHelper.getNotices(new paNoticeHelper.NoticeCallback() {
            @Override
            public void onComplete(List<paNotice> notices) {
                adapter = new paNoticeAdapter(notices, requireContext()); // Truyền context
                recyclerView.setAdapter(adapter);
            }
        });
    }

    private void setupRealtimeListener() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("notices")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w("NoticeFragment", "Listen failed", e);
                        return;
                    }
                    if (snapshots != null) {
                        List<paNotice> notices = new ArrayList<>();
                        for (DocumentSnapshot doc : snapshots) {
                            paNotice notice = doc.toObject(paNotice.class);
                            if (notice != null) {
                                notice.setId(doc.getId());
                                notices.add(notice);
                            }
                        }
                        if (adapter != null) {
                            adapter.updateNotices(notices);
                        } else {
                            adapter = new paNoticeAdapter(notices, requireContext()); // Truyền context
                            recyclerView.setAdapter(adapter);
                        }
                    }
                });
    }
}