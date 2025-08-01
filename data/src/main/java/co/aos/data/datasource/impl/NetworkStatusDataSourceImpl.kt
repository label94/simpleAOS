package co.aos.data.datasource.impl

import co.aos.data.datasource.NetworkStatusRemoteDataSource
import co.aos.data.entity.NetworkStatusEntity
import co.aos.network.check.NetworkStatusManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * 네트워크 상태 감지 관련 DataSource 구현 클래스
 * */
class NetworkStatusDataSourceImpl @Inject constructor(
    private val networkStatusManager: NetworkStatusManager
): NetworkStatusRemoteDataSource {
    override fun isNetworkConnected(): Flow<NetworkStatusEntity> {
        return networkStatusManager.networkStatus.map {
            NetworkStatusEntity(it)
        }
    }
}