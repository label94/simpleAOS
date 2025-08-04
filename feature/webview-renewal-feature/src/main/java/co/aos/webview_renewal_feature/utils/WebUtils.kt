package co.aos.webview_renewal_feature.utils

import android.util.Patterns
import androidx.core.net.toUri
import co.aos.myutils.log.LogUtil
import kotlin.collections.contains

/**
 * 웹뷰 관련 유틸 모음
 * */
object WebUtils {

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