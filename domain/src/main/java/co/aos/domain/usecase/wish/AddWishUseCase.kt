package co.aos.domain.usecase.wish

import co.aos.domain.model.Wish
import co.aos.domain.model.WishImage

/**
 * Wish 데이터 추가 관련 UseCase
 * */
interface AddWishUseCase {
    suspend operator fun invoke(wish: Wish, images: List<WishImage>)
}