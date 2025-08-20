package co.aos.myjetpack.intro.newintro

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

/** Screen 이름 */
const val MAIN_SCREEN = "main" // 메인 화면
const val SPLASH_SCREEN = "splash" // 스플래시 화면
const val PERMISSION_SCREEN = "permission" // 접근권한 안내 화면
const val LOGIN_SCREEN = "login" // 로그인 화면
const val JOIN_SCREEN = "join" // 회원가입 화면

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