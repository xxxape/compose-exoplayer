package com.xxxape.exoplayer.widget

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.ContentFrame
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import androidx.media3.ui.compose.SurfaceType
import androidx.media3.ui.compose.state.ProgressStateWithTickInterval
import androidx.media3.ui.compose.state.rememberProgressStateWithTickInterval
import com.xxxape.exoplayer.player.MediaPlayerState

/**
 * 视频渲染区域（纯 Compose）：画布 + 封面 + 缓冲 + 错误。
 * 可选：点击显隐控制条、[controls] 插槽绘制顶部/底部控制条。
 */
@OptIn(UnstableApi::class)
@Composable
internal fun VideoSurface(
    playerState: MediaPlayerState,
    url: String,
    modifier: Modifier = Modifier,
    surfaceType: @SurfaceType Int = SURFACE_TYPE_TEXTURE_VIEW,
    contentScale: ContentScale = ContentScale.Fit,
    keepContentOnReset: Boolean = false,
    shutter: @Composable () -> Unit = {},
    cover: @Composable () -> Unit = {},
    showControls: Boolean = false,
    controlsVisible: Boolean = false,
    onTap: () -> Unit = {},
    controls: @Composable BoxScope.(MediaPlayerState, ProgressStateWithTickInterval) -> Unit = { _, _ -> },
) {
    LaunchedEffect(url) {
        playerState.loadMediaItem(url)
    }

    val player = playerState.getPlayer()
    val scope = rememberCoroutineScope()
    val progressState = rememberProgressStateWithTickInterval(
        player = player,
        tickIntervalMs = 500L,
        scope = scope,
    )
    LaunchedEffect(player) { progressState.observe() }

    val shouldShowCover by remember {
        derivedStateOf {
            !player.playWhenReady && progressState.currentPositionMs == 0L
        }
    }

    var isBuffering by remember { mutableStateOf(false) }
    var playbackError by remember { mutableStateOf<PlaybackException?>(null) }
    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsLoadingChanged(isLoading: Boolean) {
                isBuffering = isLoading
            }
            override fun onPlayerError(error: PlaybackException) {
                playbackError = error
            }
        }
        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }

    Box(
        modifier = modifier
            .then(
                if (showControls) Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onTap() }
                else Modifier
            )
    ) {
        ContentFrame(
            player = player,
            modifier = Modifier
                .fillMaxSize()
                .alpha(alpha = if (shouldShowCover) 0f else 1f),
            surfaceType = surfaceType,
            contentScale = contentScale,
            keepContentOnReset = keepContentOnReset,
            shutter = shutter,
        )

        if (shouldShowCover) {
            Box(modifier = Modifier.fillMaxSize()) {
                cover()
            }
        }

        playbackError?.let { error ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = error.message ?: "播放错误",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                )
            }
        }

        if (showControls && controlsVisible) {
            controls(playerState, progressState)
        }
    }
}
