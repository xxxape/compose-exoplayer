package com.xxxape.exoplayer.widget.controller

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType.Companion.PrimaryNotEditable
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.Util
import com.xxxape.exoplayer.player.MediaPlayerState

/**
 * 播放器控制条
 *
 * @author zhuzixuan
 */

/**
 * 非全屏控制条
 */
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun NonFullscreenControlBar(playerState: MediaPlayerState) {
    NonFullscreenControlBar(
        isPlaying = playerState.isPlaying,
        currentMs = playerState.progressState.currentPositionMs,
        durationMs = playerState.progressState.durationMs,
        onPlayClick = playerState::togglePlayPause,
        onFullscreenClick = playerState::toggleFullscreen,
        seekToPosition = playerState::seekTo,
        onSpeedChanged = playerState::setSpeed
    )
}

/**
 * 全屏控制条
 */
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun FullscreenControlBar(playerState: MediaPlayerState) {
    FullscreenControlBar(
        isPlaying = playerState.isPlaying,
        currentMs = playerState.progressState.currentPositionMs,
        durationMs = playerState.progressState.durationMs,
        onPlayClick = playerState::togglePlayPause,
        onQuitFullscreenClick = playerState::toggleFullscreen,
        seekToPosition = playerState::seekTo,
        onSpeedChanged = playerState::setSpeed
    )
}

/**
 * 非全屏控制条
 */
