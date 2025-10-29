package co.aos.home.utils

/**
 * 앱 전역에서 쓰는 "사전 정의 태그" 카탈로그
 * - 필요하면 서버/원격 구성으로 대체 가능
 * - 지금은 간결성을 위해 하드코딩
 */
object TagCatalog {
    val ALL: List<String> = listOf(
        "직장", "운동", "독서", "기타", "개발", "요리", "취미", "가족", "여행"
    )
}