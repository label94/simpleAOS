package co.aos.user_feature.login.viewmodel

import android.content.Context
import androidx.lifecycle.viewModelScope
import co.aos.base.BaseViewModel
import co.aos.domain.model.User
import co.aos.domain.usecase.user.AutoLoginCheckUseCase
import co.aos.domain.usecase.user.LoginUseCase
import co.aos.myutils.log.LogUtil
import co.aos.user_feature.login.model.LoginInfoModel
import co.aos.user_feature.login.state.LoginContract
import co.aos.user_feature.utils.UserConst
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import javax.inject.Inject

/**
 * 로그인 관련 ViewModel
 * */
@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val loginUseCase: LoginUseCase,
    private val autoLoginCheckUseCase: AutoLoginCheckUseCase
): BaseViewModel<LoginContract.Event, LoginContract.State, LoginContract.Effect>() {

    init {
        // 자동 로그인 활성화 유무 확인
        isAutoLoginEnable()
    }

    /** 초기 상태 정의 */
    override fun createInitialState(): LoginContract.State {
        return LoginContract.State()
    }

    /** 이벤트 처리 */
    override fun handleEvent(event: LoginContract.Event) {
        when(event) {
            is LoginContract.Event.RequestLogin -> {
                // 로딩 UI 상태
                setState { copy(loginState = LoginContract.LoginState.Loading) }

                // 로그인 요청
                login()
            }
            is LoginContract.Event.MoveUserJoinPage -> {
                // 회원가입 페이지로 이동
                setEffect(LoginContract.Effect.MovePage(UserConst.UserPage.JOIN.pageName))
            }
            is LoginContract.Event.AutoLogin -> {
                // 로딩 UI 상태
                setState { copy(loginState = LoginContract.LoginState.Loading) }

                // 자동 로그인
                autoLogin()
            }
            is LoginContract.Event.UpdateId -> {
                // id 상태 업데이트
                setState { copy(id = event.id) }
            }
            is LoginContract.Event.UpdatePassword -> {
                // password 상태 업데이트
                setState { copy(password = event.password) }
            }
            is LoginContract.Event.UpdateIsAutoLogin -> {
                // 자동 로그인 설정 값 업데이트
                updateIsAutoLogin(event.isAutoLoginEnable)
            }
        }
    }

    /** 로그인 요청 */
    private fun login() {
        val id = currentState.id
        val password = currentState.password

        if (id.isEmpty() || password.isEmpty()) {
            // snack bar 표시
            setEffect(LoginContract.Effect.ShowSnackBar("아이디와 비밀번호를 입력해주세요."))

            // 비 로그인 상태로 표시
            setState { copy(loginState = LoginContract.LoginState.LoginOut) }
            return
        }

        viewModelScope.launch {
            // 딜레이 추가
            delay(500)

            val user = loginUseCase.invoke(id, password)
            updateLoginStateInfo(user)

            // 입력한 값 초기화
            setState { copy(id = "", password = "") }
        }
    }

    /** 자동 로그인 */
    private fun autoLogin() {
        viewModelScope.launch {
            // 딜레이 추가
            delay(500)

            val user = loginUseCase.invoke()
            updateLoginStateInfo(user)
        }
    }

    /** 로그인 상태 업데이트 */
    private fun updateLoginStateInfo(user: User?) {
        if (user != null) {
            // 로그인 상태로 업데이트
            setState { copy(loginState = LoginContract.LoginState.Login(user)) }

            // 로그인 상태 저장
            val loginInfoData = LoginInfoModel(
                id = user.id,
                password = user.password,
                nickname = user.nickname,
                profileImagePath = user.profileImagePath
            )

            // 로그인 완료
            setEffect(LoginContract.Effect.LoginComplete(loginInfo = loginInfoData))
        } else {
            // 비 로그인 상태로 업데이트
            setState { copy(loginState = LoginContract.LoginState.LoginOut) }

            // 로그인 실패 snack bar 표시
            setEffect(LoginContract.Effect.ShowSnackBar("로그인에 실패하였습니다."))
        }
    }

    /** 자동 로그인 관련 설정 상태 업데이트 */
    private fun updateIsAutoLogin(isAutoLogin: Boolean) {
        viewModelScope.launch {
            // 설정 상태 업데이트
            setState { copy(isAutoLoginEnable = isAutoLogin) }

            // 로컬에도 값을 업데이트
            saveIsAutoLoginEnable()
        }
    }

    /** 자동 로그인 활성화 유무 확인 */
    private fun isAutoLoginEnable() {
        viewModelScope.launch {
            val isAutoLoginEnable = autoLoginCheckUseCase.invoke()
            LogUtil.d(LogUtil.LOGIN_LOG_TAG, "isAutoLoginEnable : $isAutoLoginEnable")

            // 상태 업데이트
            setState { copy(isAutoLoginEnable = isAutoLoginEnable) }
        }
    }

    /** 자동 로그인 설정 값 로컬에 저장 */
    private suspend fun saveIsAutoLoginEnable() {
        val isAutoLoginEnable = currentState.isAutoLoginEnable
        // 설정 상태 업데이트
        autoLoginCheckUseCase.invoke(isAutoLoginEnable)
    }
}