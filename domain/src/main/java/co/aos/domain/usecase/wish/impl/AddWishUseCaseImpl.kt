package co.aos.domain.usecase.wish.impl

import co.aos.domain.model.Wish
import co.aos.domain.model.WishImage
import co.aos.domain.repository.WishRepository
import co.aos.domain.usecase.wish.AddWishUseCase
import javax.inject.Inject

/**
 * Wish 데이터 추가 관련 UseCase 구현 클래스
 * */
class AddWishUseCaseImpl @Inject constructor(
    private val wishRepository: WishRepository
): AddWishUseCase {

    override suspend fun invoke(wish: Wish, images: List<WishImage>) {
        wishRepository.addWish(wish, images)
    }
}