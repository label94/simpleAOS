package co.aos.webview.utils

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * 웹뷰 내 ui 관련 상호작용을 위한 인터페이스
 * */
interface WebViewUiListener {

    /** 웹뷰 스와이프 갱신 되었을 때 처리 하기 위함 */
    fun swipeRefresh(swipeRefreshLayout: SwipeRefreshLayout)
}