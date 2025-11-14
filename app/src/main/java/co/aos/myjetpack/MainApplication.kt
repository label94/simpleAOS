package co.aos.myjetpack

import android.app.Application
import co.aos.firebase.appcheck.FirebaseInitializer
import dagger.hilt.android.HiltAndroidApp

/**
 * Application
 * */
@HiltAndroidApp
class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        // Firebase SDK Init
        FirebaseInitializer.initFirebaseSDK(this, BuildConfig.DEBUG)
    }
}