@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun NonFullscreenControlBar(
    isPlaying: Boolean,
    currentMs: Long,
    durationMs: Long,
    onPlayClick: () -> Unit,
    onFullscreenClick: () -> Unit,
    seekToPosition: (Long) -> Unit,
    onSpeedChanged: (Float) -> Unit
) {
    var newCurrentMs by remember { mutableStateOf<Long?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 中间一个大大的暂停继续按钮
        // todo 往上移一点，微调下位置，颜色也可以再调一下
        IconButton(
            onClick = onPlayClick,
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.Center),
            enabled = true
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = Color.Black.copy(alpha = 0.7f),
                modifier = Modifier.size(64.dp)
            )
        }

        // 底部条
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(Color.Black)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // 进度条
            BottomProgressBar(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth(),
                progress = { (newCurrentMs ?: currentMs) / durationMs.toFloat() },
                onDragProgressUpdate = { progress ->
                    newCurrentMs = (progress * durationMs).toLong().coerceIn(0L, durationMs)
                },
                onDragEnd = {
                    newCurrentMs?.let {
                        seekToPosition(it)
                        newCurrentMs = null
                    }
                }
            )

            Row(
                modifier = Modifier.align(Alignment.BottomStart),
                verticalAlignment = Alignment.Bottom
            ) {
                // 时间
                Text(
                    text = Util.getStringForTime(newCurrentMs ?: currentMs) + "/" + Util.getStringForTime(
                        durationMs
                    ),
                    modifier = Modifier.weight(1f),
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                )

                // 倍速
                SpeedItem(
                    modifier = Modifier.height(40.dp),
                    contentAlignment = Alignment.BottomCenter,
                    onSpeedChanged = onSpeedChanged
                )

                // 全屏
                IconButton(
                    onClick = onFullscreenClick,
                    modifier = Modifier.size(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Fullscreen,
                        contentDescription = "全屏",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

/**
 * 全屏控制条
 */
@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun FullscreenControlBar(
    isPlaying: Boolean,
    currentMs: Long,
    durationMs: Long,
    onPlayClick: () -> Unit,
    onQuitFullscreenClick: () -> Unit,
    seekToPosition: (Long) -> Unit,
    onSpeedChanged: (Float) -> Unit
) {
    var newCurrentMs by remember { mutableStateOf<Long?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 中间一个大大的暂停继续按钮
        IconButton(
            onClick = onPlayClick,
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.Center),
            enabled = true
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = Color.Black.copy(alpha = 0.7f),
                modifier = Modifier.size(64.dp)
            )
        }

        // 底部黑条
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(color = Color.Black)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 当前时间
            Text(
                text = Util.getStringForTime(newCurrentMs ?: currentMs),
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
            )
            // 进度条
            BottomProgressBar(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                progress = { (newCurrentMs ?: currentMs) / durationMs.toFloat() },
                onDragProgressUpdate = { progress ->
                    newCurrentMs = (progress * durationMs).toLong().coerceIn(0L, durationMs)
                },
                onDragEnd = {
                    newCurrentMs?.let {
                        seekToPosition(it)
                        newCurrentMs = null
                    }
                }
            )
            // 总时间
            Text(
                text = Util.getStringForTime(durationMs),
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
            )
            // 倍速
            SpeedItem(
                modifier = Modifier.size(60.dp, 32.dp),
                contentAlignment = Alignment.Center,
                onSpeedChanged = onSpeedChanged
            )

            // 退出全屏
            IconButton(
                onClick = onQuitFullscreenClick,
                modifier = Modifier.size(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.FullscreenExit,
                    contentDescription = "退出全屏",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

/**
 * 底部进度条
 */
@Composable
private fun BottomProgressBar(
    modifier: Modifier,
    progress: () -> Float,
    onDragProgressUpdate: (Float) -> Unit,
    onDragEnd: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(20.dp)
            // 左右拖拽，拖拽时进度条显示拖拽进度，拖拽结束后调用 onDrag
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { change, _ ->
                        onDragProgressUpdate(change.position.x.coerceIn(0f, size.width.toFloat()) / size.width)
                        change.consume()
                    },
                    onDragEnd = {
                        onDragEnd()
                    }
                )
            }
    ) {
        // 底部条
        Box(
            modifier = Modifier
                .height(8.dp)
                .fillMaxWidth()
                .background(
                    color = Color.White.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                )
        )

        // 进度条
        Box(
            modifier = Modifier
                .height(8.dp)
                .fillMaxWidth(fraction = progress())
                .background(
                    color = Color.White.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(8.dp)
                )
        )
    }
}

/**
 * 倍速组件
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SpeedItem(
    modifier: Modifier,
    contentAlignment: Alignment,
    onSpeedChanged: (Float) -> Unit
) {
    var currentSpeedSelected by remember { mutableFloatStateOf(1f) }
    var speedMenuExpanded by remember { mutableStateOf(false) }
    val speedList = listOf(2f, 1.5f, 1f, 0.5f)
    ExposedDropdownMenuBox(
        expanded = speedMenuExpanded,
        onExpandedChange = { speedMenuExpanded = it },
    ) {
        Box(
            modifier = modifier
                .clickable { speedMenuExpanded = true }
                .menuAnchor(PrimaryNotEditable),
            contentAlignment = contentAlignment
        ) {
            BasicTextField(
                value = if (currentSpeedSelected == 1f) "倍速" else "x${currentSpeedSelected}",
                onValueChange = {},
                readOnly = true,
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                ),
            )
        }
        ExposedDropdownMenu(
            expanded = speedMenuExpanded,
            onDismissRequest = { speedMenuExpanded = false },
            modifier = Modifier
                .exposedDropdownSize(matchAnchorWidth = false)
                .background(color = Color.Black)
        ) {
            speedList.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.toString(),
                            color = Color.White,
                            fontSize = 15.sp,
                            maxLines = 1,
                            autoSize = TextAutoSize.StepBased(
                                minFontSize = 8.sp,
                                maxFontSize = 15.sp
                            )
                        )
                    },
                    onClick = {
                        currentSpeedSelected = option
                        speedMenuExpanded = false
                        onSpeedChanged(option)
                    },
                    contentPadding = PaddingValues(horizontal = 20.dp)
                )
            }
        }
    }
}

@Preview(widthDp = 480, heightDp = 270, showBackground = true)
@Composable
private fun PreviewNonFullscreenControlBar() {
    var isPlaying by remember { mutableStateOf(true) }
    NonFullscreenControlBar(
        isPlaying = isPlaying,
        currentMs = 1000000,
        durationMs = 5000000,
        onPlayClick = { isPlaying = !isPlaying },
        onFullscreenClick = {},
        seekToPosition = {},
        onSpeedChanged = {}
    )
}

@Preview(widthDp = 480, heightDp = 270, showBackground = true)
@Composable
private fun PreviewFullscreenControlBar() {
    var isPlaying by remember { mutableStateOf(true) }
    FullscreenControlBar(
        isPlaying = isPlaying,
        currentMs = 1000000,
        durationMs = 5000000,
        onPlayClick = { isPlaying = !isPlaying },
        onQuitFullscreenClick = {},
        seekToPosition = {},
        onSpeedChanged = {}
    )
}
