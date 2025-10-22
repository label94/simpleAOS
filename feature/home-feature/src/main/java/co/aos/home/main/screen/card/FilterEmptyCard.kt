package co.aos.home.main.screen.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 필터에 맞는 조건에 없을 경우 표시 되는 Card UI
 * */
@Composable
fun FilterEmptyCard(selectedCount: Int, onClear: () -> Unit) {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("선택한 태그에 맞는 일기가 없어요", style = MaterialTheme.typography.titleMedium)
        Text("필터 조건을 줄이거나 모두 지워보세요.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedButton(onClick = onClear, shape = RoundedCornerShape(14.dp)) {
            Text("필터 ${selectedCount}개 지우기", style = MaterialTheme.typography.bodySmall)
        }
    }
}