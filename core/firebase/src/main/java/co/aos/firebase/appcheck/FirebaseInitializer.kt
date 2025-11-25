package co.aos.firebase.appcheck

import android.content.Context
import co.aos.myutils.log.LogUtil
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.AppCheckProvider
import com.google.firebase.appcheck.AppCheckProviderFactory
import com.google.firebase.appcheck.AppCheckToken
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

/**
 * Firebase 관련 SDK 초기화를 담당하는 싱글톤 객체
 * - Application 내에 한번 만 호출하기 때문에 굳이 Hilt 로 DI 관리를 하지 않음.
 * */
object FirebaseInitializer {

    /**
     * [기존 함수] Firebase SDKs 초기화 (App Check 포함)
     * - 디버그 시, 매번 새로운 토큰을 Logcat으로 생성합니다.
     * @param context Application Context
     * @param isDebug 디버그 모드 여부
     * */
    fun initFirebaseSDK(context: Context, isDebug: Boolean) {
        LogUtil.d(LogUtil.DEFAULT_TAG, "FirebaseInitializer.initFirebaseSDK() isDebug : $isDebug")

        // Firebase 초기화
        FirebaseApp.initializeApp(context)

        // Firebase App Check 초기화
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            if (isDebug) {
                // 디버그 빌드일 경우 : 디버그 공급자 사용
                DebugAppCheckProviderFactory.getInstance()
            } else {
                // 릴리즈 빌드일 경우 : Play Integrity 공급자 사용
                PlayIntegrityAppCheckProviderFactory.getInstance()
            }
        )
    }

    /** 앱체크 유효성 확인 용 출력 로그 */
    fun testLogToken() {
        // 토큰 유효성 확인 용도
        FirebaseAppCheck.getInstance().getAppCheckToken(false)
            .addOnSuccessListener { token ->
                LogUtil.d(LogUtil.DEFAULT_TAG, "token = ${token.token}")
            }
            .addOnFailureListener {
                LogUtil.e(LogUtil.DEFAULT_TAG, "getAppCheckToken error = $it")
            }
    }
}
