package co.aos.media3.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import co.aos.media3.utils.MediaCommonConstants
import co.aos.myutils.log.LogUtil

/**
 * Media3 세션 서비스 정의
 * */
class MusicService: MediaSessionService() {

    private var player: ExoPlayer? = null
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        LogUtil.d(LogUtil.MEDIA3_LOG_TAG, "onCreate() call")

        // 1) 플레이어 생성
        val exoPlayer = ExoPlayer.Builder(this).build()
        player = exoPlayer

        // 2) MediaSession 생성
        val session = MediaSession.Builder(this, exoPlayer)
            .setId(MediaCommonConstants.SESSION_ID)
            .build()
        mediaSession = session

        // 3) 알림 채널 생성
        createNotificationChannelIfNeeded()

        // 4) 프로바이더 생성
        val notificationProvider: MediaNotification.Provider =
            DefaultMediaNotificationProvider.Builder(this)
                .setChannelId(MediaCommonConstants.NOTIFICATION_CHANNEL_ID)
                .setChannelName(0).build()

        setMediaNotificationProvider(notificationProvider)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        // 이 세션에 붙은 컨트롤러(해당 앱 UI / 시스템 허용)
        LogUtil.d(LogUtil.MEDIA3_LOG_TAG, "onGetSession()")
        return mediaSession
    }

    /** 알림채널 생성 */
    private fun createNotificationChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                MediaCommonConstants.NOTIFICATION_CHANNEL_ID,
                "미디어 테스트",
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)
        }
    }

    /** onDestroy */
    override fun onDestroy() {
        LogUtil.d(LogUtil.MEDIA3_LOG_TAG, "onDestroy() call!")
        mediaSession?.release()
        mediaSession = null
        player?.release()
        player = null
        super.onDestroy()
    }
}