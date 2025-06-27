package co.aos.network

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * 서버와의 통신 중에 쿠키를 저장하고 관리하기 위함
 * */
class ApiCookieJar : CookieJar {
    private val cookieManager = android.webkit.CookieManager.getInstance()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        for (cookie in cookies) {
            cookieManager.setCookie(url.toString(), cookie.toString())
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        mutableListOf<Cookie>().apply {
            cookieManager.getCookie(url.toString())?.let { cookieManagerCookies ->
                cookieManagerCookies.split(";").run {
                    forEach { it ->
                        Cookie.parse(url, it.trim())?.let { cookie ->
                            add(cookie)
                        }
                    }
                }
            }
            return this
        }
    }
}