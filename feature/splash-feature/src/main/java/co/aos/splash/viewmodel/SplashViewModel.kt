package co.aos.splash.viewmodel

import androidx.lifecycle.viewModelScope
import co.aos.base.BaseViewModel
import co.aos.domain.usecase.IsAppFirstRunUseCase
import co.aos.domain.usecase.user.renewal.GetCurrentUserUseCase
import co.aos.domain.usecase.user.renewal.IsAutoLoginUseCase
import co.aos.myutils.log.LogUtil
import co.aos.splash.state.SplashContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 스플래시 관련 ViewModel */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val isAppFirstRunUseCase: IsAppFirstRunUseCase,
    private val currentUserUseCase: GetCurrentUserUseCase,
    private val isAutoLoginUseCase: IsAutoLoginUseCase,
): BaseViewModel<SplashContract.Event, SplashContract.State, SplashContract.Effect>() {

    init {
        setEvent(SplashContract.Event.InitSettingLoad)
    }

    /** 초기 상태 설정 */
    override fun createInitialState(): SplashContract.State {
        return SplashContract.State()
    }

    /** 이벤트 처리 */
    override fun handleEvent(event: SplashContract.Event) {
        when(event) {
            is SplashContract.Event.InitSettingLoad -> {
                checkIsAppFirstRun()
            }
            is SplashContract.Event.MoveLoginPage -> {
                setEffect(SplashContract.Effect.MoveLoginPage)
            }
            is SplashContract.Event.MoveGuidePage -> {
                setEffect(SplashContract.Effect.MoveGuidePage)
            }
            is SplashContract.Event.MoveHomePage -> {
                setEffect(SplashContract.Effect.MoveHomePage)
            }
        }
    }

    /** 최초 앱 실행인지 확인 */
    private fun checkIsAppFirstRun() {
        viewModelScope.launch {
            val isFirstAppRun = isAppFirstRunUseCase.invoke()
            LogUtil.d(LogUtil.SPLASH_LOG_TAG, "isFirstAppRun : $isFirstAppRun")

            // 상태 업데이트
            setState { copy(isFirstLaunch = isFirstAppRun) }

            if (!currentState.isShowSplash) {
                // 스플래시 미 표시 경우 바로 로그인 화면 이동 이벤트 실행
                setEvent(SplashContract.Event.MoveLoginPage)
            } else {
                // 3초 동안 스플래시 실행
                delay(3000)

                // 앱 최초로 한번이라도 실행 되면 로그인 화면으로 이동
                // 그 외 접근권한 화면으로 이동
                if (isFirstAppRun) {
                    if (isAutoLoginUseCase.invoke()) {
                        // 자동 로그인이 활성화 된 상태 + 로그인 세션이 남아 있을 경우에만 홈으로 이동
                        if (currentUserUseCase.invoke() != null) {
                            setEvent(SplashContract.Event.MoveHomePage)
                        } else {
                            setEvent(SplashContract.Event.MoveLoginPage)
                        }
                    } else {
                        // 그 외 로그인 화면으로 이동
                        setEvent(SplashContract.Event.MoveLoginPage)
                    }
                } else {
                    setEvent(SplashContract.Event.MoveGuidePage)
                }
            }
        }
    }
}