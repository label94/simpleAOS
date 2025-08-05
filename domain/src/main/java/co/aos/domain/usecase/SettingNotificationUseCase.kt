package co.aos.domain.usecase

import android.content.Context

/**
 * 설정 화면 내 알람 권한 관련 유스케이스
 * */
interface SettingNotificationUseCase {
    /** 디바이스 기기 알람 허용이 되어 있는지 확인 */
    operator fun invoke(context: Context): Boolean
}