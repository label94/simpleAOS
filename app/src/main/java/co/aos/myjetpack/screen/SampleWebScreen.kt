package co.aos.myjetpack.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.aos.webview_feature.presentation.screen.SampleWebScreen

/**
 * 샘플 웹뷰 화면
 * */
@Composable
fun SampleWebScreen() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            SampleWebScreen()
        }
    }
}