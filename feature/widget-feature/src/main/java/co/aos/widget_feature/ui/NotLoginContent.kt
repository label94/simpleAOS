package co.aos.widget_feature.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import co.aos.ui.theme.Black

/** 로그인 유도 UI 위젯 */
@SuppressLint("RestrictedApi")
@Composable
internal fun NotLoginContent() {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "로그인이 필요합니다.",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                color = ColorProvider(Black)
            )
        )

        Spacer(modifier = GlanceModifier.height(4.dp))

        Text(
            text = "앱에서 로그인 후 위젯을 다시 추가해주세요.",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                color = ColorProvider(Black)
            )
        )
    }
}