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
        SERVER_PRD("https://m.naver.com/", "운영서버") // https://[도메인]
    }
}