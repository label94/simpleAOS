package co.aos.home.topbar

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton // IconButton으로 변경
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar // TopAppBar import
import androidx.compose.material3.TopAppBarDefaults // TopAppBarDefaults import
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.aos.common.APP_MAIN_NAME

/**
 * 홈에서만 사용하는 상단 바 UI
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
) {
    TopAppBar(
        title = {
            Text(
                text = APP_MAIN_NAME,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                fontFamily = FontFamily.Serif,
                color = Color.Black
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White, // 배경색 흰색으로 지정
            scrolledContainerColor = Color.White // 스크롤시 배경색 흰색으로 지정
        ),
        scrollBehavior = scrollBehavior
    )
}
