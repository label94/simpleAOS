package co.aos.domain.repository

import co.aos.domain.model.Wish
import co.aos.domain.model.WishImage

/**
 * Wish 데이터 관련 Repository
 * */
interface WishRepository {
    /** id에 대응되는 wish 목록 가져오기 */
    suspend fun getWishesByUser(id: String): List<Wish>

    /** wish 데이터 추가 */
    suspend fun addWish(wish: Wish, images: List<WishImage>)
}