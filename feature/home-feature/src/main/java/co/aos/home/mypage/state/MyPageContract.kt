package co.aos.home.mypage.state

import co.aos.base.state.UiEffect
import co.aos.base.state.UiEvent
import co.aos.base.state.UiState
import co.aos.domain.model.AppInfo
import co.aos.domain.model.User

/** 마이페이지 기능 명세서 */
class MyPageContract {
    /** 이벤트 정의 */
    sealed class Event: UiEvent {
        /** 초기 Load */
        data object Load: Event()

        /** 로그아웃 버튼 클릭 이벤트 */
        data object OnClickLogout: Event()

        /** 비밀번호 변경 버튼 클릭 이벤트 */
        data object OnClickChangePassword : Event()

        /** 비밀번호 변경 취소 클릭 이벤트 */
        data object OnDismissChangePassword : Event()

        /** 비밀번호 변경 이벤트 */
        data class OnSubmitChangePassword(
            val currentId: String,
            val currentPassword: String,
            val newPassword: String,
            val confirmPassword: String
        ): Event()

        /** 회원탈퇴 버튼 클릭 이벤트 */
        data object OnClickDeleteAccount : Event()

        /** 회원탈퇴 취소 클릭 이벤트 */
        data object OnDismissDeleteAccount : Event()

        /** 회원탈퇴 이벤트 */
        data class OnSubmitDeleteAccount(val currentPassword: String, val confirmed: Boolean) : Event()
    }

    /** 상태 정의 */
    data class State(
        val loading: Boolean = false,
        val profile: User? = null,
        val appInfo: AppInfo? = null,
        val showChangePassword: Boolean = false,
        val showDeleteAccount: Boolean = false, // 회원탈퇴 다이얼로그 표시
        val error: String? = null
    ): UiState

    /** 1회성 이벤트 */
    sealed class Effect: UiEffect {
        data object NavigateToLogin : Effect() // 로그아웃 후 로그인으로 이동
        data class Toast(val msg: String) : Effect()
    }
}