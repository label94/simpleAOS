package co.aos.myjetpack.intro.legacy.viewmodel

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.lifecycle.viewModelScope
import co.aos.base.BaseViewModel
import co.aos.local.pref.SharedPreferenceManager
import co.aos.local.pref.consts.SharedConstants
import co.aos.myjetpack.R
import co.aos.myjetpack.intro.legacy.model.GuidePermissionData
import co.aos.myjetpack.intro.legacy.state.IntroContract
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 인트로 관련 뷰모델
 * */
@HiltViewModel
class IntroViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferenceManager: SharedPreferenceManager
): BaseViewModel<IntroContract.Event, IntroContract.State, IntroContract.Effect>() {

    init {
        viewModelScope.launch {
            // 앱 실행 시 최초 실행 유무인지 확인 후, 최초실행이 아닌 경우에는 바로 푸시 권한 체크
            // 최초 실행 경우에는 "접근권한" 안내 화면에서 푸시 권한을 체크하기 때문에, 분기 처리 필요!
            val isFirstLaunch = isFirstLaunch()
            if (!isFirstLaunch) {
                setEffect(IntroContract.Effect.CheckNotificationPermission)
            }

            // 안내 화면 구성을 위한 데이터 생성
            setEvent(IntroContract.Event.LoadGuidePermissionData)

            // 3초 동안 스플래시 실행
            delay(3000)

            // 상태 업데이트 이벤트 요청
            setEvent(IntroContract.Event.UpdateIsFirstLaunch(isFirstLaunch))
            setEvent(IntroContract.Event.UpdateIsShowSplash(false))
        }
    }

    /** 초기 상태 설정 */
    override fun createInitialState(): IntroContract.State {
        return IntroContract.State()
    }

    /** 이벤트 제어 */
    override fun handleEvent(event: IntroContract.Event) {
        when(event) {
            is IntroContract.Event.UpdateNotificationPermission -> {
                setState {
                    copy(isNotificationPermission = event.isGranted)
                }
            }
            is IntroContract.Event.UpdateIsShowSplash -> {
                setState { copy(isShowSplash = event.isShow) }
            }
            is IntroContract.Event.UpdateIsFirstLaunch -> {
                setState { copy(isFirstLaunch = event.isFirstLaunch) }
            }
            is IntroContract.Event.OnBackPressed -> {
                setEffect(IntroContract.Effect.FinishActivity)
            }
            is IntroContract.Event.OnNextStep -> {
                // 알람 권한 체크
                setEffect(IntroContract.Effect.CheckNotificationPermission)

                // 상태 업데이트
                setEvent(IntroContract.Event.UpdateIsFirstLaunch(false))

                // local 저장소 업데이트
                sharedPreferenceManager.setBoolean(SharedConstants.KEY_IS_FIRST_LAUNCH, false)
            }
            is IntroContract.Event.LoadGuidePermissionData -> {
                createGuidePermissionData()
            }
        }
    }

    /** 최초 1회 실행 유무 확인 */
    private fun isFirstLaunch(): Boolean {
        return sharedPreferenceManager.getBoolean(SharedConstants.KEY_IS_FIRST_LAUNCH, true)
    }

    /** 접근권한 안내 화면 구성을 위한 데이터 생성 */
    private fun createGuidePermissionData() {
        // 필수 권한 리스트
        val requiredPermissionList = mutableListOf<GuidePermissionData>()

        // 선택적 권한 리스트
        val optionalPermissionList = mutableListOf<GuidePermissionData>()

        // 인터넷 관련 권한 설명
        val internetPermission = GuidePermissionData(
            icon = Icons.Filled.NetworkWifi,
            name = context.getString(R.string.guide_permission_internet),
            description = context.getString(R.string.guide_permission_internet_contents),
            isOptional = false
        )
        requiredPermissionList.add(internetPermission)

        // 알림 관련 권한 설명
        val notificationPermission = GuidePermissionData(
            icon = Icons.Filled.Notifications,
            name = context.getString(R.string.guide_permission_notification),
            description = context.getString(R.string.guide_permission_notification_contents),
            isOptional = true
        )

        // 카메라 관련 권한 설명
        val cameraPermission = GuidePermissionData(
            icon = Icons.Filled.PhotoCamera,
            name = context.getString(R.string.guide_permission_camera),
            description = context.getString(R.string.guide_permission_camera_contents),
            isOptional = true
        )

        // 블루투스 관련 권한 설명
        val bluetoothPermission = GuidePermissionData(
            icon = Icons.Filled.Bluetooth,
            name = context.getString(R.string.guide_permission_bluetooth),
            description = context.getString(R.string.guide_permission_bluetooth_contents),
            isOptional = true
        )

        optionalPermissionList.add(notificationPermission)
        optionalPermissionList.add(cameraPermission)
        optionalPermissionList.add(bluetoothPermission)

        // 상태 업데이트
        setState { copy(guideRequiredPermissionList = requiredPermissionList) }
        setState { copy(guideOptionalPermissionList = optionalPermissionList) }
    }
}