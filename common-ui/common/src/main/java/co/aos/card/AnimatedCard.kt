package co.aos.card

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.aos.ui.theme.White

/** 공용 커스텀 카드 뷰(카드 페이드 인 + 사이즈 애니메이션) */
@Composable
fun AnimatedCard(
    shape: Shape = RoundedCornerShape(20.dp),
    elevation: Dp = 8.dp,                 // 기본 카드 고도
    content: @Composable () -> Unit
) {
    // 천천히 커지며 나타나는 느낌
    val alpha by animateFloatAsState(targetValue = 1f, label = "fadeIn")

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation, shape)      // 추가 음영
            .animateContentSize(),
        shape = shape,
        colors = CardDefaults.elevatedCardColors(
            containerColor = White // 기본 surface (화이트 테마에서 흰색)
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = elevation)
    ) { content() }
}