package co.aos.webview_feature.presentation.viewmodel

import android.content.Context
import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import androidx.lifecycle.viewModelScope
import co.aos.base.BaseViewModel
import co.aos.domain.usecase.GetWebViewConfigUseCase
import co.aos.domain.usecase.RequestFileChooserUseCase
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
    private val getWebViewConfigUseCase: GetWebViewConfigUseCase,
    private val requestFileChooserUseCase: RequestFileChooserUseCase
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

    /** should Override Loading */
    private fun shouldOverrideLoading(url: String?) {

    }

    /** 파일 탐색기 열기 */
    private fun showFileChooser(
        filePathCallback: ValueCallback<Array<out Uri?>?>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    ) {
        // 파일 탐색기 관련 콜백 설정
        requestFileChooserUseCase.onShowFileChooser(
            filePathCallback = filePathCallback,
            fileChooserParams = fileChooserParams
        )

        // 파일 탐색기 열기 위한 Effect 실행
        setEffect(WebViewContract.Effect.LaunchFileChooser)
    }

    /** 파일 선택 후 처리 */
    private fun fileChooserResult(uris: List<Uri?>?) {
        requestFileChooserUseCase.onFileChosen(uris)
    }

    /** 초기 상태 설정 */
    override fun createInitialState(): WebViewContract.State {
        return WebViewContract.State()
    }

    /** 이벤트 처리를 위한 핸들러 */
    override fun handleEvent(event: WebViewContract.Event) {
        when(event) {
            is WebViewContract.Event.InitWebViewConfig -> {
                // 웹뷰 초기 셋팅
                initWebViewSetting()
            }
            is WebViewContract.Event.ShouldOverrideLoading -> {
                // 웹뷰 내 페이지 전환 될 때 호출
                shouldOverrideLoading(event.url)
            }
            is WebViewContract.Event.ShowFileChooser -> {
                // 웹뷰 내 파일 탐색기 오픈
                showFileChooser(event.filePathCallback, event.fileChooserParams)
            }
            is WebViewContract.Event.FileChooserResult -> {
                // 선택한 파일을 웹으로 전송
                fileChooserResult(event.uris)
            }
        }
    }
}