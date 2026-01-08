package com.arjundabbe.jivanman.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.arjundabbe.jivanman.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.net.URL;

/**
 * Handles Firebase Cloud Messaging (FCM) push notifications
 * for Jivanman News Application.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    /**
     * Triggered when a push notification is received
     */
    @Override
    public void onMessageReceived(RemoteMessage message) {

        Log.d("FCM", "Notification received");

        // Extract custom data payload from FCM message
        String title   = message.getData().get("title");
        String body    = message.getData().get("body");
        String newsId  = message.getData().get("newsId");
        String imageUrl = message.getData().get("image");

        // Display notification
        showNotification(title, body, newsId, imageUrl);
    }

    /**
     * Builds and displays notification with optional image
     */
    private void showNotification(String title,
                                  String body,
                                  String newsId,
                                  String imageUrl) {

        // Intent to open NewsDetailActivity on notification click
        Intent intent = new Intent(this, NewsDetailActivity.class);
        intent.putExtra("newsId", newsId);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        // Load image from URL if available (Big Picture Style)
        Bitmap bitmap = null;
        try {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                URL url = new URL(imageUrl);
                bitmap = BitmapFactory
                        .decodeStream(url.openConnection().getInputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Notification Manager
        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        String channelId = "jivanman_channel";

        // Create notification channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Jivanman News",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }

        // Notification builder
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.jivanman_name11)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setSound(
                                RingtoneManager.getDefaultUri(
                                        RingtoneManager.TYPE_NOTIFICATION
                                )
                        );

        // Apply Big Picture style only if image exists
        if (bitmap != null) {
            builder.setStyle(
                    new NotificationCompat.BigPictureStyle()
                            .bigPicture(bitmap)
            );
        }

        // Show notification with unique ID
        manager.notify(
                (int) System.currentTimeMillis(),
                builder.build()
        );
    }
}
