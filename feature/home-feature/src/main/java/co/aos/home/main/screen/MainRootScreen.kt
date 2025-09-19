package co.aos.home.main.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.aos.home.bottombar.BottomBar
import co.aos.home.bottombar.BottomIcon
import co.aos.ui.theme.White

/**
 * 전역 적으로 NavController 를 공유/주입 하기 위한 구조
 *
 * @property LocalAppNavController : 전역으로 사용할 NavController
 * @property MAIN_NAV_GRAPH_NAME : 메인 화면 네비게이션 그래프 이름
 * */
const val MAIN_NAV_GRAPH_NAME = "main_graph"
val LocalAppNavController = staticCompositionLocalOf<NavHostController> {
    error("NavController not provided")
}

/**
 * Main Root
 * */
@Composable
fun MainRootUI() {
    val navController = rememberNavController()
    val snackBarHostState = remember { SnackbarHostState() }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // 컨텐츠 영역
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = 64.dp
                )
        ) {
            CompositionLocalProvider(LocalAppNavController provides navController) {
                BottomNavigationGraph(
                    navController = navController,
                    snackBarHostState = snackBarHostState
                )
            }
        }


        // Surface 로 감싼 바텀바
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(64.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
                .navigationBarsPadding(),
            color = White,
        ) {
            BottomBar()
        }
    }
}

/**
 * 하단 bottom 메뉴 이동 관련 제어
 *
 * @param navController : 네비게이션 컨트롤러
 * @param snackBarHostState : 스낵바 상태
 * */
@Composable
private fun BottomNavigationGraph(
    navController: NavHostController,
    snackBarHostState: SnackbarHostState,
) {
    NavHost(
        navController = navController,
        startDestination = BottomIcon.HOME.route,
        route = MAIN_NAV_GRAPH_NAME
    ) {
        // 홈 화면
        composable(BottomIcon.HOME.route) {
            HomeScreen(
                navController = navController,
                snackBarHostState = snackBarHostState
            )
        }
    }
}