package co.aos.home.main.screen.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.aos.common.noRippleClickable
import co.aos.home.main.model.DiaryCardUi
import co.aos.ui.theme.Black

/** 최근 일기 카드 UI */
@Composable
fun DiaryCard(
    data: DiaryCardUi,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(data.moodEmoji, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(data.title, style = MaterialTheme.typography.titleMedium)
                if (data.pinned) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Outlined.PushPin, null)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(data.dateText, style = MaterialTheme.typography.labelSmall, color = Black)
            }

            if (data.preview.isNotBlank()) {
                Text(data.preview, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }

            if (data.tags.isNotEmpty()) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    data.tags.forEach { t ->
                        AssistChip(
                            onClick = { /* 태그 클릭 확장 */ },
                            label = { Text("#$t") }
                        )
                    }
                }
            }
        }
    }
}