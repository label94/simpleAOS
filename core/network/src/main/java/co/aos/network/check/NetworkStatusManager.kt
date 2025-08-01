package co.aos.network.check

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import co.aos.myutils.log.LogUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

/**
 * 네트워크 감지 할 수 있는 유틸 클래스
 * */
class NetworkStatusManager @Inject constructor(
    @ApplicationContext private val context: Context
){
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager

    val networkStatus: Flow<Boolean> = callbackFlow {
        val callback = object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                LogUtil.d(LogUtil.NET_STATE_LOG_TAG, "onAvailable()")
                trySend(true)
            }

            override fun onLost(network: Network) {
                LogUtil.e(LogUtil.NET_STATE_LOG_TAG, "onLost()")
                trySend(false)
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) // 인터넷 연결 가능한 네트워크만 감지
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, callback)

        val entity = isConnected(connectivityManager)
        trySend(entity)

        // flow 종료 시 콜백 해제
        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    /** 현재 네트워크 상태 확인 */
    private fun isConnected(manager: ConnectivityManager): Boolean {
        val network = manager.activeNetwork ?: return false
        val capabilities = manager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}