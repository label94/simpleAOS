package co.aos.myutils.common

/**
 * 공통 앱내 상수 정의
 * */
object AppConstants {

    /** 서버 도메인 타입 */
    var serverType: AppServerType = AppServerType.SERVER_PRD

    /** 서버 도메인 정의 */
    enum class AppServerType(var url: String, name: String) {
        SERVER_DEV("https://www.ost.co.kr/", "개발서버"),
        SERVER_FILE_UPLOAD_TEST("https://www.w3schools.com/jsref/tryit.asp?filename=tryjsref_fileupload_get", "파일업로드 테스트"),
        SERVER_TO_HOME("https://tohomepartner.thehyundai.com/front/cu/cua/login.do", "현대 백화점 투홈파트너"),
        SERVER_PRD("https://m.naver.com/", "운영서버") // https://[도메인]
    }

    /** 웹뷰 프래그먼트에 url 정보를 전달하는 key */
    const val WEB_LOAD_URL_KEY = "url"
    const val WEB_LOAD_UA_KEY = "ua"
}