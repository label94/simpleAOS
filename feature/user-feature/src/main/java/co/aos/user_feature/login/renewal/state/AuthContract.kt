package co.aos.user_feature.login.renewal.state

import co.aos.base.state.UiEffect
import co.aos.base.state.UiEvent
import co.aos.base.state.UiState
import co.aos.domain.model.User

/**
 * 사용자 인증 관련 명세서 정의
 * */
class AuthContract {
    /** UI 이벤트 정의 */
    sealed class Event: UiEvent {
        /** id 업데이트 */
        data class UpdateID(val id: String) : Event()

        /** password 업데이트 */
        data class UpdatePassword(val password: String) : Event()

        /** 로그인 클릭 */
        data object ClickSignIn : Event()

        /** 회원가입 화면 이동 */
        data object MoveToSignUpScreen : Event()
    }

    /** 상태 정의 */
    data class State(
        val id: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val user: User? = null,
    ): UiState

    /** 1회성 이벤트 정의 */
    sealed class Effect: UiEffect {
        /** Snack bar 표시 */
        data class ShowSnackBar(val message: String): Effect()

        /** 페이지 전환 */
        data class MovePage(val page: String): Effect()

        /** 로그인 성공 */
        data class LoginSuccess(val user: User): Effect()
    }
}