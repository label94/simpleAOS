package co.aos.network_error_feature.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 네트워크 연결 오류 시 표시하는 UI
 * */
@Composable
fun NetworkErrorScreen(
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "네트워크 연결 상태가 좋지 않습니다.",
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "잠시 후 다시 시도해주세요.",
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = {
                    onRetry.invoke()
                }
            ) {
                Text(
                    text = "다시 시도"
                )
            }
        }
    }
}