package co.aos.data.entity

import co.aos.domain.model.LegacyUser
import co.aos.roomdb.entity.UserEntity

/**
 * user data mapper
 * */

fun UserEntity.toDomain(): LegacyUser = LegacyUser(id, password, nickname, profileImagePath)
fun LegacyUser.toEntity(): UserEntity = UserEntity(id, password, nickname, profileImagePath)