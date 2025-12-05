package co.aos.media3.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import co.aos.media3.controller.rememberMediaController

/**
 * 심플 음악 플레이어 화면
 * */
@Composable
fun MusicPlayerScreen(
    controller: MediaController? = rememberMediaController(),
    modifier: Modifier = Modifier
) {
    if (controller == null) {
        // 아직 컨트롤러 연결 전
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // 상태 관련 변수 정의
    var isPlaying by remember { mutableStateOf(controller.isPlaying) }
    var title by remember { mutableStateOf(controller.currentMediaItem?.mediaMetadata?.title?.toString() ?: "") }

    // Player.Listener로 상태 변화 감지
    DisposableEffect(controller) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                isPlaying = isPlayingNow
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                title = mediaItem?.mediaMetadata?.title?.toString() ?: ""
            }
        }

        controller.addListener(listener)

        onDispose {
            // 해당 compose 벗어날 경우 리소스 해제 작업 수행
            controller.removeListener(listener)
        }
    }

    // UI
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title.ifEmpty { "재생할 곡을 선택해주세요." },
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    // 샘플 곡 1개 세팅 후 재생
                    val sampleItem = MediaItem.Builder()
                        .setUri("https://storage.googleapis.com/exoplayer-test-media-0/play.mp3")
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setTitle("Sample Song")
                                .build()
                        ).build()

                    controller.setMediaItem(sampleItem)
                    controller.prepare()
                    controller.play()
                }
            ) {
                Text("재생")
            }

            FilledTonalButton(
                onClick = {
                    if (controller.isPlaying) controller.pause() else controller.play()
                }
            ) {
                Text(if (isPlaying) "일시정지" else "재생")
            }
        }
    }
}