package co.aos.home.bottombar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 하단 바 구성을 위한 아이콘 정의
 * */
enum class BottomIcon(val id: Int, val icon: ImageVector, val label: String, val route: String) {
    /** 홈 */
    HOME(0, Icons.Outlined.Home, "홈", "home"),

    /** 게시물 작성 */
    WRITE_BOARD(1, Icons.Outlined.AddBox, "게시물작성", "writeBoard"),

    /** 음악 */
    ADD_MUSIC(2, Icons.Outlined.MusicNote, "음악", "music"),

    /** 마이페이지 */
    MY_PAGE(3, Icons.Outlined.Person, "마이페이지", "myPage")
}