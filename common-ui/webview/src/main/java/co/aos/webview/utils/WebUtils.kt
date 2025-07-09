package co.aos.webview.utils

import android.util.Patterns
import android.webkit.CookieManager
import co.aos.myutils.log.LogUtil
import androidx.core.net.toUri

/**
 * 웹 관련 유틸 모음
 * */
object WebUtils {

    /**
     * 쿠키값 반환!!
     * @param domain url
     * @return 도메인에 해당한 쿠키를 map 으로 반환
     */
    fun getAppCookie(cookieManager: CookieManager,domain: String): HashMap<String, String> {
        val cookiesMap = HashMap<String, String>()
        val cookies = cookieManager.getCookie(domain)

        try{
            val cookie = cookies.split(";")
            for(kv in cookie) {
                val key = kv.split("=".toRegex()).toTypedArray()[0]
                val lastIdx = kv.indexOf("=") + 1
                if (lastIdx <= kv.length) {
                    cookiesMap[key.trim()] = kv.substring(lastIdx)
                }
                LogUtil.d(LogUtil.WEB_COOKIE_LOG, "key : $key, value : ${kv.substring(lastIdx)}")
            }
        }catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(LogUtil.WEB_COOKIE_LOG, "get cookie error : $e")
        }
        return cookiesMap
    }

    /**
     * 쿠키 값 세팅
     * @param domain 쿠키값 세팅 domain
     * @param cookies 쿠키
     */
    private fun setCustomCookie(
        domain: String,
        cookies: Array<String>
    ) {
        if (domain.isBlank() && cookies.isEmpty()) return

        try {
            for (cookie in cookies) {
                CookieManager.getInstance().setCookie(domain, cookie)
            }
            CookieManager.getInstance().flush()
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(LogUtil.WEB_COOKIE_LOG, "set cookie error : $e")
        }
    }

    /**
     * 웹 쿠키 제거
     *
     * - 영구적인 웹 쿠키를 제거할 때 호출(지정 된 만료일 까지 유지하는 쿠키)
     * - 로그아웃, 개인정보 초기화 등 강력한 리셋 시 사용
     * - 앱 내 저장 된 모든 쿠키를 초기화
     * */
    fun clearWebCookie() {
        CookieManager.getInstance().removeAllCookies { success ->
            if (success) {
                CookieManager.getInstance().flush()
            }
        }
    }

    /**
     * 웹 세션 쿠키 제거
     *
     * - 앱 종료 시 사라지는 쿠키를 제거할 때 호출
     * - 로그인 세션 강제 만료 목적으로 많이 사용
     * - 영구 쿠키(만료 시간이 설정 된 쿠키)는 그대로 존속
     * */
    fun clearSessionCookie() {
        CookieManager.getInstance().removeAllCookies { success ->
            if (success) {
                CookieManager.getInstance().flush()
            }
        }
    }

    /** 웹뷰 URL 유효성 검증 */
    fun isSafeUrl(url: String?): Boolean {
        return !url.isNullOrBlank()
                && Patterns.WEB_URL.matcher(url).matches()
                && try {
            val uri = url.toUri()
            uri.scheme in listOf("http", "https") && !uri.host.isNullOrBlank()
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(LogUtil.WEB_VIEW_LOG_TAG, "isSafeUrl() : $e")
            false
        }
    }
}