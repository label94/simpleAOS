package co.aos.myjetpack

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import co.aos.base.BaseActivity
import co.aos.base.utils.moveActivity
import co.aos.myjetpack.sub.SubWebActivity
import co.aos.myjetpack.ui.theme.MyJetpackTheme
import co.aos.myjetpack.web.screen.MainWebViewScreen
import co.aos.myutils.common.AppConstants
import co.aos.myutils.log.LogUtil
import co.aos.network_error_feature.viewmodel.NetworkStatusViewModel
import co.aos.webview_renewal_feature.consts.WebConstants
import co.aos.webview_renewal_feature.state.WebViewContract
import co.aos.webview_renewal_feature.viewmodel.WebViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

/**
 * Main
 * */
@AndroidEntryPoint
class MainActivity : BaseActivity() {

    // 웹뷰 관련 뷰모델
    private val webViewModel: WebViewModel by viewModels()

    // 네트워크 상태 관련 뷰모델
    private val networkStatusViewModel: NetworkStatusViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 웹뷰 관련 설정
        initWebViewUrlSetting(intent)

        // UI
        setContent {
            MyJetpackTheme {
                MainWebViewScreen(
                    webViewModel = webViewModel,
                    networkStatusViewModel = networkStatusViewModel,
                    openSubWeb = { url ->
                        // 서브 웹뷰 연동
                        moveActivity(SubWebActivity::class.java, WebConstants.MOVE_URL, url)
                    }
                )
            }
        }
    }

    /** 초기 웹뷰 로드 URL 설정 */
    private fun initWebViewUrlSetting(intent: Intent?) {
        // 인텐트에 전달되는 URL 정보가 있는지 확인
        var intentUrl = intent?.data?.toString() ?: intent?.getStringExtra(WebConstants.MOVE_URL)
        if (intentUrl.isNullOrEmpty()) {
            intentUrl = AppConstants.serverType.url
        }
        LogUtil.d(LogUtil.DEFAULT_TAG, "intentUrl : $intentUrl")

        // 로드할 웹뷰 URL 상태 업데이트 이벤트 요청
        webViewModel.setEvent(WebViewContract.Event.LoadWebViewUrl(intentUrl))
    }

    /**
     * onNewIntent 호출 되었을 때 처리
     * */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        LogUtil.d(LogUtil.DEFAULT_TAG, "onNewIntent : ${intent.toString()}")

        // intent 에 전달되는 url 이 있으면 그 url로 로드하기 위한 절차 수행
        initWebViewUrlSetting(intent)
    }
}