package co.aos.user_feature.join.renewal.state

import co.aos.base.state.UiEffect
import co.aos.base.state.UiEvent
import co.aos.base.state.UiState
import co.aos.domain.model.User

/**
 * 회원가입 기능 명세서
 * */
class SignUpContract {
    /** 이벤트 정의 */
    sealed class Event: UiEvent {
        /** 입력 데이터 초기화 */
        data object InitData : Event()

        /** id 업데이트 */
        data class UpdateID(val id: String) : Event()

        /** password 업데이트 */
        data class UpdatePassword(val password: String) : Event()

        /** passwordCheck 업데이트 */
        data class UpdatePasswordCheck(val isPasswordCheck: Boolean) : Event()

        /** nickName 업데이트 */
        data class UpdateNickName(val nickName: String) : Event()

        /** 프로필 이미지 코드 업데이트 */
        data class UpdateLocalProfileImgCode(val code: Int) : Event()

        /** 닉네임 중복 확인 */
        data object ClickToNickName : Event()

        /** id 중복 확인 */
        data object ClickToIdCheck : Event()

        /** 회원가입 버튼 클릭 */
        data object ClickSignUp : Event()

        /** 프로필 이미지 피커 팝업 표시 */
        data object ShowProfilePicker : Event()

        /** 프로필 이미지 피커 팝업 숨김 */
        data object HideProfilePicker : Event()
    }

    /** 상태 정의 */
    data class State(
        val id: String = "",
        val password: String = "",
        val nickName: String = "",
        val nickNameAvailable: Boolean = false,
        val isLoading: Boolean = false,
        val isIdAvailable: Boolean = false,
        val isPasswordVisible: Boolean = false,
        val localProfileImgCode: Int = 0,
        val user: User? = null,
        val isShowProfilePicker: Boolean = false
    ): UiState

    /** 1회성 이벤트 정의 */
    sealed class Effect: UiEffect {
        /** Snack bar 표시 */
        data class ShowSnackBar(val message: String): Effect()

        /** 회원가입 성공 */
        data object SignUpSuccess: Effect()
    }
}