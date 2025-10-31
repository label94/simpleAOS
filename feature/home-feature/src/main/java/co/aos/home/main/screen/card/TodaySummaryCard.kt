package co.aos.home.main.screen.card

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.aos.ui.theme.Amber
import co.aos.ui.theme.Black
import co.aos.ui.theme.Blue
import co.aos.ui.theme.ChartBlue
import co.aos.ui.theme.ChartCyan
import co.aos.ui.theme.ChartDeepOrange
import co.aos.ui.theme.ChartDeepPurple
import co.aos.ui.theme.ChartIndigo
import co.aos.ui.theme.ChartLightGreen
import co.aos.ui.theme.ChartLime
import co.aos.ui.theme.ChartOrange
import co.aos.ui.theme.ChartPink
import co.aos.ui.theme.ChartPurple
import co.aos.ui.theme.ChartSienna
import co.aos.ui.theme.ChartTeal
import co.aos.ui.theme.ChartYellow
import co.aos.ui.theme.LightGreen2
import co.aos.ui.theme.LightRed
import co.aos.ui.theme.White

/** 오늘 요약 카드 (개선된 디자인) */
@Composable
fun TodaySummaryCard(
    todayMood: Int?,
    todayWritten: Boolean,
    randomTitleFromMonth: String?,
    monthTagStats: Map<String, Float>,
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
                SummaryLoadingSkeleton()
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
                        }
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            // 이번 달 요약 영역 (랜덤 제목 + 태그 차트)
            Crossfade(targetState = loading, label = "summary-crossfade") {
                if (it) {
                    // 로딩 스켈레톤 UI
                    SummaryLoadingSkeleton()
                } else {
                    MonthSummarySection(randomTitleFromMonth, monthTagStats)
                }
            }
        }
    }
}

@Composable
private fun SummaryLoadingSkeleton() {
    Row(Modifier.height(100.dp)) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color.LightGray.copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .background(Color.LightGray.copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(20.dp)
                    .background(Color.LightGray.copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
            )
        }
    }
}

@Composable
private fun MonthSummarySection(
    randomTitle: String?,
    tagStats: Map<String, Float>
) {
    Column {
        // 1. 이번 달의 랜덤 제목
        Text(
            text = "이번 달, 이런 생각을 했어요",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = randomTitle ?: "아직 기록이 없어요. 첫 기록을 남겨보세요!",
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 2. 태그 통계
        Text(
            text = "이달의 관심사",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (tagStats.isEmpty()) {
            Text(
                text = "태그를 사용해 일기를 분류하고 통계를 확인해보세요.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 4.dp)
            )
        } else {
            TagStatChart(stats = tagStats)
        }
    }
}

@Composable
private fun TagStatChart(
    stats: Map<String, Float>,
    modifier: Modifier = Modifier
) {
    // 1. 태그 이름으로 통계를 정렬하여 항상 일관된 순서와 색상을 보장
    val sortedStats = remember(stats) { stats.toList().sortedBy { it.first } }

    // 2. 정렬된 태그 목록을 기반으로 색상 맵을 생성
    val tagColors = remember(sortedStats) {
        val colorPalette = listOf(
            ChartSienna, ChartPink, ChartPurple, ChartDeepPurple,
            ChartIndigo, ChartBlue, Blue, ChartCyan,
            ChartTeal, LightGreen2, ChartLightGreen, ChartLime,
            ChartYellow, Amber, ChartOrange, ChartDeepOrange
        )
        sortedStats.mapIndexed { index, (tag, _) ->
            tag to colorPalette[index % colorPalette.size]
        }.toMap()
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 3. 정렬된 데이터와 색상으로 차트와 범례를 그림
        PieChart(stats = sortedStats, colors = tagColors)
        VerticalDivider(Modifier.height(100.dp).padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ChartLegend(stats = sortedStats, colors = tagColors)
    }
}

@Composable
private fun PieChart(
    stats: List<Pair<String, Float>>, // Map 대신 정렬된 List를 받음
    colors: Map<String, Color>,
    modifier: Modifier = Modifier
) {
    // 단일 Canvas에서 모든 조각을 그려 성능 및 정확성 향상
    Canvas(modifier = modifier.size(100.dp)) {
        var startAngle = -90f
        stats.forEach { (tag, percentage) ->
            val sweepAngle = 360 * percentage
            if (sweepAngle > 0f) {
                drawArc(
                    color = colors[tag] ?: Color.Gray,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = 25f, cap = StrokeCap.Butt)
                )
            }
            startAngle += sweepAngle
        }
    }
}

@Composable
private fun ChartLegend(
    stats: List<Pair<String, Float>>, // Map 대신 정렬된 List를 받음
    colors: Map<String, Color>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        // 정렬된 목록의 순서대로 범례 표시 (최대 4개)
        stats.take(4).forEach { (tag, percentage) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(colors[tag] ?: Color.Gray, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = tag,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${(percentage * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
