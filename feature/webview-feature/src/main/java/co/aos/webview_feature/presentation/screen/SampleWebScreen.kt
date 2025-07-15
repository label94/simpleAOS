package co.aos.webview_feature.presentation.screen

import android.view.View
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import co.aos.myutils.log.LogUtil
import co.aos.webview.BaseWebViewFragment
import co.aos.webview_feature.presentation.state.WebViewContract
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
    val effectFlow = viewModel.effect

    // 파일 선택 결과 전달
    val fileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        LogUtil.i(LogUtil.WEB_VIEW_LOG_TAG, "fileLauncher : $uris")

        // 선택한 파일 Uri를 웹으로 보내는 이벤트 실행
        viewModel.setEvent(WebViewContract.Event.FileChooserResult(uris))
    }

    // 런타임 시 동적으로 ID를 생성
    val containerId = remember { View.generateViewId() }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { con ->
            FrameLayout(con).apply {
                // 프래그먼트 추가를 위한 동적 id 지정
                id = containerId

                // 웹뷰 프래그먼트 추가
                post {
                    val fragmentManager = (activity as? FragmentActivity)?.supportFragmentManager
                    val tag = "BaseWebViewFragment"

                    if (fragmentManager?.findFragmentByTag(tag) == null) {
                        val fragment = BaseWebViewFragment().apply {
                            // 웹뷰 초기 URL 및 UA 설정
                            arguments = bundleOf(
                                AppConstants.WEB_LOAD_URL_KEY to uiState.webViewConfig.url,
                                AppConstants.WEB_LOAD_UA_KEY to uiState.webViewConfig.userAgent
                            )

                            // 웹뷰 로드 시작
                            onPageStartedCallback = { url, bitmap ->

                            }

                            // 웹뷰 로드 종료
                            onPageFinishedCallback = {}

                            // 웹뷰 오류
                            onReceivedErrorCallback = { request, error -> }

                            // 웹뷰 내 페이지 전환
                            shouldOverrideUrlLoadingCallback = { url ->
                                viewModel.setEvent(WebViewContract.Event.ShouldOverrideLoading(url))
                                false
                            }

                            // 파일 탐색기 열기
                            onShowFileChooserCallback = { filePathCallback, fileChooserParams ->
                                viewModel.setEvent(WebViewContract.Event.ShowFileChooser(filePathCallback, fileChooserParams))
                                true
                            }
                        }

                        // fragment commit
                        fragmentManager?.beginTransaction()?.replace(containerId, fragment, tag)?.commit()
                    }
                }
            }
        }
    )

    /** 1회성 이벤트 감지 */
    LaunchedEffect(key1 = effectFlow) {
        effectFlow.collect { effect ->
            when(effect) {
                is WebViewContract.Effect.LaunchFileChooser -> {
                    LogUtil.i(LogUtil.WEB_VIEW_LOG_TAG, "LaunchFileChooser")

                    // 이미지 형식만 표시하는 탐색기 호출
                    fileLauncher.launch("image/*")
                }
            }
        }
    }

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