package co.aos.home.main.screen.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.aos.loading.skeleton.ShimmerBox
import co.aos.ui.theme.Amber
import co.aos.ui.theme.Blue
import co.aos.ui.theme.LightGreen
import co.aos.ui.theme.Orange
import co.aos.ui.theme.Red
import java.time.LocalDate

/**
 * 주간 무드 바 차트 (요일 라벨 고정: 월~일, 오늘 라벨만 붉게)
 *
 * @param weekly   지난 7일 무드 (길이 7 권장, 값: 1..5, null=기록없음)
 *                 리스트가 과거→오늘/오늘→과거든 상관 없음.
 *                 (아래 normalize 로직으로 '지난 7일 날짜'에 매핑해서 월~일 순서로 재배열)
 * @param streak   연속 작성
 * @param bestStreak 최고 연속
 * @param loading  로딩 여부
 * @param endDate  "오늘" 날짜 (보통 LocalDate.now())
 */
@Composable
fun WeeklyMoodCard(
    weekly: List<Int?>,                 // 지난 7일 (길이 7 권장, 값: 1..5, null=기록없음)
    streak: Int,                        // (표시는 안 하지만 파라미터 유지)
    bestStreak: Int,                    // (표시는 안 하지만 파라미터 유지)
    loading: Boolean,
    endDate: LocalDate        // 오늘 날짜 (요일 강조용)
) {
    val maxBarHeight = 64.dp

    // 월~일 고정 라벨
    val weekdayKor = listOf("월","화","수","목","금","토","일")

    // 지난 7일 날짜(과거→오늘): [endDate-6, ..., endDate]
    val last7Dates = (6 downTo 0).map { endDate.minusDays(it.toLong()) }

    // DayOfWeek: MONDAY=1..SUNDAY=7 → 월=0..일=6
    fun weekdayIndex(date: LocalDate) = (date.dayOfWeek.value - 1) % 7

    // weekly 정규화: 길이 7 맞추기(앞쪽 null 패딩), 순서는 과거→오늘로 간주
    // (프로젝트에서 weekly가 다른 정렬이라면 여기에서 reversed() 등으로 맞춰주세요)
    val normalized: List<Int?> = run {
        val w = if (weekly.size >= 7) weekly.takeLast(7) else List(7 - weekly.size) { null } + weekly
        w
    }

    // 요일별 버킷(월=0..일=6): 해당 요일에 지난 7일 값 채우기
    val moodByWeekday: Array<Int?> = arrayOfNulls(7)
    last7Dates.forEachIndexed { i, date ->
        moodByWeekday[weekdayIndex(date)] = normalized.getOrNull(i)
    }

    // 오늘 요일 인덱스(라벨 강조)
    val todayW = weekdayIndex(endDate)

    // 오늘 점수(하단 문구용): normalized의 마지막을 오늘로 간주
    val todayScore: Int? = normalized.lastOrNull()

    // ✅ 점수별 막대 색상 팔레트 (서로 다른 컬러)
    //  - 접근성/가독성 위해 명도 대비가 확보된 톤을 선정
    //  - 필요 시 팀 브랜드 톤에 맞춘 팔레트로 교체 가능
    val moodBarColors = listOf(
        1 to Red, // Red 700 (기분 안 좋음)
        2 to Orange, // Orange 600
        3 to Amber, // Amber 500
        4 to LightGreen, // Green 500
        5 to Blue  // Blue 400 (최상)
    )

    // 점수 -> 막대 색상 (null은 outline 기반 연함)
    @Composable
    fun barColorFor(score: Int?): Color =
        score?.let { s -> moodBarColors.firstOrNull { it.first == s }?.second }
            ?: MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)

    // 막대 높이 계산 (1..5 → 비례), null은 아주 얇게
    fun barHeightFor(score: Int?): Dp =
        if (score == null) 8.dp
        else (score.coerceIn(1, 5) / 5f * maxBarHeight.value).dp

    Column(Modifier.padding(16.dp)) {

        // ✅ 타이틀 문구 업데이트
        Text("이번 주 나의 무드", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))

        if (loading) {
            // 로딩 스켈레톤: 7칸
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(7) {
                    Column(
                        Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ShimmerBox(Modifier.fillMaxWidth().height(maxBarHeight))
                        ShimmerBox(Modifier.width(24.dp).height(12.dp))
                    }
                }
            }
        } else {
            // 데이터: 월~일 순서로 렌더
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                for (w in 0..6) {
                    val mood = moodByWeekday[w]
                    val isToday = (w == todayW)

                    // 트랙 색(기록 없으면 더 흐림)
                    val trackColor =
                        if (mood == null) MaterialTheme.colorScheme.surfaceVariant
                        else MaterialTheme.colorScheme.surface

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 트랙 + 실제 바(아래 정렬)
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(maxBarHeight)
                                .background(trackColor, RoundedCornerShape(8.dp))
                        ) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(barHeightFor(mood))
                                    .align(Alignment.BottomCenter)
                                    .background(barColorFor(mood), RoundedCornerShape(8.dp))
                            )
                        }
                        // 요일 라벨: 오늘만 빨간색으로 강조
                        Text(
                            text = weekdayKor[w],
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isToday) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(15.dp))
            // 하단: 오늘 점수 기반 감정 문구(이전 단계 유지)
            Text(
                text = moodCaption(todayScore),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/** 오늘 무드 점수(1..5) → 기발한 문구 매핑 */
@Composable
private fun moodCaption(score: Int?): String = when (score) {
    null -> "아직 오늘의 기분을 정하지 않았어요. ✍️"
    1 -> "🌧 오늘은 흐림… 작은 위로가 필요해요."
    2 -> "⛅ 아직은 좀 무겁지만, 천천히 밝아지고 있어요."
    3 -> "🌤 괜찮아요. 평온한 하루, 작은 즐거움을 찾아볼까요?"
    4 -> "☀️ 좋다! 에너지가 도는 하루예요."
    5 -> "🌈 최고! 오늘의 빛나는 순간을 기록해요 ✨"
    else -> "오늘은 어떤 하루였나요?"
}