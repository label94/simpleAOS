package co.aos.myjetpack.intro.viewmodel

import androidx.lifecycle.viewModelScope
import co.aos.base.BaseViewModel
import co.aos.local.pref.SharedPreferenceManager
import co.aos.local.pref.consts.SharedConstants
import co.aos.myjetpack.intro.state.IntroContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 인트로 관련 뷰모델
 * */
@HiltViewModel
class IntroViewModel @Inject constructor(
    private val sharedPreferenceManager: SharedPreferenceManager
): BaseViewModel<IntroContract.Event, IntroContract.State, IntroContract.Effect>() {

    init {
        viewModelScope.launch {
            // 앱 실행 시 최초 실행 유무인지 확인 후, 최초실행이 아닌 경우에는 바로 푸시 권한 체크
            // 최초 실행 경우에는 "접근권한" 안내 화면에서 푸시 권한을 체크하기 때문에, 분기 처리 필요!
            val isFirstLaunch = isFirstLaunch()
            if (!isFirstLaunch) {
                setEffect(IntroContract.Effect.CheckNotificationPermission)
            }

            // 3초 동안 스플래시 실행
            delay(3000)

            // 상태 업데이트 이벤트 요청
            setEvent(IntroContract.Event.UpdateIsFirstLaunch(isFirstLaunch))
            setEvent(IntroContract.Event.UpdateIsShowSplash(false))
        }
    }

    /** 초기 상태 설정 */
    override fun createInitialState(): IntroContract.State {
        return IntroContract.State()
    }

    /** 이벤트 제어 */
    override fun handleEvent(event: IntroContract.Event) {
        when(event) {
            is IntroContract.Event.UpdateNotificationPermission -> {
                setState {
                    copy(isNotificationPermission = event.isGranted)
                }
            }
            is IntroContract.Event.UpdateIsShowSplash -> {
                setState { copy(isShowSplash = event.isShow) }
            }
            is IntroContract.Event.UpdateIsFirstLaunch -> {
                setState { copy(isFirstLaunch = event.isFirstLaunch) }
            }
            is IntroContract.Event.OnBackPressed -> {
                setEffect(IntroContract.Effect.FinishActivity)
            }
            is IntroContract.Event.OnNextStep -> {
                // 알람 권한 체크
                setEffect(IntroContract.Effect.CheckNotificationPermission)

                // 상태 업데이트
                setEvent(IntroContract.Event.UpdateIsFirstLaunch(false))

                // local 저장소 업데이트
                sharedPreferenceManager.setBoolean(SharedConstants.KEY_IS_FIRST_LAUNCH, false)
            }
        }
    }

    /** 최초 1회 실행 유무 확인 */
    private fun isFirstLaunch(): Boolean {
        return sharedPreferenceManager.getBoolean(SharedConstants.KEY_IS_FIRST_LAUNCH, true)
    }
}