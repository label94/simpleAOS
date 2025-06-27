package co.aos.network

/**
 * Api 설정 관련 Manager
 * */
object ApiManager {
    private const val TIMEOUT = 10 * 1000L // 타임아웃 관련 상수
    private const val USER_AGENT = "User-agent"

    // base url
    var baseUrl: String? = null

    // 타임아웃
    var timeOutMilliSec = TIMEOUT

    // 헤더 정보
    var header = mutableMapOf<String, String>()

    // 서버 쿠키 관련
    val cookie: ApiCookieJar = ApiCookieJar()

    /** 서버 연동 관련 기본 정보 설정 */
    fun initialize(
        baseUrl: String? = null,
        userAgent: String? = null,
        header: MutableMap<String, String>? = null
    ) {
        baseUrl?.let {
            this.baseUrl = it
        }
        header?.let {
            this.header.putAll(it)
        }
        userAgent?.let {
            this.header[USER_AGENT] = it
        }
    }
}