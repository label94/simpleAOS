package co.aos.webview_feature.presentation.screen

import android.view.View
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import co.aos.myutils.common.AppConstants
import co.aos.webview.BaseWebViewFragment
import co.aos.webview.utils.BaseWebChromeClient
import co.aos.webview_feature.presentation.viewmodel.WebViewModel

/**
 * 샘플 용 웹뷰 화면
 * */
@Composable
fun SampleWebScreen(
    modifier: Modifier = Modifier,
    viewModel: WebViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val activity = LocalActivity.current
    val fmActivity = activity as? FragmentActivity

    // 런타임 시 동적으로 ID를 생성
    val containerId = remember { View.generateViewId() }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { con ->
            FrameLayout(con).apply {
                id = containerId

                // 웹뷰 프래그먼트 추가
                val fm = (context as FragmentActivity).supportFragmentManager
                if (fm.findFragmentByTag("BaseWebViewFragment") == null) {
                    fm.beginTransaction().replace(containerId, BaseWebViewFragment().apply {
                        arguments = bundleOf(
                            AppConstants.WEB_LOAD_URL_KEY to uiState.webViewConfig.url,
                            AppConstants.WEB_LOAD_UA_KEY to uiState.webViewConfig.userAgent
                        )
                    }, "BaseWebViewFragment").commit()
                }
            }
        }
    )

    // 뒤로가기 핸들링
    BackHandler {
        val fragment = (fmActivity?.supportFragmentManager?.findFragmentByTag("BaseWebViewFragment")) as? BaseWebViewFragment
        fragment?.let {
            if (!it.goBack()) {
                activity.finish()
            }
        } ?: activity?.finish()
    }
}