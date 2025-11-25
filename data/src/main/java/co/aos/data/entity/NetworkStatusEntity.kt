package co.aos.data.entity

import androidx.annotation.Keep
import co.aos.domain.model.NetworkStatusData

/** 네트워크 상태 Entity */
@Keep
data class NetworkStatusEntity(
    val isNetworkConnected: Boolean
)

@Keep
fun NetworkStatusEntity.toDomain() = NetworkStatusData(
    isNetworkConnected = isNetworkConnected
)
