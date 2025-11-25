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

// --- 고정 토큰 로직에 필요한 private 헬퍼 클래스들 ---

/**
 * 항상 고정된 "가짜" 토큰을 반환하는 커스텀 AppCheckProvider.
 */
private class SingleTokenDebugProvider(private val staticToken: String) : AppCheckProvider {
    override fun getToken(): Task<AppCheckToken> {
        val token = object : AppCheckToken() {
            override fun getToken() = staticToken
            override fun getExpireTimeMillis() = System.currentTimeMillis() + 3600000 // 1 hour
        }
        // AppCheckToken을 즉시 성공한 Task로 래핑하여 반환합니다.
        return Tasks.forResult(token)
    }
}

/**
 * 위에서 만든 Provider를 생성하는 커스텀 팩토리.
 */
private class SingleTokenDebugProviderFactory(private val staticToken: String) :
    AppCheckProviderFactory {
    override fun create(firebaseApp: FirebaseApp): AppCheckProvider {
        return SingleTokenDebugProvider(staticToken)
    }
}


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

    /**
     * [앱체크 디버그 용도] 고정된 디버그 토큰으로 App Check을 초기화합니다.
     * - 어떤 기기나 재설치 시에도 항상 동일한 토큰을 사용합니다.
     * @param context Application Context
     */
    fun initializeWithStaticToken(context: Context) {
        LogUtil.d(LogUtil.DEFAULT_TAG, "FirebaseInitializer.initializeWithStaticToken()")

        // Firebase 초기화
        FirebaseApp.initializeApp(context)

        // Firebase App Check 초기화
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            SingleTokenDebugProviderFactory("35d8cab9-7e67-4c27-9336-4fe87ab6b831")
        )
    }
}
