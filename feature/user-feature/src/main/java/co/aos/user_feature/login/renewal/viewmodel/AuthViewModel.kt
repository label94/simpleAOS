package co.aos.user_feature.login.renewal.viewmodel

import androidx.lifecycle.viewModelScope
import co.aos.base.BaseViewModel
import co.aos.domain.usecase.user.renewal.EnableAutoLoginUseCase
import co.aos.domain.usecase.user.renewal.EnableIsSaveIdUseCase
import co.aos.domain.usecase.user.renewal.GetLoginIdUseCase
import co.aos.domain.usecase.user.renewal.IsSaveIdUseCase
import co.aos.domain.usecase.user.renewal.SetLoginIdUseCase
import co.aos.domain.usecase.user.renewal.SignInGoogleLoginUseCase
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
    private val enableIsSaveIdUseCase: EnableIsSaveIdUseCase,
    private val isSaveIdUseCase: IsSaveIdUseCase,
    private val setLoginIdUseCase: SetLoginIdUseCase,
    private val getLoginIdUseCase: GetLoginIdUseCase,
    private val enableAutoLoginUseCase: EnableAutoLoginUseCase,
    private val googleLoginUseCase: SignInGoogleLoginUseCase
) : BaseViewModel<AuthContract.Event, AuthContract.State, AuthContract.Effect>() {

    init {
        initData()
    }

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
            is AuthContract.Event.UpdateIsSaveId -> {
                saveId(event.isSaveIdChecked)
            }
            is AuthContract.Event.UpdateIsAutoLogin -> {
                setState { copy(isAutoLoginChecked = event.isAutoLoginChecked) }
            }
            is AuthContract.Event.ClickGoogleSignIn -> {
                handleGoogleSignIn()
            }
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
                // 로그인 성공 시 자동 로그인 flag가 활성화 된 상태인 경우에만 저장
                if (currentState.isAutoLoginChecked) {
                    enableAutoLoginUseCase.invoke(true)
                }

                setEffect(AuthContract.Effect.LoginSuccess(it))
            }.onFailure {
                it.printStackTrace()
                LogUtil.e(LogUtil.LOGIN_LOG_TAG, "login error : $it")

                setEffect(AuthContract.Effect.ShowSnackBar("로그인 실패했습니다."))
            }
        }
    }

    /** id 저장 관련 활성화 처리 */
    private fun saveId(isSaveId: Boolean) {
        setState { copy(isSaveIdChecked = isSaveId) }
        enableIsSaveIdUseCase.invoke(isSaveId)

        if (isSaveId) {
            setLoginIdUseCase.invoke(currentState.id)
        } else {
            setLoginIdUseCase.invoke("")
        }
    }

    /** 초기 init */
    private fun initData() {
        val isSaveId = isSaveIdUseCase.invoke()
        val loginId = getLoginIdUseCase.invoke()
        LogUtil.d(LogUtil.LOGIN_LOG_TAG, "initData => isSaveId : $isSaveId , loginId : $loginId")

        // preference에 저장 된 id가 존재할 경우에만 업데이트 이벤트 실행
        if (isSaveId && loginId.isNotEmpty()) {
            setEvent(AuthContract.Event.UpdateID(loginId))
            setEvent(AuthContract.Event.UpdateIsSaveId(true))
        }
    }

    /** 구글 로그인 처리 */
    private fun handleGoogleSignIn() {
        setState { copy(isLoading = true) }

        viewModelScope.launch {
            delay(500)

            val result = runCatching { googleLoginUseCase.invoke() }
            setState { copy(isLoading = false) }

            result.onSuccess { user ->
                if (user != null) {
                    // 로그인 성공 시 자동 로그인 flag가 활성화 된 상태인 경우에만 저장
                    if (currentState.isAutoLoginChecked) {
                        enableAutoLoginUseCase.invoke(true)
                    }

                    setEffect(AuthContract.Effect.LoginSuccess(user = user))
                } else {
                    setEffect(AuthContract.Effect.ShowSnackBar("구글 로그인 실패했습니다."))
                }
            }.onFailure { e->
                e.printStackTrace()
                LogUtil.e(LogUtil.GOOGLE_LOGIN_LOG_TAG, "google login error : $e")
                setEffect(AuthContract.Effect.ShowSnackBar("구글 로그인 실패했습니다."))
            }
        }
    }
}