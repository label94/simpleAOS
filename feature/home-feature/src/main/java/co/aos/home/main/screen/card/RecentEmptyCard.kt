package co.aos.home.main.screen.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.aos.ui.theme.Black
import co.aos.ui.theme.White

/** 최근 일기 빈 상태 */
@Composable
fun RecentEmptyCard(
    onQuickAdd: () -> Unit
) {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            "아직 작성한 일기가 없어요",
            style = MaterialTheme.typography.titleMedium,
            color = Black
        )
        Text(
            "오늘의 첫 기록을 남겨보세요.",
            style = MaterialTheme.typography.bodyMedium,
            color = Black
        )
        Button(
            onClick = onQuickAdd,
            shape = RoundedCornerShape(14.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Black,
                contentColor = White
            )
        ) {
            Text("지금 작성하기")
        }
    }
}