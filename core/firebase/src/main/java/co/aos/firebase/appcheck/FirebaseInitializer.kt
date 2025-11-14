package co.aos.firebase.appcheck

import android.content.Context
import co.aos.myutils.log.LogUtil
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

/**
 * Firebase 관련 SDK 초기화를 담당하는 싱글톤 객체
 * - Application 내에 한번 만 호출하기 때문에 굳이 Hilt 로 DI 관리를 하지 않음.
 * */
object FirebaseInitializer {

    /**
     * Firebase SDKs 초기화 (App Check 포함)
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
}