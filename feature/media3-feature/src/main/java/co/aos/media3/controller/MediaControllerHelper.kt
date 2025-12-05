package co.aos.media3.controller

import android.content.ComponentName
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import co.aos.media3.service.MusicService
import kotlinx.coroutines.guava.await

/**
 * Service와 통신할 MediaController 생성을 위한 Helper
 * - Service가 살아 있으면 Session에 붙고, 없으면 Service가 자동으로 뜬 뒤 연결 됨(Media3가 관리)
 * */

/** Media 서비스를 연결하고, 컨트롤러를 생성 */
@Composable
fun rememberMediaController(): MediaController? {
    val context = LocalContext.current
    var controller by remember { mutableStateOf<MediaController?>(null) }

    // MusicService 를 가리키는 SessionToken
    val sessionToken = remember {
        SessionToken(
            context,
            ComponentName(context, MusicService::class.java)
        )
    }

    // 비동기로 MediaController 생성
    LaunchedEffect(sessionToken) {
        // rememberMediaController() 가 처음 호출 될 때 단 한 번 실행되어 컨트롤러를 생성
        val mediaController = MediaController.Builder(context, sessionToken)
            .buildAsync()
            .await()
        controller = mediaController
    }

    // Composable 범위를 벗어나면 release
    DisposableEffect(sessionToken) {
        onDispose {
            controller?.release()
            controller = null
        }
    }
    return controller
}