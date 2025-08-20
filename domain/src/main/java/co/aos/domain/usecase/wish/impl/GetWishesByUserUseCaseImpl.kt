package co.aos.domain.usecase.wish.impl

import co.aos.domain.model.Wish
import co.aos.domain.repository.WishRepository
import co.aos.domain.usecase.wish.GetWishesByUserUseCase
import javax.inject.Inject

/**
 * 위시 목록 가져오는 유스케이스 구현 클래스
 * */
class GetWishesByUserUseCaseImpl @Inject constructor(
    private val wishRepository: WishRepository
): GetWishesByUserUseCase {

    override suspend fun invoke(id: String): List<Wish> {
        return wishRepository.getWishesByUser(id)
    }
}