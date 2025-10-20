package co.aos.home.bottombar

/** 라우트 정의 */
object Routes {
    // ====== Bottom tabs ======
    const val HOME = "home" // 홈
    const val CALENDAR = "calendar" // 달력
    const val INSIGHTS = "leaderboard"   // 통계
    const val PROFILE = "myPage"         // 마이페이지

    // ====== Sub Page ======
    const val EDITOR = "editor"          // 작성/수정 (optional entryId)
    const val DETAIL = "detail/{entryId}" // 상세(entryId 필수)
    const val SEARCH = "search" // 검색

    // 상세 화면 경로 생성기
    fun detail(entryId: String) = "detail/$entryId"

    // 에디터 경로 생성기(수정 모드면 entryId 포함)
    fun editor(entryId: String? = null) =
        if (entryId == null) "editor" else "editor?entryId=$entryId"
}