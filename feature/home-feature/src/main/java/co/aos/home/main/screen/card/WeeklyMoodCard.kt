package co.aos.home.main.screen.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.aos.loading.skeleton.ShimmerBox
import co.aos.ui.theme.Black
import co.aos.ui.theme.Transparent
import co.aos.ui.theme.White

/** 주간 무드 카드 */
@Composable
fun WeeklyMoodCard(
    weekly: List<Int?>,
    streak: Int,
    bestStreak: Int,
    loading: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
    ) {
        Text(
            "지난 7일 무드",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 10.dp, top = 10.dp)
        )
        Spacer(Modifier.height(8.dp))

        if (loading) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { repeat(7) { ShimmerBox(Modifier.weight(1f)) } }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                weekly.forEachIndexed { idx, m ->
                    val isToday = idx == weekly.lastIndex
                    val label = when (m) {
                        1 -> "😣"; 2 -> "😕"; 3 -> "🙂"; 4 -> "😊"; 5 -> "🤩"
                        else -> "○"
                    }
                    Surface(
                        tonalElevation = if (isToday) 6.dp else 1.dp,
                        shadowElevation = if (isToday) 4.dp else 0.dp,
                        shape = RoundedCornerShape(12.dp),
                        color = if (m == null) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .padding(horizontal = 8.dp)
                                .wrapContentSize(Alignment.Center)
                        ) {
                            Text(label)
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                "Streak ${streak}일 / Best ${bestStreak}일",
                modifier = Modifier.padding(start = 10.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}