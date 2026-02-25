package com.xxxape.exoplayer.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.xxxape.exoplayer.player.MediaPlayerState

/**
 * 可切换全屏的视频播放器
 */
@Composable
fun RotatableMediaPlayer(
    playerState: MediaPlayerState,
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    cover: @Composable () -> Unit = {},
) {
    if (playerState.isFullscreen) {
        Dialog(
            onDismissRequest = { playerState.toggleFullscreen() },
            properties = DialogProperties(
                decorFitsSystemWindows = false,
                usePlatformDefaultWidth = false
            )
        ) {
            SimpleMediaPlayer(
                playerState = playerState,
                url = url,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                showControls = true,
                cover = cover,
            )
        }
    } else {
        SimpleMediaPlayer(
            playerState = playerState,
            url = url,
            modifier = modifier,
            contentScale = contentScale,
            showControls = true,
            cover = cover,
        )
    }
}
