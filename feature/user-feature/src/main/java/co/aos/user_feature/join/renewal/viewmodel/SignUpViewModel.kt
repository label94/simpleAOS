package co.aos.user_feature.join.renewal.viewmodel

import androidx.lifecycle.viewModelScope
import co.aos.base.BaseViewModel
import co.aos.domain.usecase.user.renewal.CheckNicknameAvailableUseCase
import co.aos.domain.usecase.user.renewal.CheckUserIdAvailableUseCase
import co.aos.domain.usecase.user.renewal.SignUpUseCase
import co.aos.myutils.log.LogUtil
import co.aos.user_feature.join.renewal.state.SignUpContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 회원가입 관련 뷰모델 */
@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val checkNickNameUseCase: CheckNicknameAvailableUseCase,
    private val checkIdUseCase: CheckUserIdAvailableUseCase,
): BaseViewModel<SignUpContract.Event, SignUpContract.State, SignUpContract.Effect>() {

    /** 초기 상태 설정 */
    override fun createInitialState(): SignUpContract.State {
        return SignUpContract.State()
    }

    /** 이벤트 제어 */
    override fun handleEvent(event: SignUpContract.Event) {
        when(event) {
            is SignUpContract.Event.InitData -> {
                setState {
                    copy(
                        id = "",
                        password = "",
                        nickName = "",
                        nickNameAvailable = false,
                        isIdAvailable = false,
                        isPasswordVisible = false,
                        localProfileImgCode = 0,
                    )
                }
            }
            is SignUpContract.Event.UpdateID -> {
                setState {
                    copy(id = event.id)
                }
            }
            is SignUpContract.Event.UpdatePassword -> {
                setState {
                    copy(password = event.password)
                }
            }
            is SignUpContract.Event.UpdatePasswordCheck -> {
                setState {
                    copy(isPasswordVisible = event.isPasswordCheck)
                }
            }
            is SignUpContract.Event.UpdateNickName -> {
                setState {
                    copy(nickName = event.nickName)
                }
            }
            is SignUpContract.Event.UpdateLocalProfileImgCode -> {
                setState {
                    copy(localProfileImgCode = event.code)
                }
            }
            is SignUpContract.Event.ClickToNickName -> {
                checkNickName()
            }
            is SignUpContract.Event.ClickSignUp -> {
                handleSignUp()
            }
            is SignUpContract.Event.ClickToIdCheck -> {
                checkId()
            }
            is SignUpContract.Event.ShowProfilePicker -> {
                setState { copy(isShowProfilePicker = true) }
            }
            is SignUpContract.Event.HideProfilePicker -> {
                setState { copy(isShowProfilePicker = false) }
            }
        }
    }

    /** 닉네임 중복 체크 */
    private fun checkNickName() {
        viewModelScope.launch {
            val isAvailable = runCatching {
                checkNickNameUseCase.invoke(currentState.nickName)
            }.getOrElse {
                it.printStackTrace()
                LogUtil.e(LogUtil.JOIN_LOG_TAG, "error => $it")

                setEffect(SignUpContract.Effect.ShowSnackBar("닉네임 확인 실패"))
                return@launch
            }

            setState { copy(nickNameAvailable = isAvailable) } // 상태 업데이트
            if (isAvailable) {
                setEffect(SignUpContract.Effect.ShowSnackBar("닉네임 사용 가능"))
            } else {
                setEffect(SignUpContract.Effect.ShowSnackBar("닉네임 사용 불가능"))
            }
        }
    }

    /** 회원가입 */
    private fun handleSignUp() {
        val id = currentState.id
        val password = currentState.password
        val nickName = currentState.nickName
        val localProfileImgCode = currentState.localProfileImgCode

        if (id.isEmpty()) {
            setEffect(SignUpContract.Effect.ShowSnackBar("아이디를 입력해주세요"))
            return
        }

        if (password.isEmpty()) {
            setEffect(SignUpContract.Effect.ShowSnackBar("비밀번호를 입력해주세요"))
            return
        }

        if (nickName.isEmpty()) {
            setEffect(SignUpContract.Effect.ShowSnackBar("닉네임을 입력해주세요"))
            return
        }

        viewModelScope.launch {
            setState { copy(isLoading = true) }
            delay(500)

            val result = runCatching {
                signUpUseCase.invoke(id, password, nickName, localProfileImgCode)
            }

            setState { copy(isLoading = false) }
            result.onSuccess {
                setEffect(SignUpContract.Effect.SignUpSuccess)
            }.onFailure {
                it.printStackTrace()
                LogUtil.e(LogUtil.JOIN_LOG_TAG, "error => $it")

                setEffect(SignUpContract.Effect.ShowSnackBar("회원가입 실패"))
            }
        }
    }

    /** id 유효성 체크 */
    private fun checkId() {
        val id = currentState.id.trim()
        if (id.isEmpty()) {
            setEffect(SignUpContract.Effect.ShowSnackBar("아이디를 입력해주세요"))
            return
        }
        if (!id.contains("@")) {
            setEffect(SignUpContract.Effect.ShowSnackBar("이메일 형식으로 작성하세요."))
            return
        }

        viewModelScope.launch {
            val isAvailable = runCatching {
                checkIdUseCase.invoke(id)
            }.getOrElse {
                it.printStackTrace()
                LogUtil.e(LogUtil.JOIN_LOG_TAG, "id check error => $it")
                return@launch
            }

            setState { copy(isIdAvailable = isAvailable) } // 상태 업데이트
            if (isAvailable) {
                setEffect(SignUpContract.Effect.ShowSnackBar("사용 가능한 아이디입니다"))
            } else {
                setEffect(SignUpContract.Effect.ShowSnackBar("이미 존재하는 아이디입니다"))
            }
        }
    }
}