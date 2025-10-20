package co.aos.home.main.screen.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** 최근 일기 빈 상태 */
@Composable
fun RecentEmptyCard(
    onQuickAdd: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("아직 작성한 일기가 없어요", style = MaterialTheme.typography.titleMedium)
            Text("오늘의 첫 기록을 남겨보세요.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Button(onClick = onQuickAdd, shape = RoundedCornerShape(14.dp)) { Text("지금 작성하기") }
        }
    }
}