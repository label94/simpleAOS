package co.aos.widget_feature.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import co.aos.ui.theme.Black
import co.aos.ui.theme.White
import co.aos.widget_feature.R
import co.aos.widget_feature.callback.MoodWidgetActionCallback
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.width
import java.time.LocalDate
import java.time.format.TextStyle as JavaTextStyle
import java.util.Locale

/** 지난 7일 무드 불러오기 위젯 UI */
@SuppressLint("RestrictedApi")
@Composable
internal fun MoodWidgetContent(
    weeklyMoods: List<Int?>
) {
    // 초기 파이어베이스에서 얻어온 주간 데이터 중 마지막 데이터를 가져온다(마지막 데이터를 오늘 데이터로 간주)
    val todayLoadData = weeklyMoods.lastOrNull()

    // 위젯의 내부 상태(Preferences)를 직접 읽어 오늘 무드를 확인합니다.
    // 이 값은 ActionCallback에서 updateAppWidgetState를 통해 업데이트됩니다.
    val prefs = currentState<Preferences>()
    val todayMood = prefs[MoodWidgetActionCallback.TODAY_MOOD_KEY]

    val displayWeeklyMoods = remember(weeklyMoods, todayMood) {
        if (todayMood != null && weeklyMoods.isNotEmpty()) {
            weeklyMoods.toMutableList().apply {
                this[lastIndex] = todayMood // 마지막 데이터를 오늘의 mood로 데이터로 업데이트
            }
        } else {
            weeklyMoods
        }
    }

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(16.dp)
            .background(White),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "최근 7일, 나의 감정 변화는?",
            style = TextStyle(
                color = ColorProvider(Black),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        )

        Spacer(modifier = GlanceModifier.height(12.dp))

        WeeklyMoodBarChart(moods = displayWeeklyMoods)

        Spacer(modifier = GlanceModifier.height(16.dp))

        // 구분선
        Box(modifier = GlanceModifier.fillMaxWidth().height(1.dp).background(Color(0x1F000000))) {}

        Spacer(modifier = GlanceModifier.height(16.dp))

        Text(
            text = "오늘 하루는 어땠나요?",
            style = TextStyle(
                color = ColorProvider(Color.DarkGray),
                fontSize = 14.sp
            )
        )

        Spacer(modifier = GlanceModifier.height(8.dp))

        if (todayMood != null) {
            // 오늘 기록된 무드가 있으면 -> 확인 UI 표시
            TodayMoodWidgetContent(
                selectedMood = todayMood
            )
        } else {
            if (todayLoadData != null) {
                // 초기 firebase 에서 가져온 데이터가 존재할 경우에는, 해당 데이터로 UI를 표시
                TodayMoodWidgetContent(
                    selectedMood = todayLoadData
                )
            } else {
                // 오늘 기록된 무드가 없으면 -> 선택 UI 표시
                MoodQuickSelectRow()
            }
        }
    }
}

/**
 * 지난 7일 무드를 간단한 바 차트로 표현.
 */
@SuppressLint("RestrictedApi")
@Composable
private fun WeeklyMoodBarChart(
    moods: List<Int?>,
) {
    val today = LocalDate.now()
    val lastSevenMoods = (List(7) { null } + moods.takeLast(7)).takeLast(7)
    // 오늘을 기준으로 지난 7일의 요일을 동적으로 생성합니다. (예: 월, 화, 수...)
    val dayLabels = (0..6).map {
        today.minusDays(6L - it).dayOfWeek.getDisplayName(JavaTextStyle.SHORT, Locale.KOREAN)
    }

    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .height(60.dp), // 차트 영역 전체 높이
        verticalAlignment = Alignment.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        lastSevenMoods.forEachIndexed { index, mood ->
            val coercedMood = mood?.coerceIn(1, 5)
            val barHeight = when (coercedMood) {
                1 -> 8.dp
                2 -> 16.dp
                3 -> 24.dp
                4 -> 32.dp
                5 -> 40.dp // 최대 높이
                else -> 2.dp // 기록 없는 날은 작은 회색 바로 표시
            }
            val barColor = Color(
                when (coercedMood) {
                    1 -> 0xFFF48FB1.toInt() // Light Pink
                    2 -> 0xFFFFF176.toInt() // Light Yellow
                    3 -> 0xFF81C784.toInt() // Light Green
                    4 -> 0xFF64B5F6.toInt() // Light Blue
                    5 -> 0xFFBA68C8.toInt() // Light Purple
                    else -> 0xFFE0E0E0.toInt() // Placeholder Gray
                }
            )

            Column(
                modifier = GlanceModifier.defaultWeight().fillMaxHeight(),
                verticalAlignment = Alignment.Bottom, // 바 아래쪽 정렬
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = GlanceModifier
                        .width(12.dp) // 막대 너비
                        .height(barHeight)
                        .background(ColorProvider(barColor))
                ) {}

                Spacer(GlanceModifier.height(4.dp))

                Text(
                    text = dayLabels.getOrElse(index) { "" }, // 동적으로 생성된 요일 사용
                    style = TextStyle(
                        fontSize = 10.sp,
                        color = ColorProvider(Color.Gray)
                    )
                )
            }
        }
    }
}

/**
 * 1~5 무드 선택 아이콘 행
 */
@Composable
private fun MoodQuickSelectRow() {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        (1..5).forEach { mood ->
            val iconRes = when(mood) {
                1 -> R.drawable.ic_mood_1
                2 -> R.drawable.ic_mood_2
                3 -> R.drawable.ic_mood_3
                4 -> R.drawable.ic_mood_4
                5 -> R.drawable.ic_mood_5
                else -> R.drawable.ic_mood_1
            }
            val moodText = when(mood) {
                1 -> "최악"
                2 -> "나쁨"
                3 -> "보통"
                4 -> "좋음"
                5 -> "최고"
                else -> ""
            }

            Column(
                modifier = GlanceModifier
                    .defaultWeight()
                    .clickable(
                        onClick = actionRunCallback<MoodWidgetActionCallback>(
                            actionParametersOf(MoodWidgetActionCallback.MOOD_PARAM_KEY to mood)
                        )
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    provider = ImageProvider(iconRes),
                    contentDescription = moodText,
                    modifier = GlanceModifier.size(38.dp)
                )
                Spacer(GlanceModifier.height(4.dp))
                Text(
                    text = moodText,
                    style = TextStyle(fontSize = 11.sp, color = ColorProvider(Color.DarkGray))
                )
            }
        }
    }
}
