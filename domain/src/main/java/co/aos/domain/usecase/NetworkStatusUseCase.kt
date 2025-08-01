package co.aos.domain.usecase

import co.aos.domain.model.NetworkStatusData
import kotlinx.coroutines.flow.Flow

/**
 * 네트워크 상태 관련 UseCase
 * */
interface NetworkStatusUseCase {
    operator fun invoke(): Flow<NetworkStatusData>
}