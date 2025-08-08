package co.aos.data.entity

import co.aos.domain.model.User
import co.aos.roomdb.entity.UserEntity

/**
 * user data mapper
 * */

fun UserEntity.toDomain(): User = User(id, password, nickname, profileImagePath)
fun User.toEntity(): UserEntity = UserEntity(id, password, nickname, profileImagePath)