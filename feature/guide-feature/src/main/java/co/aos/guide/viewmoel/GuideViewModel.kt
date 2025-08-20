package co.aos.guide.viewmoel

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.lifecycle.viewModelScope
import co.aos.base.BaseViewModel
import co.aos.domain.usecase.UpdateIsFirstRunUseCase
import co.aos.guide.R
import co.aos.guide.model.GuidePermissionData
import co.aos.guide.state.GuideContract
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 안내 화면 관련 뷰모델
 * */
@HiltViewModel
class GuideViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val updateIsFirstRunUseCase: UpdateIsFirstRunUseCase
): BaseViewModel<GuideContract.Event, GuideContract.State, GuideContract.Effect>() {

    init {
        setEvent(GuideContract.Event.LoadGuidePermissionData)
    }

    override fun createInitialState(): GuideContract.State {
        return GuideContract.State()
    }

    override fun handleEvent(event: GuideContract.Event) {
        when(event) {
            is GuideContract.Event.LoadGuidePermissionData -> {
                loadGuidePermissionData()
            }
            is GuideContract.Event.OnNextStep -> {
                updateIsFirstRun()
            }
        }
    }

    /** 접근권한 관련 데이터 로드 */
    private fun loadGuidePermissionData() {
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

    /** 앱 최초 실행 유무 Flag 값 업데이트 */
    private fun updateIsFirstRun() {
        viewModelScope.launch {
            // 값 업데이트
            updateIsFirstRunUseCase(true)

            // 알림 권한 요청
            setEffect(GuideContract.Effect.RequestPermission)
        }
    }
}