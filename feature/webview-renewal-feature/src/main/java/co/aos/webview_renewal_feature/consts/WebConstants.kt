package co.aos.webview_renewal_feature.consts

/**
 * 웹뷰 관련 상수 정의
 * */
object WebConstants {

    /** 서버 도메인 타입 */
    var webServerType: WebServerType = WebServerType.SERVER_DEV

    /** 서버 도메인 정의 */
    enum class WebServerType(var url: String, name: String) {
        SERVER_DEV("https://www.google.com/", "개발서버"),
        SERVER_FILE_UPLOAD_TEST("https://www.w3schools.com/jsref/tryit.asp?filename=tryjsref_fileupload_get", "파일업로드 테스트"),
        SERVER_PRD("https://m.naver.com/", "운영서버") //
    }

    /** 자바스크립트인터페이스 브릿지 이름 */
    const val JS_BRIDGE_NAME = "AndroidBridge"

    /** 웹 스킴 명 */
    const val WEB_SCHEME_NAME = "jetpack"

    /** 액티비티 이동 시 보내는 URL Key */
    const val MOVE_URL = "MOVE_URL"
}