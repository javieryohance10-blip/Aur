package com.aura.music.ui.player

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Lyrics
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

enum class RepeatMode { Off, All, One }

data class PlayerUiState(
    val title: String,
    val artist: String,
    val artwork: Any? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 1L,
    val isFavorite: Boolean = false,
    val shuffle: Boolean = false,
    val repeat: RepeatMode = RepeatMode.Off,
)

data class PlayerActions(
    val onBack: () -> Unit = {},
    val onMore: () -> Unit = {},
    val onPlayPause: () -> Unit = {},
    val onNext: () -> Unit = {},
    val onPrevious: () -> Unit = {},
    val onSeek: (Long) -> Unit = {},
    val onFavorite: () -> Unit = {},
    val onShuffle: () -> Unit = {},
    val onRepeat: () -> Unit = {},
    val onLyrics: () -> Unit = {},
    val onQueue: () -> Unit = {},
)

private val GlassFill = Brush.linearGradient(
    listOf(Color.White.copy(alpha = 0.18f), Color.White.copy(alpha = 0.05f))
)
private val GlassStroke = Brush.linearGradient(
    listOf(Color.White.copy(alpha = 0.55f), Color.White.copy(alpha = 0.06f), Color.White.copy(alpha = 0.28f))
)
private val Muted = Color.White.copy(alpha = 0.65f)

fun Modifier.glass(shape: Shape = RoundedCornerShape(28.dp)): Modifier =
    this
        .clip(shape)
        .background(GlassFill)
        .drawBehind {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.10f), Color.Transparent),
                    endY = size.height * 0.45f,
                ),
                size = Size(size.width, size.height),
            )
        }
        .border(1.dp, GlassStroke, shape)

@Composable
fun LiquidGlassPlayer(
    state: PlayerUiState,
    actions: PlayerActions,
    selectedTab: Int,
    onTabSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize()) {
        LiquidBackdrop(state.artwork)
        Column(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
        ) {
            TopBar(actions)
            Spacer(Modifier.height(20.dp))
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                ArtworkCard(state.artwork, state.isPlaying)
            }
            Spacer(Modifier.height(20.dp))
            TrackInfo(state, actions)
            Spacer(Modifier.height(4.dp))
            SeekBar(state.positionMs, state.durationMs, actions.onSeek)
            Spacer(Modifier.height(10.dp))
            Controls(state, actions)
            Spacer(Modifier.height(14.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GlassChip(Icons.Rounded.Lyrics, "Lyrics", actions.onLyrics, Modifier.weight(1f))
                GlassChip(Icons.Rounded.QueueMusic, "Queue", actions.onQueue, Modifier.weight(1f))
            }
            Spacer(Modifier.height(14.dp))
            GlassNavBar(selectedTab, onTabSelect)
        }
    }
}

@Composable
private fun LiquidBackdrop(artwork: Any?) {
    Box(Modifier.fillMaxSize().background(Color(0xFF050505))) {
        AsyncImage(
            model = artwork,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().scale(1.4f).blur(90.dp),
            alpha = 0.9f,
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(Color.Black.copy(alpha = 0.35f), Color.Black.copy(alpha = 0.78f))
                )
            )
        )
    }
}

@Composable
private fun TopBar(actions: PlayerActions) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        GlassIconButton(Icons.Rounded.KeyboardArrowDown, "Close player", actions.onBack)
        Text(
            "NOW PLAYING",
            modifier = Modifier.weight(1f),
            color = Muted,
            fontSize = 12.sp,
            letterSpacing = 2.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        GlassIconButton(Icons.Rounded.MoreVert, "More options", actions.onMore)
    }
}

