package com.xxxape.exoplayer.ui

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.util.UnstableApi
import com.xxxape.exoplayer.player.MediaPlayerState

/**
 * 全屏播放器层：当 [playerState].[MediaPlayerState.isFullscreen] 为 true 时在根布局上绘制全屏播放器，
 * 并隐藏状态栏/导航栏；退出全屏时恢复系统栏。
 *
 * 使用方式：在 Activity setContent 的根布局（如 Box）内、列表之上调用本 Composable，
 * 例如 `Box { LazyColumn { ... }; FullScreenPlayerOverlay(...) }`。
 */
@OptIn(UnstableApi::class)
@Composable
fun FullScreenPlayerOverlay(
    playerState: MediaPlayerState,
    url: String,
    cover: @Composable () -> Unit = {},
) {
    if (!playerState.isFullscreen) return

    val view = LocalView.current
    DisposableEffect(Unit) {
        val controller = ViewCompat.getWindowInsetsController(view)
        controller?.hide(WindowInsetsCompat.Type.systemBars())
        controller?.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        onDispose {
            controller?.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        SimpleMediaPlayer(
            playerState = playerState,
            url = url,
            modifier = Modifier.fillMaxSize(),
            showControls = true,
            cover = cover,
        )
    }
}
