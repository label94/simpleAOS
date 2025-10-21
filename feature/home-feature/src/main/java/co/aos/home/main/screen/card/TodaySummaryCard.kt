package co.aos.home.main.screen.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.aos.loading.skeleton.ShimmerBox
import co.aos.ui.theme.Black
import co.aos.ui.theme.White

/** 오늘 요약 카드 (개선된 디자인) */
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
    val cardShape = RoundedCornerShape(20.dp)
    val cardBrush = Brush.verticalGradient(
        colors = listOf(White, Color(0xFFF8F8F8))
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = cardBrush,
                shape = cardShape
            ),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("오늘의 요약", style = MaterialTheme.typography.titleMedium)

            if (loading) {
                // 로딩 UI
                ShimmerBox(Modifier.fillMaxWidth(0.8f))
                ShimmerBox(Modifier.fillMaxWidth(0.6f))
            } else {
                if (todayMood == null) {
                    // "오늘의 무드" 컨텐츠가 없을 경우
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("오늘의 무드를 선택해주세요.", style = MaterialTheme.typography.bodyMedium)
                        OutlinedButton(onClick = onPickMood, shape = RoundedCornerShape(12.dp)) {
                            Text("기록하기", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                } else {
                    // 무드 기록이 있을 경우
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 좌측: 큰 이모지
                        val emoji = listOf("😣", "😕", "🙂", "😊", "🤩")[todayMood - 1]
                        Text(emoji, fontSize = 48.sp, modifier = Modifier.weight(0.3f))

                        // 우측: 정보 및 액션 영역
                        Column(
                            modifier = Modifier.weight(0.7f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (todayWritten) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.CheckCircleOutline,
                                        contentDescription = "작성 완료",
                                        tint = Black
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text("오늘 일기: 작성 완료", style = MaterialTheme.typography.bodyMedium)
                                }
                            } else {
                                Button(
                                    onClick = onWrite,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Black)
                                ) {
                                    Icon(Icons.Outlined.Edit, contentDescription = "일기 쓰기")
                                    Spacer(Modifier.width(8.dp))
                                    Text("일기 쓰기", color = White, style = MaterialTheme.typography.bodyMedium)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.LocalFireDepartment,
                                    contentDescription = "연속 기록",
                                    tint = Color.Red.copy(alpha = 0.8f)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "연속 ${streak}일 (최고 ${bestStreak}일)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
