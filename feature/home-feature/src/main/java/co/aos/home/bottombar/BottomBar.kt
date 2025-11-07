package co.aos.home.bottombar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import co.aos.common.noRippleClickable
import co.aos.home.main.screen.LocalAppNavController
import co.aos.home.main.screen.MAIN_NAV_GRAPH_NAME
import co.aos.myutils.log.LogUtil
import co.aos.ui.theme.Black
import co.aos.ui.theme.Magenta
import co.aos.ui.theme.Red
import co.aos.ui.theme.White

/**
 * 커스텀 하단 바
 * */
@Composable
fun BottomBar(
    navController: NavHostController,
) {
    // nav 관련
    val backEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backEntry?.destination?.route

    // icon 목록
    val items = BottomIcon.entries.toList()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .wrapContentHeight()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp)
                .background(White),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            items.forEach { item ->
                BottomItemView(
                    item = item,
                    selected = currentRoute?.startsWith(item.route) == true,
                    onItemSelect = { icon ->
                        LogUtil.d(LogUtil.BARCODE_SCAN_LOG_TAG, "icon : $icon")

                        // 하단 바 클릭 이벤트 처리
                        navController.navigate(icon.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(MAIN_NAV_GRAPH_NAME){
                                inclusive = true
                                saveState = true
                            }
                        }
                    }
                )
            }
        }
    }
}

/**
 * Icon UI 영역
 * */
@Composable
fun BottomItemView(
    item: BottomIcon,
    selected: Boolean,
    onItemSelect: (BottomIcon) -> Unit
) {
    val tint = if (selected) {
        Magenta
    } else {
        Black
    }

    Icon(
        imageVector = item.icon,
        contentDescription = item.label,
        tint = tint,
        modifier = Modifier
            .size(30.dp)
            .noRippleClickable {
                onItemSelect(item)
            }
    )
}