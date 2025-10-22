package co.aos.home.main.screen.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AssistChip
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
    onClick: () -> Unit,
    onTagClick: (String) -> Unit
) {
    Column(
        Modifier
            .noRippleClickable { onClick() }
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(data.moodEmoji, fontSize = 20.sp)
            Spacer(Modifier.width(8.dp))
            Text(data.title, color = Black, style = MaterialTheme.typography.titleMedium)
            if (data.pinned) {
                Spacer(Modifier.width(6.dp))
                Icon(Icons.Outlined.PushPin, null)
            }
            Spacer(Modifier.weight(1f))
            Text(
                data.dateText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (data.preview.isNotBlank()) {
            Text(data.preview, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
        if (data.tags.isNotEmpty()) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                data.tags.forEach { t ->
                    AssistChip(
                        onClick = { onTagClick(t) },   // ✅ 카드 내 태그 → 필터 추가
                        label = { Text("#$t" , color = Black, style = MaterialTheme.typography.bodySmall) }
                    )
                }
            }
        }
    }
}