package co.aos.home.main.screen.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.aos.ui.theme.Black

/** 태그 칩 섹션
 *  - 추천 태그(tags) 노출
 *  - 선택/해제 토글(onToggle)
 *  - 선택 중일 때 상단에 '필터 중 n개 · 모두 지우기' 행 노출(onClear)
 */
@Composable
fun TagChipsSection(
    tags: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit,
    onClear: () -> Unit
) {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("태그", color = Black, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.width(8.dp))
            if (selected.isNotEmpty()) {
                AssistChip(
                    onClick = onClear,
                    label = { Text("필터 중 ${selected.size}개 · 모두 지우기", style = MaterialTheme.typography.bodySmall) },
                    leadingIcon = { Icon(Icons.Outlined.FilterAlt, contentDescription = null) }
                )
            }
        }
        if (tags.isEmpty()) {
            Text("추천 태그가 아직 없어요.", color = Black, style = MaterialTheme.typography.bodySmall)
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tags.forEach { tag ->
                    val isSel = tag in selected
                    FilterChip(
                        selected = isSel,
                        onClick = { onToggle(tag) },
                        label = { Text(tag) },
                        leadingIcon = if (isSel) { { Icon(Icons.Outlined.Check, null) } } else null,
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }
        }
    }
}