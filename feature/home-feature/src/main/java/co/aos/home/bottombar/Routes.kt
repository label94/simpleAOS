package co.aos.home.bottombar

/** 라우트 정의 */
object Routes {
    // ====== Bottom tabs ======
    const val HOME = "home" // 홈
    const val CALENDAR = "calendar" // 달력
    const val INSPIRATION = "inspiration"   // 영감
    const val PROFILE = "myPage"         // 마이페이지

    // ====== Sub Page ======
    const val EDITOR = "editor"          // 작성/수정 (optional entryId)
    const val DETAIL = "detail/{entryId}" // 상세(entryId 필수)
    const val SEARCH = "search" // 검색
    const val UPDATE = "update/{entryId}" // 수정 (entryId 필수)

    // ======== 새로고침을 위한 Flag =========
    const val REFRESH_DETAIL = "refreshDetail" // 상세 리프레시
    const val REFRESH_LIST = "refreshList" // 홈, 전체 목록 리프레시 용도


    // 상세 화면 경로 생성기
    fun detail(entryId: String) = "detail/$entryId"

    // 수정 모드 일 때 에디터 경로 생성기
    fun update(entryId: String) = "update/$entryId"
}