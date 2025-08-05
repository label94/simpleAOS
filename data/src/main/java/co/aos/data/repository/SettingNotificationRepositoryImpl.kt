package co.aos.data.repository

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import co.aos.domain.repository.SettingNotificationRepository
import javax.inject.Inject

/**
 * 설정 화면 내 알람 권한 관련 Repository 구현 클래스
 * */
class SettingNotificationRepositoryImpl @Inject constructor() : SettingNotificationRepository {

    /** 디바이스 알람 권한 확인 */
    override fun isNotificationPermissionGranted(context: Context): Boolean {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // 안드로이드 13이상 경우
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                    && notificationManager.areNotificationsEnabled()
        } else {
            // 안드로이드 13 미만 경우
            notificationManager.areNotificationsEnabled()
        }
    }
}