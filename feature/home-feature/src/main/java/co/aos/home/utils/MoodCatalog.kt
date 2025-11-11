package co.aos.home.utils

/**
 * mood 표시를 위한 유틸 카탈로그
 * */
object MoodCatalog {

    /** mood 표시를 위한 데이터 리스트 */
    val MOOD_DATA_LIST = listOf(
        1 to "😣",  2 to "😕", 3 to "🙂", 4 to "😊", 5 to "🤩"
    )

    /** 이모지가 아닌, 문자 형태의 mood를 표시하기 위한 데이터 리스트 */
    val MOOD_DATA_STRING_LIST = listOf(
        1 to "힘든 날이에요.",  2 to "불안한 날이에요.", 3 to "평범한 날이에요.", 4 to "대체로 좋은날이에요.", 5 to "아주 기분 좋은날이에요."
    )

    /** 이모지 찾기 */
    fun findMood(mood: Int): String {
        return MOOD_DATA_LIST.find { it.first == mood }?.second ?: "😕"
    }
}