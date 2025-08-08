package co.aos.user_feature.login.state

import co.aos.base.state.UiEffect
import co.aos.base.state.UiEvent
import co.aos.base.state.UiState
import co.aos.domain.model.User
import co.aos.user_feature.login.model.LoginInfoModel

/**
 * 로그인 관련 기능 명세서
 * */
class LoginContract {
    /** 이벤트 정의 */
    sealed class Event : UiEvent {
        /** 로그인 요청 */
        data object RequestLogin : Event()

        /** 회원가입 화면 이동 */
        data object MoveUserJoinPage : Event()

        /** 자동 로그인 */
        data object AutoLogin : Event()

        /** id 상태 업데이트 */
        data class UpdateId(val id: String) : Event()

        /** password 상태 업데이트 */
        data class UpdatePassword(val password: String) : Event()

        /** isAutoLogin 상태 업데이트 */
        data class UpdateIsAutoLogin(val isAutoLoginEnable: Boolean) : Event()
    }

    /** 상태 정의 */
    data class State(
        val id : String = "",
        val password : String = "",
        val isAutoLoginEnable : Boolean = false,
        val loginState : LoginState = LoginState.LoginOut,
    ): UiState

    /** 로그인 관련 UI 상태 */
    sealed class LoginState {
        /** 로딩 */
        data object Loading : LoginState()

        /** 로그인 상태 */
        data class Login(val user: User?) : LoginState()

        /** 로그아웃 상태 */
        data object LoginOut : LoginState()
    }

    /** 1회성 이벤트 정의 */
    sealed class Effect: UiEffect {
        /** Snack bar 표시 */
        data class ShowSnackBar(val message: String): Effect()

        /** 페이지 전환 */
        data class MovePage(val page: String): Effect()

        /** 로그인 완료 */
        data class LoginComplete(val loginInfo: LoginInfoModel): Effect()
    }
}