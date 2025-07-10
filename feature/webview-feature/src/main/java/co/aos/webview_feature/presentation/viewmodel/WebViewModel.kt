package co.aos.webview_feature.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.viewModelScope
import co.aos.base.BaseViewModel
import co.aos.domain.usecase.GetWebViewConfigUseCase
import co.aos.webview_feature.presentation.model.toPresentation
import co.aos.webview_feature.presentation.state.WebViewContract
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 웹뷰 관련 뷰모델
 * */
@HiltViewModel
class WebViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getWebViewConfigUseCase: GetWebViewConfigUseCase
): BaseViewModel<WebViewContract.Event, WebViewContract.State, WebViewContract.Effect>() {

    init {
        // 초기 웹뷰 설정 이벤트 호출
        setEvent(WebViewContract.Event.InitWebViewConfig)
    }

    /** 초기 웹뷰 설정 */
    private fun initWebViewSetting() {
        viewModelScope.launch {
            val config = (getWebViewConfigUseCase.invoke()).toPresentation()
            setState {
                copy(
                    webViewConfig = config
                )
            }
        }
    }

    /** 초기 상태 설정 */
    override fun createInitialState(): WebViewContract.State {
        return WebViewContract.State()
    }

    /** 이벤트 처리를 위한 핸들러 */
    override fun handleEvent(event: WebViewContract.Event) {
        when(event) {
            is WebViewContract.Event.InitWebViewConfig -> {
                initWebViewSetting()
            }
        }
    }
}