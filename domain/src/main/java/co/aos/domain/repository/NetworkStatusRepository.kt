package co.aos.domain.repository

import co.aos.domain.model.NetworkStatusData
import kotlinx.coroutines.flow.Flow

/** 네트워크 상태 감지 관련 Repository */
interface NetworkStatusRepository {

    /** 네트워크 연결 상태 반환 */
    fun isNetworkConnected() : Flow<NetworkStatusData>
}