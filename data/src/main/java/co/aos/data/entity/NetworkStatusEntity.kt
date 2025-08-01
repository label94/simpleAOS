package co.aos.data.entity

import co.aos.domain.model.NetworkStatusData

/** 네트워크 상태 Entity */
data class NetworkStatusEntity(
    val isNetworkConnected: Boolean
)

fun NetworkStatusEntity.toDomain() = NetworkStatusData(
    isNetworkConnected = isNetworkConnected
)
