package co.aos.myutils.log

import android.content.Context
import android.os.Build
import android.util.Log
import co.aos.myutils.BuildConfig
import co.aos.myutils.R

/**
 * LogUtils
 *
 * - 커스텀 Log 유틸
 * */
object LogUtil {

    // 기본 log 태그
    const val DEFAULT_TAG = "myApp::"

    // network
    const val NET_STATE_LOG_TAG = "${DEFAULT_TAG}_network"

    // webView
    const val WEB_VIEW_LOG_TAG = "${DEFAULT_TAG}_webView"

    // cookie
    const val WEB_COOKIE_LOG = "${DEFAULT_TAG}_cookie"

    // ocr
    const val OCR_LOG_TAG = "${DEFAULT_TAG}_ocr"

    // js
    const val JS_LOG_TAG = "${DEFAULT_TAG}_js"

    // 로그인
    const val LOGIN_LOG_TAG = "${DEFAULT_TAG}_login"

    // 회원가입
    const val JOIN_LOG_TAG = "${DEFAULT_TAG}_join"

    // 스플래시
    const val SPLASH_LOG_TAG = "${DEFAULT_TAG}_splash"

    // barcode 스캔
    const val BARCODE_SCAN_LOG_TAG = "${DEFAULT_TAG}_barcode"

    // bottomBar
    const val BOTTOM_BAR_LOG_TAG = "${DEFAULT_TAG}_bottomBar"

    // build.gradle.kts 에 적용 된 build Type 을 확인하기 위한 상수
    private const val BUILD_TYPE_DEBUG = "debug" // 디버그(개발)

    // 로그 활성화 유무
    private var isEnabled = true

    private const val SPLIT_LOG_LENGTH = 2048 // 로그 자르기 길이
    var traceLogCallBack: ((String, Throwable) -> Unit)? = null

    /**
     * AGP 9 부터는 BuildConfig가 deprecated 되어, 대응하기 위해 코드 추가
     * - 현재 build Type이 debug 인지 확인하는 용도
     *
     * @return : true(debug), false(release)
     * */
    fun isDevMode(): Boolean {
        val mode = BuildConfig.BUILD_TYPE
        isEnabled = mode == BUILD_TYPE_DEBUG
        return isEnabled
    }

    @JvmStatic
    fun e(tag: String, log: String) = write(Log.ERROR, tag, log)

    @JvmStatic
    fun w(tag: String, log: String) = write(Log.WARN, tag, log)

    @JvmStatic
    fun v(tag: String, log: String) = write(Log.VERBOSE, tag, log)

    @JvmStatic
    fun i(tag: String, log: String) = write(Log.INFO, tag, log)

    @JvmStatic
    fun d(tag: String, log: String) = write(Log.DEBUG, tag, log)

    private fun makeLogPreFix(): String {
        var result = ""
        val currentThreadTrace = Thread.currentThread().stackTrace
        var index = 5
        if (index < currentThreadTrace.size) {
            if (currentThreadTrace[index].fileName.isNullOrEmpty()) index += 1
            result = "(${currentThreadTrace[index].fileName}:${currentThreadTrace[index].lineNumber}) "
        }
        return result
    }

    private fun write(logType: Int, tagValue: String, logValue: String) {
        if (logType == Log.WARN || logType == Log.ERROR) traceLogCallBack?.invoke(tagValue, Throwable(logValue))
        if (isEnabled) {
            try {
                // 태그가 빈 문자열이면 기본 태그 지정
                var tag = if (tagValue.isNotEmpty()) tagValue else DEFAULT_TAG

                // 잘라질 로그 크기 계산
                val chunkCount = logValue.length / SPLIT_LOG_LENGTH // integer division

                // 잘라질 만큼
                for (i in 0..chunkCount) {
                    val max = SPLIT_LOG_LENGTH * (i + 1)
                    val logData = StringBuilder()
                    logData.append(makeLogPreFix())
                    if (0 < chunkCount) {
                        logData.append("($i/$chunkCount) ")
                    }
                    logData.append(
                        if (max >= logValue.length)
                            logValue.substring(SPLIT_LOG_LENGTH * i)
                        else
                            logValue.substring(SPLIT_LOG_LENGTH * i, max)
                    )
                    when (logType) {
                        Log.DEBUG -> Log.d(tag, logData.toString())
                        Log.ERROR -> Log.e(tag, logData.toString())
                        Log.VERBOSE -> Log.v(tag, logData.toString())
                        Log.WARN -> Log.w(tag, logData.toString())
                        else -> { // Log.INFO
                            Log.i(tag, logData.toString())
                        }
                    }
                }
            } catch (e: java.lang.Exception) {
                printStackTrace(e)
            }
        }
    }

    fun showCallStack() {
        if (isEnabled) {
            val stringBuilder = StringBuilder()
            for (stackTrace in Thread.currentThread().stackTrace) {
                stringBuilder.append("(${stackTrace.fileName}:${stackTrace.lineNumber}) ${stackTrace.methodName} isNative : ${stackTrace.isNativeMethod}" + "\n")
            }
            Log.i(DEFAULT_TAG, stringBuilder.toString())
        }
    }

    @JvmStatic
    fun printStackTrace(e: Throwable?) {
        if (isEnabled) {
            e?.printStackTrace()
        }
    }
}