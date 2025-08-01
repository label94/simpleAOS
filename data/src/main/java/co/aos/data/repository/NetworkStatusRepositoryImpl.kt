package co.aos.data.repository

import co.aos.data.datasource.NetworkStatusRemoteDataSource
import co.aos.data.entity.NetworkStatusEntity
import co.aos.data.entity.toDomain
import co.aos.domain.model.NetworkStatusData
import co.aos.domain.repository.NetworkStatusRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * 네트워크 상태 Repository 구현 클래스
 * */
class NetworkStatusRepositoryImpl @Inject constructor(
    private val networkStatusRemoteDataSource: NetworkStatusRemoteDataSource
): NetworkStatusRepository {
    override fun isNetworkConnected(): Flow<NetworkStatusData> {
        return networkStatusRemoteDataSource.isNetworkConnected().map { entity ->
            NetworkStatusEntity(isNetworkConnected = entity.isNetworkConnected).toDomain()
        }
    }
}