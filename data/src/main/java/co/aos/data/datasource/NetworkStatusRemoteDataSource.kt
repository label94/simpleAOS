package co.aos.data.datasource

import co.aos.data.entity.NetworkStatusEntity
import kotlinx.coroutines.flow.Flow

/** 네트워크 상태 감지 RemoteDataSource */
interface NetworkStatusRemoteDataSource {

    /** 네트워크 연결 상태 반환 */
    fun isNetworkConnected(): Flow<NetworkStatusEntity>
}