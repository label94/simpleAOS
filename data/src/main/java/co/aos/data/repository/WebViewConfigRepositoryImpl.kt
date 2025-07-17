package co.aos.data.repository

import co.aos.data.entity.WebViewConfigEntity
import co.aos.domain.model.WebViewConfig
import co.aos.domain.repository.WebViewConfigRepository
import co.aos.myutils.common.AppConstants
import co.aos.myutils.log.LogUtil
import javax.inject.Inject

/**
 * 웹뷰 설정 관련 Repository 구현체
 * */
class WebViewConfigRepositoryImpl @Inject constructor(

): WebViewConfigRepository {

    /** userAgent 생성 */
    private fun createUserAgent(): String {
        val userAgent = LinkedHashMap<String, String>().apply {
            put("APP", "Y")
            put("DTYPE", "A")
        }
        LogUtil.d(LogUtil.DEFAULT_TAG, "userAgent => $userAgent")

        if(userAgent.isNotEmpty()) {
            val sb = StringBuilder()
            val keys = userAgent.keys.iterator()

            while (keys.hasNext()) {
                val key = keys.next()
                sb.append(key).append("=").append(userAgent[key])

                if (keys.hasNext()) {
                    sb.append(";")
                }
            }
            return sb.toString()
        }
        return "APP=Y;DTYPE=A"
    }

    override suspend fun getWebViewConfig(): WebViewConfig {
        val entity = WebViewConfigEntity(
            userAgent = createUserAgent()
        )
        return entity.toDomain()
    }
}