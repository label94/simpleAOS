package co.aos.network_error_feature.viewmodel

import androidx.lifecycle.viewModelScope
import co.aos.base.BaseViewModel
import co.aos.domain.usecase.NetworkStatusUseCase
import co.aos.myutils.log.LogUtil
import co.aos.network_error_feature.model.toPresentation
import co.aos.network_error_feature.state.NetworkStatusContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 네트워크 연결 상태 확인을 위한 뷰모델
 * */
@HiltViewModel
class NetworkStatusViewModel @Inject constructor(
    private val networkStatusUseCase: NetworkStatusUseCase
): BaseViewModel<NetworkStatusContract.Event, NetworkStatusContract.State, NetworkStatusContract.Effect>() {

    init {
        observeNetworkStatus()
    }

    /** 초기 상태 */
    override fun createInitialState(): NetworkStatusContract.State {
        return NetworkStatusContract.State()
    }

    /** event 처리 */
    override fun handleEvent(event: NetworkStatusContract.Event) {
        when(event) {
            is NetworkStatusContract.Event.UpdateNetworkErrorScreen -> {
                updateNetworkErrorScreen(event.isShow)
            }
            is NetworkStatusContract.Event.ReloadWebView -> {
                reloadWebView()
            }
        }
    }

    /** 네트워크 상태 체크 */
    private fun observeNetworkStatus() {
        viewModelScope.launch {
            networkStatusUseCase.invoke().collect { network ->
                LogUtil.d(LogUtil.NET_STATE_LOG_TAG, "observeNetworkStatus() network : $network")

                // 상태 업데이트
                setState {
                    copy(
                        networkStatus = network.toPresentation(),
                        message = if (network.isNetworkConnected) "네트워크 연결 성공" else "네트워크 연결 실패"
                    )
                }

                // 네트워크 미 연결 된 경우 연결 에러 UI 표시하는 상태로 업데이트
                if (!currentState.networkStatus.isNetworkConnected) {
                    setEvent(NetworkStatusContract.Event.UpdateNetworkErrorScreen(true))
                }
            }
        }
    }

    /** 연결 에러 표시를 결정하는 flag 상태 업데이트 */
    private fun updateNetworkErrorScreen(isShow: Boolean) {
        setState { copy(isShowErrorScreen = isShow) }
    }

    /** 웹뷰 리로드 */
    private fun reloadWebView() {
        val currentNetworkIsConnection = currentState.networkStatus.isNetworkConnected
        LogUtil.d(LogUtil.NET_STATE_LOG_TAG, "reloadWebView currentNetworkIsConnection : $currentNetworkIsConnection")

        // 네트워크 연결이 되었을 경우 웹뷰 리로드
        if (currentNetworkIsConnection) {
            // retry 관련 effect 호출
            setEffect(NetworkStatusContract.Effect.Retry)

            // 네트워크 연결 오류 UI 숨김 처리
            setEvent(NetworkStatusContract.Event.UpdateNetworkErrorScreen(false))
        }
    }
}