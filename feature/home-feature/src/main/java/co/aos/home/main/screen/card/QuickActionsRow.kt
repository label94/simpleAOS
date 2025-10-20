package co.aos.home.main.screen.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** 빠른 작성 버튼 UI */
@Composable
fun QuickActionsRow(
    onQuickAddText: () -> Unit,
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        ElevatedButton(onClick = onQuickAddText, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp)) {
            Text("✍️ 텍스트")
        }
    }
}