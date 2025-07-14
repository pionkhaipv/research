package pion.tech.pionbase.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import pion.tech.pionbase.R
import pion.tech.pionbase.app.presentation.MainActivity

/**
 * Helper class để tạo và quản lý notification cho popup detection
 */
object PopupNotificationHelper {
    private const val CHANNEL_ID = "popup_detection_channel"
    private const val NOTIFICATION_ID = 1001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Popup Detection"
            val descriptionText = "Thông báo khi phát hiện popup quảng cáo"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                    setSound(null, null) // Tắt âm thanh
                    enableVibration(false) // Tắt rung
                }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showPopupDetectedNotification(
        context: Context,
        appName: String,
        popupType: String,
    ) {
        createNotificationChannel(context)

        val intent =
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )

        val currentTime =
            java.text
                .SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
                .format(java.util.Date())

        val notification =
            NotificationCompat
                .Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_security)
                .setContentTitle("Popup quảng cáo phát hiện")
                .setContentText("Đã phát hiện popup từ $appName")
                .setStyle(
                    NotificationCompat
                        .BigTextStyle()
                        .bigText("Ứng dụng: $appName\nLoại popup: $popupType\nThời gian: $currentTime"),
                ).setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun createPersistentNotification(context: Context): Notification {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )

        return NotificationCompat
            .Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_security)
            .setContentTitle("Dịch vụ phát hiện popup")
            .setContentText("Đang hoạt động để bảo vệ bạn khỏi popup quảng cáo")
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setSound(null)
            .setVibrate(null)
            .build()
    }
}
