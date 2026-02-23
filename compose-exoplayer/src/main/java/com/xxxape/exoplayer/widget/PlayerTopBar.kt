package com.xxxape.exoplayer.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.xxxape.exoplayer.player.MediaPlayerState

/**
 * 顶部栏（纯 Compose）：返回按钮。全屏时退出全屏。
 */
@Composable
fun PlayerTopBar(
    modifier: Modifier = Modifier,
    playerState: MediaPlayerState,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = { playerState.toggleFullscreen() },
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "返回",
                tint = Color.White,
                modifier = Modifier.size(28.dp),
            )
        }
    }
}
