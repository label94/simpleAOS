package co.aos.data.entity

import co.aos.domain.model.User
import co.aos.firebase.model.UserDto

/**
 * UserData Mapper 유틸 파일
 * */
fun UserDto.toDomain() = User(uid, id, nickName, localProfileImgCode)