package co.aos.domain.repository

import android.content.Context

/**
 * 설정 화면 내 알람 권한 관련 Repository
 * */
interface SettingNotificationRepository {
    /** 디바이스 기기 알람 허용이 되어 있는지 확인 */
    fun isNotificationPermissionGranted(context: Context): Boolean
}