package co.aos.network_error_feature.model

import co.aos.domain.model.NetworkStatusData

/**
 * 네트워크 연결 상태 관련 dto
 * */
data class NetworkStatusModel(
    val isNetworkConnected: Boolean
)

fun NetworkStatusData.toPresentation(): NetworkStatusModel = NetworkStatusModel(
    isNetworkConnected = isNetworkConnected
)
