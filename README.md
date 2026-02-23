# Compose ExoPlayer

基于 Media3 ExoPlayer 的 Jetpack Compose 视频播放库，提供可复用的播放状态、全屏覆盖层与内置缓存。

## 功能概览

- **多格式支持**：自动根据 URL 推断并加载 DASH、HLS、普通渐进式（如 MP4）等流媒体。
- **视频缓存**：使用 Media3 `SimpleCache`（默认 20MB、LRU 淘汰），减少重复请求。
- **全屏播放**：支持竖屏小窗与横屏全屏切换，全屏时隐藏系统栏，支持返回键/手势退出全屏。
- **生命周期感知**：退后台自动暂停，回到前台时若之前在播放则自动恢复。
- **纯 Compose UI**：视频画布、封面、缓冲/错误态、点击显隐控制条、顶部返回、底部进度与全屏按钮，均可自定义或复用。

## 使用方法

### 1. 创建播放状态

在 Composable 中通过 `rememberMediaPlayerState` 创建并托管生命周期：

```kotlin
@Composable
fun MyScreen() {
    val playerState = rememberMediaPlayerState(
        repeatMode = Player.REPEAT_MODE_ONE,  // 可选：单曲循环等
        playWhenReady = true,                 // 可选：是否自动播放
    )
    // ...
}
```

同一界面若有多个播放器，可为每个视频各创建一个 `MediaPlayerState`。

### 2. 内嵌播放器

使用 `SimpleMediaPlayer` 展示视频 + 封面 + 控制条（点击显隐）：

```kotlin
SimpleMediaPlayer(
    playerState = playerState,
    url = "https://example.com/video.mp4",
    modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(16f / 9f),
    showControls = true,
    cover = { /* 自定义封面，如缩略图、占位 */ },
)
```

- `url`：视频地址，支持 http(s)、file 等；同一次 `url` 不会重复 load。
- `cover`：未开始播放或暂停在 0 秒时显示的封面内容。

### 3. 全屏覆盖层

当用户点击控制条「全屏」后，`playerState.isFullscreen == true`。在**根布局**（如 `Box`）内、列表之上放置 `FullScreenPlayerOverlay`，即可在顶层绘制全屏播放器并隐藏状态栏/导航栏：

```kotlin
Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn { /* 列表中的 SimpleMediaPlayer */ }

    FullScreenPlayerOverlay(
        playerState = playerState,
        url = url,
        cover = { /* 与内嵌时一致的封面 */ },
    )
}
```

退出全屏时由 Overlay 内部通过 `SimpleMediaPlayer` 的顶部返回键或系统返回键调用 `playerState.toggleFullscreen()`，并恢复系统栏。

### 4. 拦截返回键（全屏时先退出全屏）

在根或与 `FullScreenPlayerOverlay` 同级处添加 `MediaPlayerBackHandler`，使全屏时按返回键先退出全屏而不直接关页：

```kotlin
MediaPlayerBackHandler(playerState)
```

若有多个播放器，可为每个 `MediaPlayerState` 各调用一次（按需）。
