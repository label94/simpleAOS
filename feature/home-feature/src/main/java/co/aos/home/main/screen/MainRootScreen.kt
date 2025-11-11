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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import co.aos.home.bottombar.BottomBar
import co.aos.home.bottombar.Routes
import co.aos.home.calendar.screen.CalendarScreen
import co.aos.home.detail.load.screen.DiaryDetailScreen
import co.aos.home.detail.update.screen.DiaryUpdateScreen
import co.aos.home.editor.screen.DiaryEditorScreen
import co.aos.home.inspiration.screen.InspirationScreen
import co.aos.ui.theme.White

/**
 * 전역 적으로 NavController 를 공유/주입 하기 위한 구조
 *
 * @property LocalAppNavController : 전역으로 사용할 NavController
 * @property MAIN_NAV_GRAPH_NAME : 메인 화면 네비게이션 그래프 이름
 * */
const val MAIN_NAV_GRAPH_NAME = "main_graph"

/** Composable 어디서든 NavController 접근할 수 있도록 Local 제공 */
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

        // Surface 로 감싼 바텀바(고정 하단 바)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(64.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
                .navigationBarsPadding(),
            color = White,
        ) {
            BottomBar(navController)
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
        startDestination = Routes.HOME,
        route = MAIN_NAV_GRAPH_NAME
    ) {
        // 홈 화면
        composable(Routes.HOME) {
            HomeScreen(
                snackBarHostState = snackBarHostState,
                onOpenEditor = {
                    navController.navigate(Routes.EDITOR)
                },
                onOpenDetail = { entryId ->
                    navController.navigate(Routes.detail(entryId))
                },
                onOpenSearch = {
                    navController.navigate(Routes.SEARCH)
                },
                navController = navController
            )
        }

        // 달력
        composable(Routes.CALENDAR) {
            CalendarScreen(
                onBack = {
                    navController.navigate(Routes.HOME) {
                        // 백 스택의 시작점(HOME)까지 모든 스택을 제거합니다.
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // 이미 HOME에 있다면 새 화면을 만들지 않습니다.
                        launchSingleTop = true
                        // HOME 화면의 이전 상태를 복원합니다.
                        restoreState = true
                    }
                },
                onOpenDetail = { entryId ->
                    navController.navigate(Routes.detail(entryId))
                },
            )
        }

        // 영감
        composable(Routes.INSPIRATION) {
            InspirationScreen(
                onBack = {
                    navController.navigate(Routes.HOME) {
                        // 백 스택의 시작점(HOME)까지 모든 스택을 제거합니다.
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // 이미 HOME에 있다면 새 화면을 만들지 않습니다.
                        launchSingleTop = true
                        // HOME 화면의 이전 상태를 복원합니다.
                        restoreState = true
                    }
                },
                onNavigateToWrite = {}
            )
        }

        // 프로필(마이페이지)
        composable(Routes.PROFILE) {

        }

        // 작성
        composable(Routes.EDITOR) {
            DiaryEditorScreen(
                onClose = { navController.popBackStack() },
                onSaved = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(Routes.REFRESH_LIST, true)
                    navController.popBackStack()
                }
            )
        }

        // 상세
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("entryId") { nullable = false })
        ) { backStack ->
            val id = backStack.arguments?.getString("entryId") ?: ""
            DiaryDetailScreen(
                entryId = id,
                onClose = {
                    navController.popBackStack()
                },
                onEdit = { id ->
                    navController.navigate(Routes.update(id))
                },
                onRefreshRequest = {
                    // 상세 화면에서 데이터 갱신이 발생한 경우, 이전 화면에서도 데이터 갱신을 위해 Flag 설정
                    navController.previousBackStackEntry?.savedStateHandle?.set(Routes.REFRESH_LIST, true)
                    navController.popBackStack()
                },
                navController = navController
            )
        }

        // 수정
        composable(
            route = Routes.UPDATE,
            arguments = listOf(navArgument("entryId") { nullable = false })
        ) { backStack ->
            val id = backStack.arguments?.getString("entryId") ?: ""

            DiaryUpdateScreen(
                entryId = id,
                onClose = {
                    navController.popBackStack()
                },
                onSaved = {
                    // 저장 후 상세 화면도 업데이트를 위해 요청
                    navController.previousBackStackEntry?.savedStateHandle?.set(Routes.REFRESH_DETAIL, true)

                    // 수정 화면 닫기
                    navController.popBackStack()
                }
            )
        }


        // 검색
        composable(Routes.SEARCH) {
            // todo : 검색 화면 개발 필요
        }
    }
}