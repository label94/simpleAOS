package co.aos.myjetpack.intro.newintro

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.aos.guide.screen.GuideScreen
import co.aos.home.main.screen.MainRootUI
import co.aos.splash.screen.SplashScreen
import co.aos.user_feature.join.screen.JoinScreen
import co.aos.user_feature.login.legacy.screen.LoginScreen

/**
 * App Root
 * */
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController, startDestination = SPLASH_SCREEN) {

        // Splash
        composable(SPLASH_SCREEN) {
            SplashScreen(
                onFirstLaunch = {
                    // 앱 최초 실행 경우에만 접근권한 화면으로 이동
                    navController.navigate(PERMISSION_SCREEN) {
                        popUpTo(SPLASH_SCREEN) { inclusive = true } // 스택 제거
                    }
                },
                onNotFirstLaunch = {
                    // 그 외 로그인 화면으로 이동
                    navController.navigate(LOGIN_SCREEN) {
                        popUpTo(SPLASH_SCREEN) { inclusive = true } // 스택 제거
                    }
                }
            )
        }

        // Permission
        composable(PERMISSION_SCREEN) {
            GuideScreen(
                onComplete = {
                    // 앱 최초 실행 경우에만 접근권한 화면으로 이동
                    navController.navigate(LOGIN_SCREEN) {
                        popUpTo(PERMISSION_SCREEN) { inclusive = true } // 스택 제거
                    }
                }
            )
        }

        // Login
        composable(LOGIN_SCREEN) {
            LoginScreen(
                onLoginSuccess = {
                    // 로그인 성공 시 메인 화면으로 이동
                    navController.navigate(MAIN_SCREEN) {
                        popUpTo(LOGIN_SCREEN) { inclusive = true } // 스택 제거
                    }
                },
                onMoveUserJoinPage = {
                    // 회원가입 페이지로 이동
                    navController.navigate(JOIN_SCREEN)
                }
            )
        }

        // Join
        composable(JOIN_SCREEN) {
            JoinScreen(
                onBack = {
                    navController.navigate(LOGIN_SCREEN) {
                        popUpTo(JOIN_SCREEN) { inclusive = true } // 스택 제거
                    }
                },
                onJoinSuccess = {
                    navController.navigate(LOGIN_SCREEN) {
                        popUpTo(JOIN_SCREEN) { inclusive = true } // 스택 제거
                    }
                }
            )
        }

        // Main
        composable(MAIN_SCREEN) {
            MainRootUI()
        }
    }
}