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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.aos.common.noRippleClickable
import co.aos.myutils.log.LogUtil
import co.aos.ui.theme.White

/**
 * 커스텀 하단 바
 * */
@Composable
fun BottomBar() {
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
                .padding(15.dp)
                .background(White),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            items.forEach { item ->
                BottomItemView(
                    item = item,
                    onItemSelect = { icon ->
                        LogUtil.d(LogUtil.BARCODE_SCAN_LOG_TAG, "icon : $icon")

                        //todo : 개발 필요 => 하단 바 클릭 이벤트 처리
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
    onItemSelect: (BottomIcon) -> Unit
) {
    Icon(
        imageVector = item.icon,
        contentDescription = item.label,
        modifier = Modifier
            .size(30.dp)
            .noRippleClickable {
                onItemSelect(item)
            }
    )
}