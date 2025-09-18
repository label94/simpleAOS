package co.aos.home.main.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.aos.home.bottombar.BottomBar


@Composable
fun MainHomeScreen() {

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(), // 하단 시스템 바만큼 패딩 추가,
        bottomBar = {
            BottomBar()
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {

        }
    }
}