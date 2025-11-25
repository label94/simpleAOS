package co.aos.domain.model

import androidx.annotation.Keep

/**
 * 네트워크 연결 상태 관련 data set
 * */
@Keep
data class NetworkStatusData(
    val isNetworkConnected: Boolean
)
