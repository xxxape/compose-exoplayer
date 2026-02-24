package com.xxxape.exoplayer.player

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.xxxape.exoplayer.cache.VideoDataSourceHolder
import com.xxxape.exoplayer.util.getActivity

@Stable
class MediaPlayerState(
    val context: Context,
    repeatMode: Int = Player.REPEAT_MODE_ONE,
    playWhenReady: Boolean = true,
) {
    var isFullscreen by mutableStateOf(false)
        private set

    private val exoPlayer = ExoPlayer.Builder(context)
        .build()
        .apply {
            this.repeatMode = repeatMode
            this.playWhenReady = playWhenReady
        }

    private var currentUrl: String? = null
    private var listener: Player.Listener? = null

    /** 退后台前是否在播放，用于 onResume 时仅在此为 true 时恢复播放 */
    private var wasPlayingBeforePause: Boolean = false

    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onPause(owner: LifecycleOwner) {
            wasPlayingBeforePause = exoPlayer.isPlaying
            exoPlayer.pause()
        }
        override fun onResume(owner: LifecycleOwner) {
            if (wasPlayingBeforePause) exoPlayer.play()
        }
    }
    private var lifecycle: Lifecycle? = null

    init {
        (context.getActivity() as? LifecycleOwner)?.lifecycle?.let {
            lifecycle = it
            it.addObserver(lifecycleObserver)
        }
    }

    @OptIn(UnstableApi::class)
    fun loadMediaItem(url: String) {
        if (currentUrl == url) return
        currentUrl = url
        val uri = url.toUri()
        val mediaItem = MediaItem.fromUri(uri)
        val dataSourceFactory = VideoDataSourceHolder.getCacheFactory(context)
        val mediaSource = when (Util.inferContentType(uri)) {
            C.CONTENT_TYPE_DASH -> DashMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
            C.CONTENT_TYPE_HLS -> HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
            else -> ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
        }
        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()
    }

    fun setPlayerListener(playerListener: Player.Listener) {
        listener?.let { exoPlayer.removeListener(it) }
        listener = playerListener
        exoPlayer.addListener(playerListener)
    }

    fun getPlayer(): Player = exoPlayer

    @SuppressLint("SourceLockedOrientationActivity")
    fun toggleFullscreen() {
        isFullscreen = !isFullscreen
        context.getActivity()?.let { activity ->
            activity.requestedOrientation = if (isFullscreen) {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
    }

    fun release() {
        lifecycle?.removeObserver(lifecycleObserver)
        lifecycle = null
        listener?.let {
            exoPlayer.removeListener(it)
            listener = null
        }
        exoPlayer.release()
    }
}

/**
 * 拦截系统返回/手势返回：全屏时退出全屏并消费事件，非全屏时不消费，交给下层或系统。
 * 应在根或与 [FullScreenPlayerOverlay] 同级处调用，多个播放器时可为每个 [MediaPlayerState] 各调用一次。
 */
@Composable
fun MediaPlayerBackHandler(playerState: MediaPlayerState) {
    val isFullscreen = playerState.isFullscreen
    BackHandler(enabled = isFullscreen) {
        playerState.toggleFullscreen()
    }
}

@Composable
fun rememberMediaPlayerState(
    repeatMode: Int = Player.REPEAT_MODE_ONE,
    playWhenReady: Boolean = true,
): MediaPlayerState {
    val context = LocalContext.current
    val playerState = remember { MediaPlayerState(context, repeatMode, playWhenReady) }
    DisposableEffect(Unit) {
        onDispose { playerState.release() }
    }
    return playerState
}
