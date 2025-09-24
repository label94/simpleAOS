package co.aos.home.bottombar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 하단 바 구성을 위한 아이콘 정의
 * */
enum class BottomIcon(val id: Int, val icon: ImageVector, val label: String, val route: String) {
    /** 홈 */
    HOME(0, Icons.Outlined.Home, "홈", "home"),

    /** 달력 */
    CALENDAR(1, Icons.Outlined.CalendarMonth, "달력", "calendar"),

    /** 탐색 */
    EXPLORE(2, Icons.Outlined.Explore, "탐색", "explore"),

    /** 마이페이지 */
    MY_PAGE(3, Icons.Outlined.Person, "마이페이지", "myPage")
}