package co.aos.network.check

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import co.aos.myutils.log.LogUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * 네트워크 감지 할 수 있는 유틸 클래스
 * */
class NetworkStatusManager @Inject constructor(
    @ApplicationContext private val context: Context
){
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager

    /** 네트워크 상태 변화 리스너 설정 */
    fun registerNetworkCallback(networkCallback: NetworkCallback) {
        // 네트워크 상태 변화 감지 요청
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) // 인터넷 연결 가능한 네트워크만 감지
            .build()

        LogUtil.d(LogUtil.NET_STATE_LOG_TAG, "registerNetworkCallback")

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    /** 콜백 해제 */
    fun unregisterNetworkCallback(networkCallback: NetworkCallback) {
        LogUtil.d(LogUtil.NET_STATE_LOG_TAG, "unregisterNetworkCallback")
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}