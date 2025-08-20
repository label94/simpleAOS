package co.aos.data.repository

import co.aos.domain.model.Wish
import co.aos.domain.model.WishImage
import co.aos.domain.repository.WishRepository
import co.aos.roomdb.dao.WishDao
import co.aos.roomdb.entity.WishEntity
import co.aos.roomdb.entity.WishImageEntity
import javax.inject.Inject

/**
 * Wish 데이터 관련 Repository 구현 클래스
 * */
class WishRepositoryImpl @Inject constructor(
    private val wishDao: WishDao
): WishRepository {

    override suspend fun getWishesByUser(id: String): List<Wish> {
        return wishDao.getWishesByUser(id).map { relation ->
            Wish(
                wishId = relation.wish.wishId,
                ownerId = relation.wish.ownerId,
                title = relation.wish.title,
                content = relation.wish.content,
                images = relation.images.map {
                    WishImage(
                        imageId = it.imageId,
                        wishOwnerId = it.wishOwnerId,
                        imageUrl = it.imageUrl
                    )
                }
            )
        }
    }

    override suspend fun addWish(
        wish: Wish,
        images: List<WishImage>
    ) {
        val wishId = wishDao.insertWish(
            WishEntity(
                ownerId = wish.ownerId,
                title = wish.title,
                content = wish.content
            )
        )
        val imageEntities = images.map {
            WishImageEntity(
                wishOwnerId = wishId,
                imageUrl = it.imageUrl
            )
        }
        wishDao.insertImage(imageEntities)
    }
}