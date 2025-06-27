package co.aos.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

/**
 * 커스텀 속성 정의
 *
 * - 공통으로 사용할 커스텀 속성 정의
 * */

/**
 * UI 클릭 시 ripple 효과 제거하는 컴포저블 함수
 *
 * - 버튼 클릭 시 ripple 효과 제거 확장 함수
 *
 * @param onClick : 클릭 이벤트
 * */
inline fun Modifier.noRippleClickable(crossinline onClick: ()->Unit): Modifier = composed {
    clickable(indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}

/**
 * 폰트 사이즈 변환(dp -> sp) 용도의 컴포저블 함수
 *
 * - 텍스트 크기 고정을 하고 싶을 때 사용
 *
 * @param size : 폰트 사이즈
 * */
@Composable
fun getFontSize(size: Float): TextUnit {
    val density = LocalDensity.current
    val fontSizeInPx = with(density) { size.dp.toPx() }
    return with(density) { fontSizeInPx.toSp() }
}