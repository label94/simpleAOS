package co.aos.domain.usecase.impl

import android.content.Context
import co.aos.domain.repository.SettingNotificationRepository
import co.aos.domain.usecase.SettingNotificationUseCase
import javax.inject.Inject

/**
 * 설정 화면 내 알람 권한 관련 유스케이스 구현 클래스
 * */
class SettingNotificationUseCaseImpl @Inject constructor(
    private val repository: SettingNotificationRepository
) : SettingNotificationUseCase {

    override fun invoke(context: Context): Boolean {
        return repository.isNotificationPermissionGranted(context)
    }
}