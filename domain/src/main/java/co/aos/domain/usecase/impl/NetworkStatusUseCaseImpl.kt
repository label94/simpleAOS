package co.aos.domain.usecase.impl

import co.aos.domain.model.NetworkStatusData
import co.aos.domain.repository.NetworkStatusRepository
import co.aos.domain.usecase.NetworkStatusUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 네트워크 연결 상태 UseCase 구현 클래스
 * */
class NetworkStatusUseCaseImpl @Inject constructor(
    private val networkStatusRepository: NetworkStatusRepository
): NetworkStatusUseCase {

    override fun invoke(): Flow<NetworkStatusData> = networkStatusRepository.isNetworkConnected()
}