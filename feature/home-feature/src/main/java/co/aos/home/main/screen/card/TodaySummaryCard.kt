package co.aos.home.main.screen.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.aos.loading.skeleton.ShimmerBox
import co.aos.ui.theme.Black
import co.aos.ui.theme.White

/** 오늘 요약 카드 */
@Composable
fun TodaySummaryCard(
    todayMood: Int?,
    todayWritten: Boolean,
    streak: Int,
    bestStreak: Int,
    onPickMood: () -> Unit,
    onWrite: () -> Unit,
    loading: Boolean
) {
    Column(Modifier
        .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("오늘의 요약", style = MaterialTheme.typography.titleMedium)

        if (loading) {
            // 로딩 UI
            ShimmerBox(Modifier.fillMaxWidth(0.6f))
            ShimmerBox(Modifier.fillMaxWidth(0.4f))
        } else {
            if (todayMood == null) {
                // "오늘의 무드" 컨텐츠가 없을 경우
                Text("오늘의 무드를 선택해주세요.", color = Black, style = MaterialTheme.typography.bodyMedium)
                OutlinedButton(
                    onClick = onPickMood,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("기분 기록하기", color = Black, style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                val emoji = listOf("😣","😕","🙂","😊","🤩")[todayMood - 1]
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("오늘의 기분:", color = Black, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.width(8.dp))
                    Text(emoji, fontSize = 26.sp)
                }
            }

            // 오늘 일기 상태 + (미작성 시) 작성하기 버튼
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val written = if (todayWritten) "작성 완료" else ""
                Text("오늘 일기: $written", color = Black, style = MaterialTheme.typography.bodyMedium)
                if (!todayWritten) {
                    // ✅ 미작성인 경우에만 ‘작성하기’ 버튼 노출
                    Button(
                        onClick = onWrite,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Black,
                            contentColor = White
                        )
                    ) {
                        Text("빠른 작성", style = MaterialTheme.typography.bodyMedium, color = White)
                    }
                }
            }
            Text("연속 작성 : ${streak}일 • 최고: ${bestStreak}일", color = Black, style = MaterialTheme.typography.bodyMedium)
        }
    }
}