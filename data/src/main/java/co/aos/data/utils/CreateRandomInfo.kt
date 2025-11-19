package co.aos.data.utils

/**
 * 구글 로그인 후 랜덤 ID 및 닉네임 생성 용도
 * */
object CreateRandomInfo {
    /** 랜덤 이메일(ID) 생성 */
    fun createRandomID(str: String): String {
        val randomNumber = (0..100).random()
        return "Guest${randomNumber}${str.takeLast(5)}@gmail.com"
    }

    /** 랜덤 닉네임(ID) 생성 */
    fun createRandomNickName(str: String): String {
        val randomNumber = (0..100).random()
        return "G_${str.take(4)}_${randomNumber}님"
    }
}