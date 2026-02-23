package com.xxxape.exoplayer.widget

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.ui.compose.buttons.PlayPauseButton
import androidx.media3.ui.compose.state.ProgressStateWithTickInterval

/**
 * 底部控制条（纯 Compose）：播放/暂停、当前时间、进度条、总时长、全屏。
 */
@OptIn(UnstableApi::class)
@Composable
fun PlayerControlBar(
    modifier: Modifier = Modifier,
    player: Player,
    progressState: ProgressStateWithTickInterval,
    isFullscreen: Boolean,
    onFullscreenClick: () -> Unit,
) {
    val currentMs = progressState.currentPositionMs
    val durationMs = progressState.durationMs
    val progressFraction =
        if (durationMs > 0L) (currentMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlayPauseButton(player = player) {
            IconButton(
                onClick = { if (isEnabled) onClick() },
                enabled = isEnabled,
            ) {
                Icon(
                    imageVector = if (showPlay) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                    contentDescription = if (showPlay) "播放" else "暂停",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp),
                )
            }
        }

        Text(
            text = Util.getStringForTime(currentMs),
            color = Color.White,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 8.dp)
        )

        Slider(
            value = progressFraction,
            onValueChange = { fraction ->
                if (durationMs > 0L) {
                    val pos = (fraction * durationMs).toLong().coerceIn(0L, durationMs)
                    player.seekTo(pos)
                }
            },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White.copy(alpha = 0.8f),
                inactiveTrackColor = Color.White.copy(alpha = 0.3f),
            ),
        )

        Text(
            text = Util.getStringForTime(durationMs),
            color = Color.White.copy(alpha = 0.9f),
            style = MaterialTheme.typography.bodySmall,
        )

        IconButton(onClick = onFullscreenClick) {
            Icon(
                imageVector = if (isFullscreen) Icons.Filled.FullscreenExit else Icons.Filled.Fullscreen,
                contentDescription = if (isFullscreen) "退出全屏" else "全屏",
                tint = Color.White,
                modifier = Modifier.size(28.dp),
            )
        }
    }
}