@Composable
private fun ArtworkCard(artwork: Any?, isPlaying: Boolean) {
    val scale by animateFloatAsState(
        targetValue = if (isPlaying) 1f else 0.9f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "artScale",
    )
    val shape = RoundedCornerShape(36.dp)
    Box(
        Modifier
            .aspectRatio(1f)
            .scale(scale)
            .shadow(32.dp, shape, ambientColor = Color.Black, spotColor = Color.Black)
            .clip(shape)
            .border(1.dp, GlassStroke, shape),
    ) {
        AsyncImage(
            model = artwork,
            contentDescription = "Album artwork",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun TrackInfo(state: PlayerUiState, actions: PlayerActions) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(
                state.title,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                state.artist,
                color = Muted,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        GlassIconButton(
            icon = if (state.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
            desc = if (state.isFavorite) "Remove from favorites" else "Add to favorites",
            onClick = actions.onFavorite,
        )
    }
}

@Composable
private fun SeekBar(positionMs: Long, durationMs: Long, onSeek: (Long) -> Unit) {
    var dragging by remember { mutableStateOf<Float?>(null) }
    val progress = dragging ?: (positionMs.toFloat() / durationMs.coerceAtLeast(1L)).coerceIn(0f, 1f)
    Column {
        Slider(
            value = progress,
            onValueChange = { dragging = it },
            onValueChangeFinished = {
                dragging?.let { onSeek((it * durationMs).toLong()) }
                dragging = null
            },
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.White.copy(alpha = 0.2f),
            ),
        )
        Row(Modifier.fillMaxWidth().padding(horizontal = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(formatTime((progress * durationMs).toLong()), color = Muted, fontSize = 12.sp)
            Text(formatTime(durationMs), color = Muted, fontSize = 12.sp)
        }
    }
}

@Composable
private fun Controls(state: PlayerUiState, actions: PlayerActions) {
    Row(
        Modifier.fillMaxWidth().glass(RoundedCornerShape(40.dp)).padding(horizontal = 12.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlainIconButton(
            Icons.Rounded.Shuffle, "Shuffle", actions.onShuffle,
            tint = if (state.shuffle) Color.White else Muted,
        )
        PlainIconButton(Icons.Rounded.SkipPrevious, "Previous", actions.onPrevious, size = 52.dp)
        Box(
            Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable(onClick = actions.onPlayPause),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                if (state.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                contentDescription = if (state.isPlaying) "Pause" else "Play",
                tint = Color.Black,
                modifier = Modifier.size(40.dp),
            )
        }
        PlainIconButton(Icons.Rounded.SkipNext, "Next", actions.onNext, size = 52.dp)
        PlainIconButton(
            if (state.repeat == RepeatMode.One) Icons.Rounded.RepeatOne else Icons.Rounded.Repeat,
            "Repeat", actions.onRepeat,
            tint = if (state.repeat == RepeatMode.Off) Muted else Color.White,
        )
    }
}

@Composable
private fun GlassChip(icon: ImageVector, label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier.glass(RoundedCornerShape(24.dp)).clickable(onClick = onClick).padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        Spacer(Modifier.size(8.dp))
        Text(label, color = Color.White, fontSize = 14.sp)
    }
}

@Composable
fun GlassNavBar(selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    val items = listOf(
        Icons.Rounded.Home to "Home",
        Icons.Rounded.Search to "Search",
        Icons.Rounded.LibraryMusic to "Library",
        Icons.Rounded.Settings to "Settings",
    )
    Row(
        modifier.fillMaxWidth().glass(RoundedCornerShape(34.dp)).padding(6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        items.forEachIndexed { i, (icon, label) ->
            val isSelected = i == selected
            val bg by animateColorAsState(
                if (isSelected) Color.White.copy(alpha = 0.20f) else Color.Transparent,
                label = "navBg",
            )
            Column(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(28.dp))
                    .background(bg)
                    .clickable { onSelect(i) }
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(icon, label, tint = if (isSelected) Color.White else Muted, modifier = Modifier.size(24.dp))
                Text(label, color = if (isSelected) Color.White else Muted, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun GlassIconButton(
    icon: ImageVector,
    desc: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
) {
    Box(
        modifier.size(size).glass(CircleShape).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = desc, tint = Color.White, modifier = Modifier.size(size * 0.5f))
    }
}

@Composable
private fun PlainIconButton(
    icon: ImageVector,
    desc: String,
    onClick: () -> Unit,
    size: Dp = 44.dp,
    tint: Color = Color.White,
) {
    Box(
        Modifier.size(size).clip(CircleShape).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = desc, tint = tint, modifier = Modifier.size(size * 0.55f))
    }
}

private fun formatTime(ms: Long): String {
    val total = (ms / 1000L).coerceAtLeast(0L)
    return "%d:%02d".format(total / 60, total % 60)
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun LiquidGlassPlayerPreview() {
    var tab by remember { mutableStateOf(0) }
    LiquidGlassPlayer(
        state = PlayerUiState(
            title = "Midnight Drive",
            artist = "AURA Sessions",
            isPlaying = true,
            positionMs = 74_000,
            durationMs = 212_000,
            isFavorite = true,
        ),
        actions = PlayerActions(),
        selectedTab = tab,
        onTabSelect = { tab = it },
    )
}
