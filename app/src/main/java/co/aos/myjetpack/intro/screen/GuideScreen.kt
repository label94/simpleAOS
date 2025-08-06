package co.aos.myjetpack.intro.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import co.aos.myjetpack.ui.theme.White

/**
 * 접근권한 안내 화면
 * */
@Composable
fun GuideScreen(
    onComplete: () -> Unit,
    onBack: () -> Unit
) {
    // Back 버튼 처리
    BackHandler {
        onBack.invoke()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(White)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "접근권한 안내화면",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.weight(1f)) // 빈 공간으로 밀어내기

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    onClick = {
                        onComplete.invoke()
                    }
                ) {
                    Text(
                        text = "확인",
                        fontSize = 30.sp,
                    )
                }
            }
        }
    }
}