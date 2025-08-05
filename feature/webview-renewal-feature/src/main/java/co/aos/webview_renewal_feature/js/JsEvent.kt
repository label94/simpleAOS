package co.aos.webview_renewal_feature.js

/**
 * JS Event 처리
 * */
sealed class JsEvent {
    /** 화면 닫기 */
    data object OnJsClose : JsEvent()

    /** 서브 웹뷰 열기 */
    data class SubWebViewOpen(val url: String) : JsEvent()

    /** 외부 브라우저 열기 */
    data class OpenWebBrowser(val url: String) : JsEvent()

    /** 앱 설정 메뉴 열기 */
    data object OpenAppSetting: JsEvent()
}