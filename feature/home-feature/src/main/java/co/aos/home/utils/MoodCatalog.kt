package co.aos.home.utils

/**
 * mood 표시를 위한 유틸 카탈로그
 * */
object MoodCatalog {

    /** mood 표시를 위한 데이터 리스트 */
    val MOOD_DATA_LIST = listOf(
        1 to "😣",  2 to "😕", 3 to "🙂", 4 to "😊", 5 to "🤩"
    )

    /** 이모지 찾기 */
    fun findMood(mood: Int): String {
        return MOOD_DATA_LIST.find { it.first == mood }?.second ?: "😕"
    }
}