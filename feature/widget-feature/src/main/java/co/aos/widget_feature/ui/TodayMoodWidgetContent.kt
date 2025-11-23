package co.aos.widget_feature.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import co.aos.ui.theme.Black
import co.aos.ui.theme.White
import co.aos.widget_feature.R

/**
 * 오늘 선택된 무드를 보여주는 확인용 위젯 UI
 * @param selectedMood: 오늘 기록된 1~5 사이의 무드 값
 */
@SuppressLint("RestrictedApi")
@Composable
internal fun TodayMoodWidgetContent(
    selectedMood: Int
) {
    // 1. 선택된 무드에 맞는 리소스 준비
    val iconRes = when(selectedMood) {
        1 -> R.drawable.ic_mood_1
        2 -> R.drawable.ic_mood_2
        3 -> R.drawable.ic_mood_3
        4 -> R.drawable.ic_mood_4
        5 -> R.drawable.ic_mood_5
        else -> R.drawable.ic_mood_3 // 기본값
    }
    val moodText = when(selectedMood) {
        1 -> "최악"
        2 -> "나쁨"
        3 -> "보통"
        4 -> "좋음"
        5 -> "최고"
        else -> "보통"
    }

    // 2. 전체 UI 레이아웃 (공간을 더 효율적으로 사용하도록 수정)
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(16.dp)
            .background(White),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상단 헤더 텍스트
        Text(
            text = "오늘 하루, 마음을 기록했어요.",
            style = TextStyle(
                color = ColorProvider(Color.DarkGray),
                fontSize = 12.sp // Font size reduced
            )
        )

        // Spacer 높이 감소
        Spacer(modifier = GlanceModifier.height(16.dp))

        // 중앙 컨텐츠 (아이콘 + 텍스트)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                provider = ImageProvider(iconRes),
                contentDescription = moodText,
                modifier = GlanceModifier.size(40.dp) // Icon size reduced
            )
            Spacer(modifier = GlanceModifier.width(12.dp))
            Column {
                Text(
                    text = moodText,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp, // Font size reduced
                        color = ColorProvider(Black)
                    )
                )
                Text(
                    text = "으로 기록했어요.",
                    style = TextStyle(
                        fontSize = 11.sp, // Font size reduced
                        color = ColorProvider(Color.Gray)
                    )
                )
            }
        }

        // Spacer 높이 감소
        Spacer(modifier = GlanceModifier.height(16.dp))

        // 하단 격려 텍스트
        Text(
            text = "내일의 감정도 들려주세요!",
            style = TextStyle(
                fontSize = 11.sp, // Font size reduced
                color = ColorProvider(Color.Gray)
            )
        )
    }
}
