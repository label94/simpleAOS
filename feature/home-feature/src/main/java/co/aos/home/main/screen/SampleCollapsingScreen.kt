package co.aos.home.main.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.aos.appbar.ui.CollapsingToolbarScaffold
import co.aos.appbar.ui.rememberCollapsingToolbarScaffoldState
import co.aos.appbar.utils.ScrollStrategy
import co.aos.ui.theme.LightGreen
import co.aos.ui.theme.White

/**
 * CollapsingToolbarScaffold 사용 샘플 코드
 * */
@Composable
fun SampleCollapsingScreen() {
    val state = rememberCollapsingToolbarScaffoldState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(), // 하단 시스템 바만큼 패딩 추가,
    ) { innerPadding ->
        CollapsingToolbarScaffold(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            state = state,
            scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
            toolbar = {
                val textSize = (18 + (30 - 18) * state.toolbarState.progress).sp

                Box(
                    modifier = Modifier
                        .background(LightGreen)
                        .fillMaxWidth()
                        .height(100.dp)
                        .pin()
                )

                Text(
                    text = "Title",
                    modifier = Modifier
                        .road(Alignment.CenterStart, Alignment.BottomEnd)
                        .padding(60.dp, 16.dp, 16.dp, 16.dp),
                    color = White,
                    fontSize = textSize
                )

                Image(
                    modifier = Modifier
                        .pin()
                        .padding(16.dp),
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null
                )
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(40) {
                    Text(
                        text = "Item $it",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}