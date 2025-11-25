package co.aos.data.entity

import androidx.annotation.Keep
import co.aos.domain.model.LegacyUser
import co.aos.roomdb.entity.UserEntity

/**
 * user data mapper
 * */

@Keep
fun UserEntity.toDomain(): LegacyUser = LegacyUser(id, password, nickname, profileImagePath)

@Keep
fun LegacyUser.toEntity(): UserEntity = UserEntity(id, password, nickname, profileImagePath)