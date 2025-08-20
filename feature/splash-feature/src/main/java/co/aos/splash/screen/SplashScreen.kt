package co.aos.splash.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import co.aos.ui.theme.DarkSeaGreen

/**
 * 스플래시 화면
 * */
@Composable
fun SplashScreen(
    onBack: () -> Unit
) {
    // 뒤로가기 핸들러
    BackHandler {
        onBack.invoke()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSeaGreen)
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center),
            text = "Android",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            color = Color.White,
        )
    }
}