package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class paMyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("FCM", "Nhận được thông báo: " + remoteMessage.getMessageId());
        if (remoteMessage.getNotification() != null) {
            Log.d("FCM", "Title: " + remoteMessage.getNotification().getTitle());
            Log.d("FCM", "Body: " + remoteMessage.getNotification().getBody());
            showNotification(
                    remoteMessage.getNotification().getTitle() != null ?
                            remoteMessage.getNotification().getTitle() : "Thông báo",
                    remoteMessage.getNotification().getBody() != null ?
                            remoteMessage.getNotification().getBody() : ""
            );
        }
    }

    private void showNotification(String title, String message) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "notice_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Notices",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setAutoCancel(true);

        notificationManager.notify(new Random().nextInt(), builder.build());
    }
}