package com.xxxape.exoplayer.ui

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.media3.common.util.UnstableApi
import com.xxxape.exoplayer.player.MediaPlayerState
import com.xxxape.exoplayer.widget.PlayerControlBar
import com.xxxape.exoplayer.widget.VideoSurface

/**
 * 简易媒体播放器（纯 Compose）：视频画布 + 封面 + 缓冲/错误 + 点击显隐控制条 + 顶部返回 + 底部控制条。
 *
 * - 点击视频区域显隐控制条（由 [showControls] 控制是否启用）。
 * - 全屏由 [MediaPlayerState] 管理，顶部返回在全屏时退出全屏。
 */
@OptIn(UnstableApi::class)
@Composable
fun SimpleMediaPlayer(
    playerState: MediaPlayerState,
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    showControls: Boolean = false,
    cover: @Composable () -> Unit = {},
) {
    var controlsVisible by remember { mutableStateOf(false) }

    VideoSurface(
        playerState = playerState,
        url = url,
        modifier = modifier,
        contentScale = contentScale,
        cover = cover,
        showControls = showControls,
        controlsVisible = controlsVisible,
        onTap = { controlsVisible = !controlsVisible },
        controls = { state, progressState ->
            PlayerControlBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                player = state.getPlayer(),
                progressState = progressState,
                isFullscreen = state.isFullscreen,
                onFullscreenClick = { state.toggleFullscreen() },
            )
        },
    )
}
