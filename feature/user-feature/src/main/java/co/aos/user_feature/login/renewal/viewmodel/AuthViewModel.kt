package co.aos.user_feature.login.renewal.viewmodel

import androidx.lifecycle.viewModelScope
import co.aos.base.BaseViewModel
import co.aos.domain.usecase.user.renewal.SignInUseCase
import co.aos.myutils.log.LogUtil
import co.aos.user_feature.login.renewal.state.AuthContract
import co.aos.user_feature.utils.UserConst
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 인증 관련 ViewModel
 * */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
) : BaseViewModel<AuthContract.Event, AuthContract.State, AuthContract.Effect>() {

    /** 초기 상태 정의 */
    override fun createInitialState(): AuthContract.State {
        return AuthContract.State()
    }

    /** 이벤트 처리 */
    override fun handleEvent(event: AuthContract.Event) {
        when(event) {
            is AuthContract.Event.UpdateID -> { setState { copy(id = event.id) } }
            is AuthContract.Event.UpdatePassword -> { setState { copy(password = event.password) } }
            is AuthContract.Event.ClickSignIn -> {
                handleSignIn()
            }
            is AuthContract.Event.MoveToSignUpScreen -> { setEffect(AuthContract.Effect.MovePage(UserConst.UserPage.JOIN.pageName)) }
        }
    }

    /** 로그인 */
    private fun handleSignIn() {
        val id = currentState.id
        val password = currentState.password

        if (id.isEmpty() || password.isEmpty()) {
            setEffect(AuthContract.Effect.ShowSnackBar("아이디와 비밀번호를 입력해주세요."))
            return
        }

        setState { copy(isLoading = true) }

        viewModelScope.launch {
            delay(500) // 임의의 딜레이 추가

            val result = runCatching { signInUseCase(id, password) }
            setState { copy(isLoading = false) }
            result.onSuccess {
                setEffect(AuthContract.Effect.LoginSuccess(it))
            }.onFailure {
                it.printStackTrace()
                LogUtil.e(LogUtil.LOGIN_LOG_TAG, "login error : $it")

                setEffect(AuthContract.Effect.ShowSnackBar("로그인 실패했습니다."))
            }
        }
    }
}