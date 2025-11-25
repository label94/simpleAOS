package co.aos.data.entity

import androidx.annotation.Keep
import co.aos.domain.model.User
import co.aos.firebase.model.UserDto

/**
 * UserData Mapper 유틸 파일
 * */
@Keep
fun UserDto.toDomain() = User(uid, id, nickName, localProfileImgCode)