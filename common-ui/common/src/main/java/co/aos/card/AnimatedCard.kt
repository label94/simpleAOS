package co.aos.card

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import co.aos.ui.theme.White

/** 공용 커스텀 카드 뷰(카드 페이드 인 + 사이즈 애니메이션) */
@Composable
fun AnimatedCard(
    content: @Composable () -> Unit,
) {
    val alpha by animateFloatAsState(targetValue = 1f, label = "fadeIn")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { this.alpha = alpha }
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp)
    ) {
        content()
    }
}