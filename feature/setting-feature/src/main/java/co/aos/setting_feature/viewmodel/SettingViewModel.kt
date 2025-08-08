package co.aos.setting_feature.viewmodel

import android.content.Context
import co.aos.base.BaseViewModel
import co.aos.base.utils.openDeviceSetting
import co.aos.domain.usecase.SettingNotificationUseCase
import co.aos.myutils.log.LogUtil
import co.aos.setting_feature.state.SettingContract
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * 설정 뷰 모델
 * */
@HiltViewModel
class SettingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingNotificationUseCase: SettingNotificationUseCase
): BaseViewModel<SettingContract.Event, SettingContract.State, SettingContract.Effect>() {

    init {
        // 기기 알람 권한 확인
        setEvent(SettingContract.Event.CheckNotificationPermission)
    }

    /** 초기 상태 설정 */
    override fun createInitialState(): SettingContract.State {
        return SettingContract.State()
    }

    /** 이벤트 제어 */
    override fun handleEvent(event: SettingContract.Event) {
        when(event) {
            is SettingContract.Event.CheckNotificationPermission -> {
                checkNotificationPermission()
            }
            is SettingContract.Event.UpdateNotificationPermission -> {
                updateNotificationPermission(event.isGranted)
            }
            is SettingContract.Event.FinishSettingActivity -> {
                setEffect(SettingContract.Effect.FinishActivity)
            }
            is SettingContract.Event.MoveDeviceSetting -> {
                context.openDeviceSetting()
            }
        }
    }

    /** 알림 권한 확인 */
    private fun checkNotificationPermission() {
        val isNotificationGranted = settingNotificationUseCase(context)
        LogUtil.i(LogUtil.DEFAULT_TAG, "isNotificationGranted : $isNotificationGranted")

        // 상태 업데이트를 위한 이벤트 요청
        setEvent(SettingContract.Event.UpdateNotificationPermission(isNotificationGranted))
    }

    /** 알람 권한 상태 업데이트 */
    private fun updateNotificationPermission(isGranted: Boolean) {
        setState { copy(isNotificationPermissionGranted = isGranted) }
    }
}