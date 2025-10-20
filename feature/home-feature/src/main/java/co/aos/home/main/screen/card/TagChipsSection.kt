package co.aos.home.main.screen.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** 태그 칩 섹션 */
@Composable
fun TagChipsSection(
    tags: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    Column {
        Text("태그", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 10.dp, top = 10.dp))
        Spacer(Modifier.height(8.dp))
        FlowRow(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp),
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
        Spacer(Modifier.height(8.dp))
    }
}