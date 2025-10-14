package co.aos.domain.model

/**
 * User 관리를 위한 데이터 모델
 * */
data class User(
    val uid: String, // 회원 등록 시 발급 되는 uid
    val id: String, // 로그인 id(email)
    val nickName: String, // 닉네임,
    val localProfileImgCode: Int, // 로컬 프로필 이미지 코드
)
