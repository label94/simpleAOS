package co.aos.home.mypage.viewmodel

import android.content.Context
import androidx.lifecycle.viewModelScope
import co.aos.base.BaseViewModel
import co.aos.domain.model.AppInfo
import co.aos.domain.usecase.user.renewal.ChangePasswordUseCase
import co.aos.domain.usecase.user.renewal.DeleteAccountUseCase
import co.aos.domain.usecase.user.renewal.GetCurrentUserUseCase
import co.aos.domain.usecase.user.renewal.ReauthenticateUseCase
import co.aos.domain.usecase.user.renewal.SignOutUseCase
import co.aos.home.mypage.state.MyPageContract
import co.aos.myutils.common.AppInfoUtils
import co.aos.myutils.log.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 마이페이지 뷰모델 */
@HiltViewModel
class MyPageViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: SignOutUseCase,
    private val reauthenticateUseCase: ReauthenticateUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase
): BaseViewModel<MyPageContract.Event, MyPageContract.State, MyPageContract.Effect>() {

    init {
        // 초기 컨텐츠 로드
        setEvent(MyPageContract.Event.Load)
    }

    /** 초기 상태 정의 */
    override fun createInitialState(): MyPageContract.State {
        return MyPageContract.State()
    }

    /** 이벤트 제어 */
    override fun handleEvent(event: MyPageContract.Event) {
        when(event) {
            is MyPageContract.Event.Load -> {
                load()
            }
            is MyPageContract.Event.OnClickLogout -> {
                logout()
            }
            is MyPageContract.Event.OnClickChangePassword -> {
                setState { copy(showChangePassword = true) }
            }
            is MyPageContract.Event.OnDismissChangePassword -> {
                setState { copy(showChangePassword = false) }
            }
            is MyPageContract.Event.OnSubmitChangePassword -> {
                changePasswordFlow(
                    currentId = event.currentId,
                    currentPassword = event.currentPassword,
                    newPassword = event.newPassword,
                    confirmPassword = event.confirmPassword
                )
            }
            is MyPageContract.Event.OnClickDeleteAccount -> {
                setState { copy(showDeleteAccount = true) }
            }
            is MyPageContract.Event.OnDismissDeleteAccount -> {
                setState { copy(showDeleteAccount = false) }
            }
            is MyPageContract.Event.OnSubmitDeleteAccount -> {
                deleteAccountFlow(
                    currentPassword = event.currentPassword,
                    confirmed = event.confirmed
                )
            }
        }
    }

    /** 초기 load */
    private fun load() {
        viewModelScope.launch {
            try {
                setState { copy(loading = true, error = null) }

                // 로그인 사용자 정보
                val user = getCurrentUserUseCase.invoke() ?: run {
                    setState { copy(loading = false) }
                    setEffect(MyPageContract.Effect.Toast("정보를 불러오지 못했어요."))

                    // 0.7초 후 로그인 화면으로 이동
                    delay(700)
                    setEffect(MyPageContract.Effect.NavigateToLogin)
                    return@launch
                }

                // 앱 정보
                val appInfo = AppInfo(
                    versionName = AppInfoUtils.getAppVersionName(context),
                    versionCode = AppInfoUtils.getAppVersionCode(context)
                )

                // 상태 업데이트
                setState { copy(loading = false, profile = user, appInfo = appInfo) }
            } catch (t: Throwable) {
                t.printStackTrace()
                LogUtil.e(LogUtil.MY_PAGE_LOG_TAG, "error : $t")

                setState { copy(loading = false, error = t.message) }
                setEffect(MyPageContract.Effect.Toast("정보를 불러오지 못했어요."))
            }
        }
    }

    /** 패스워드 변경 관련 */
    private fun changePasswordFlow(
        currentId: String,
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        viewModelScope.launch {
            if (currentId.isBlank() || currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
                setEffect(MyPageContract.Effect.Toast("모든 값을 입력해 주세요."))
                return@launch
            }
            if (newPassword != confirmPassword) {
                setEffect(MyPageContract.Effect.Toast("새 비밀번호가 일치하지 않아요."))
                return@launch
            }
            if (newPassword.length < 6) {
                setEffect(MyPageContract.Effect.Toast("새 비밀번호는 6자리 이상이어야 해요."))
                return@launch
            }

            try {
                setState { copy(loading = true) }
                reauthenticateUseCase.invoke(currentId, currentPassword)
                changePasswordUseCase.invoke(currentPassword, newPassword)

                setState { copy(loading = false, showChangePassword = false) }
                setEffect(MyPageContract.Effect.Toast("비밀번호가 변경되었어요. 다시 로그인 하세요."))

                // 0.5초 후 로그아웃 진행
                delay(500)
                setEvent(MyPageContract.Event.OnClickLogout)
            } catch (t: Throwable) {
                t.printStackTrace()
                LogUtil.e(LogUtil.MY_PAGE_LOG_TAG, "error : $t")

                setState { copy(loading = false) }
                setEffect(MyPageContract.Effect.Toast("비밀번호 변경 중 오류가 발생했어요."))
            }
        }
    }

    /** 회원탈퇴 이벤트 */
    private fun deleteAccountFlow(
        currentPassword: String,
        confirmed: Boolean
    ) {
        viewModelScope.launch {
            if (!confirmed) {
                setEffect(MyPageContract.Effect.Toast("탈퇴 동의 체크가 필요해요."))
                return@launch
            }

            if (currentPassword.isBlank()) {
                setEffect(MyPageContract.Effect.Toast("현재 비밀번호를 입력해 주세요."))
                return@launch
            }

            try {
                setState { copy(loading = true) }

                // 실제 삭제 요청 (재인증 + Firestore 삭제 + Auth 삭제)
                deleteAccountUseCase.invoke(currentPassword)

                // 상태 초기화
                setState {
                    copy(
                        loading = false,
                        showChangePassword = false,
                        showDeleteAccount = false,
                        profile = null,
                        appInfo = null,
                        error = null
                    )
                }

                setEffect(MyPageContract.Effect.Toast("회원탈퇴가 완료되었어요."))

                // 모든 데이터가 사라졌으니 로그인 화면으로 이동
                delay(500)
                setEffect(MyPageContract.Effect.NavigateToLogin)
            } catch (t: Throwable) {
                t.printStackTrace()
                LogUtil.e(LogUtil.MY_PAGE_LOG_TAG, "error : $t")

                setState { copy(loading = false) }
                setEffect(MyPageContract.Effect.Toast("탈퇴 처리 중 오류가 발생했어요."))
            }
        }
    }

    /** 로그아웃 */
    private fun logout() {
        viewModelScope.launch {
            logoutUseCase.invoke()

            // 상태 초기화
            setState {
                copy(
                    loading = false,
                    showChangePassword = false,
                    showDeleteAccount = false,
                    profile = null,
                    appInfo = null,
                    error = null
                )
            }

            // 로그인 화면으로 이동
            setEffect(MyPageContract.Effect.NavigateToLogin)
        }
    }
}