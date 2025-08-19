package co.aos.user_feature.join.state

import android.content.Intent
import android.net.Uri
import co.aos.base.state.UiEffect
import co.aos.base.state.UiEvent
import co.aos.base.state.UiState

/**
 * 회원가입 화면 기능 명세서 정의
 * */
class JoinContract {
    /** 이벤트 정의 */
    sealed class Event: UiEvent {
        /** 초기화 이벤트 */
        data object InitData: Event()

        /** 회원가입 버튼 이벤트 */
        data object OnJoin: Event()

        /** id 업데이트 이벤트 */
        data class OnUpdateId(val id: String): Event()

        /** password 업데이트 이벤트 */
        data class OnUpdatePassword(val password: String): Event()

        /** nickname 업데이트 이벤트 */
        data class OnUpdateNickname(val nickname: String): Event()

        /** profileImagePath 업데이트 이벤트 */
        data class OnUpdateProfileImagePath(val profileImagePath: String): Event()

        /** 비밀번호 표시 여부 업데이트 이벤트 */
        data class OnUpdatePasswordVisible(val isVisible: Boolean): Event()

        /** 프로필 이미지 선택 */
        data object ClickProfileImage: Event()

        /** id 중복 체크 */
        data object CheckIdDuplicate: Event()

        /** id 중복 체크 flag 업데이트 */
        data class UpdateIdDuplicate(val isDuplicate: Boolean): Event()

        /** 카메라 권한 요청 */
        data object RequestCameraPermission: Event()

        /** 카메라 권한 허용 */
        data class UpdateCameraPermissionGranted(val isGranted: Boolean): Event()

        /** chooser 관련 처리 */
        data class ChooserResult(val resultCode: Int, val intent: Intent?): Event()
    }

    /** 상태 정의 */
    data class State(
        val id: String = "",
        val password: String = "",
        val nickname: String = "",
        val profileImagePath: String = "",
        val isIdValid: Boolean = false,
        val passwordVisible: Boolean = false,
        val isCameraPermissionGranted: Boolean = false,
    ): UiState

    /** 1회성 이벤트 정의 */
    sealed class Effect: UiEffect {
        /** 스낵바 표시 */
        data class ShowSnackBar(val message: String): Effect()

        /** 회원가입 성공 */
        data object JoinSuccess: Effect()

        /** 카메라 권한 요청 */
        data object RequestCameraPermission: Effect()

        /** chooser 관련 처리 */
        data class ChooserResult(val chooserIntent: Intent): Effect()
    }
}