package co.aos.domain.usecase.wish

import co.aos.domain.model.Wish

/**
 * 위시 목록 가져오는 유스케이스
 * */
interface GetWishesByUserUseCase {
    suspend operator fun invoke(id: String): List<Wish>
}