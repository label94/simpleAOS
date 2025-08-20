package co.aos.myjetpack.sub

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import co.aos.myjetpack.web.screen.SubWebViewScreen
import co.aos.myutils.log.LogUtil
import co.aos.network_error_feature.viewmodel.NetworkStatusViewModel
import co.aos.ui.theme.MyJetpackTheme
import co.aos.webview_renewal_feature.consts.WebConstants
import co.aos.webview_renewal_feature.state.WebViewContract
import co.aos.webview_renewal_feature.viewmodel.WebViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

/**
 * 서브 웹 액티비티
 * */
@AndroidEntryPoint
class SubWebActivity : ComponentActivity() {

    // 네트워크 뷰모델
    private val networkStatusViewModel: NetworkStatusViewModel by viewModels()

    // 웹뷰 뷰모델
    private val webViewModel: WebViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtil.d(LogUtil.WEB_VIEW_LOG_TAG, "SubWebActivity onCreate()")

        // init
        initWebViewUrlSetting(intent)

        // UI
        setContent {
            MyJetpackTheme {
                SubWebViewScreen(
                    webViewModel = webViewModel,
                    networkStatusViewModel = networkStatusViewModel
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        LogUtil.d(LogUtil.WEB_VIEW_LOG_TAG, "SubWebActivity onNewIntent()")

        // 웹뷰 URL 관련 처리
        initWebViewUrlSetting(intent)
    }

    /** 웹뷰 init */
    private fun initWebViewUrlSetting(intent: Intent?) {
        val url = intent?.getStringExtra(WebConstants.MOVE_URL)
        LogUtil.d(LogUtil.WEB_VIEW_LOG_TAG, "sub web url : $url")

        if (!url.isNullOrEmpty()) {
            webViewModel.setEvent(WebViewContract.Event.LoadWebViewUrl(url))
        }
    }
}