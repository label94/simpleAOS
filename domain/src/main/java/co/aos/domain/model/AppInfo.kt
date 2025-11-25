package co.aos.domain.model

import androidx.annotation.Keep

/** 앱 정보 관련 데이터 모델 */
@Keep
data class AppInfo(
    val versionName: String,
    val versionCode: String
)
