package com.aura.music.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

private val SoftWhite = Color.White.copy(alpha = 0.65f)

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val artwork: Any? = null,
    val durationMs: Long = 0L,
)

enum class LibraryTab(val label: String) {
    Songs("Songs"), Albums("Albums"), Artists("Artists"), Playlists("Playlists")
}

data class LibraryEntry(
    val id: String,
    val title: String,
    val subtitle: String,
    val artwork: Any? = null,
)

@Composable
fun GlassBackdrop(artwork: Any?, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().background(Color(0xFF050505))) {
        AsyncImage(
            model = artwork,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().scale(1.4f).blur(90.dp),
            alpha = 0.85f,
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(Color.Black.copy(alpha = 0.4f), Color.Black.copy(alpha = 0.82f))
                )
            )
        )
    }
}

@Composable
fun GlassScaffold(
    artwork: Any?,
    modifier: Modifier = Modifier,
    bottomBar: @Composable ColumnScope.() -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier.fillMaxSize()) {
        GlassBackdrop(artwork)
        Column(Modifier.fillMaxSize().systemBarsPadding()) {
            Box(Modifier.weight(1f).fillMaxWidth(), content = content)
            Column(Modifier.padding(horizontal = 20.dp).padding(bottom = 12.dp), content = bottomBar)
        }
    }
}

@Composable
private fun ArtThumb(artwork: Any?, size: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier.size(size).clip(RoundedCornerShape(size * 0.26f)).background(Color.White.copy(alpha = 0.08f)),
    ) {
        AsyncImage(
            model = artwork,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
fun TrackRow(
    title: String,
    subtitle: String,
    artwork: Any?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (highlighted) Color.White.copy(alpha = 0.12f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ArtThumb(artwork, 52.dp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(subtitle, color = SoftWhite, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        trailing?.invoke()
    }
}

@Composable
fun MiniPlayer(
    track: Track,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier.fillMaxWidth().glass(RoundedCornerShape(28.dp)).clickable(onClick = onOpen).padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ArtThumb(track.artwork, 48.dp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(track.title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(track.artist, color = SoftWhite, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Box(
            Modifier.size(42.dp).clip(CircleShape).background(Color.White).clickable(onClick = onPlayPause),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = Color.Black,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(Modifier.width(4.dp))
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
}

@Composable
fun HomeScreen(
    greeting: String,
    quickPicks: List<Track>,
    recent: List<Track>,
    favorites: List<Track>,
    nowPlaying: Track?,
    isPlaying: Boolean,
    onTrackClick: (Track) -> Unit,
    onPlayPause: () -> Unit,
    onOpenPlayer: () -> Unit,
    selectedTab: Int,
    onTabSelect: (Int) -> Unit,
) {
    GlassScaffold(
        artwork = nowPlaying?.artwork,
        bottomBar = {
            if (nowPlaying != null) {
                MiniPlayer(nowPlaying, isPlaying, onPlayPause, onOpenPlayer)
                Spacer(Modifier.height(10.dp))
            }
            GlassNavBar(selectedTab, onTabSelect)
        },
    ) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            item { Text(greeting, color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold) }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionTitle("Quick picks")
                    quickPicks.chunked(2).forEach { pair ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            pair.forEach { t ->
                                Row(
                                    Modifier
                                        .weight(1f)
                                        .glass(RoundedCornerShape(22.dp))
                                        .clickable { onTrackClick(t) }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    ArtThumb(t.artwork, 44.dp)
                                    Spacer(Modifier.width(10.dp))
                                    Text(t.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                }
                            }
                            if (pair.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionTitle("Recently played")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        items(recent, key = { it.id }) { t ->
                            Column(Modifier.width(132.dp).clickable { onTrackClick(t) }) {
                                ArtThumb(t.artwork, 132.dp)
                                Spacer(Modifier.height(8.dp))
                                Text(t.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(t.artist, color = SoftWhite, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionTitle("Favorites")
                    Column(Modifier.fillMaxWidth().glass(RoundedCornerShape(28.dp)).padding(6.dp)) {
                        favorites.forEach { t ->
                            TrackRow(t.title, t.artist, t.artwork, onClick = { onTrackClick(t) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LibraryScreen(
    entries: Map<LibraryTab, List<LibraryEntry>>,
    nowPlaying: Track?,
    isPlaying: Boolean,
    onEntryClick: (LibraryTab, LibraryEntry) -> Unit,
    onPlayPause: () -> Unit,
    onOpenPlayer: () -> Unit,
    selectedTab: Int,
    onTabSelect: (Int) -> Unit,
) {
    var tab by remember { mutableStateOf(LibraryTab.Songs) }
    var query by remember { mutableStateOf("") }
    val shown = (entries[tab] ?: emptyList()).filter {
        query.isBlank() || it.title.contains(query, true) || it.subtitle.contains(query, true)
    }
    GlassScaffold(
        artwork = nowPlaying?.artwork,
        bottomBar = {
            if (nowPlaying != null) {
                MiniPlayer(nowPlaying, isPlaying, onPlayPause, onOpenPlayer)
                Spacer(Modifier.height(10.dp))
            }
            GlassNavBar(selectedTab, onTabSelect)
        },
    ) {
        Column(Modifier.fillMaxSize().padding(horizontal = 20.dp).padding(top = 20.dp)) {
            Text("Library", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(14.dp))
            Row(
                Modifier.fillMaxWidth().glass(RoundedCornerShape(26.dp)).padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Rounded.Search, contentDescription = null, tint = SoftWhite, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                    cursorBrush = SolidColor(Color.White),
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        if (query.isEmpty()) Text("Search your library", color = SoftWhite, fontSize = 16.sp)
                        inner()
                    },
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(
                Modifier.fillMaxWidth().glass(RoundedCornerShape(26.dp)).padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                LibraryTab.values().forEach { t ->
                    val on = t == tab
                    Box(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(22.dp))
                            .background(if (on) Color.White.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable { tab = t }
                            .padding(vertical = 9.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(t.label, color = if (on) Color.White else SoftWhite, fontSize = 13.sp, fontWeight = if (on) FontWeight.Medium else FontWeight.Normal)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            if (shown.isEmpty()) {
                Text("Nothing matches your search.", color = SoftWhite, modifier = Modifier.padding(top = 24.dp))
            }
            LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                items(shown, key = { it.id }) { e ->
                    TrackRow(e.title, e.subtitle, e.artwork, onClick = { onEntryClick(tab, e) })
                }
            }
        }
    }
}

@Composable
fun QueueScreen(
    current: Track,
    upNext: List<Track>,
    onPlay: (Track) -> Unit,
    onRemove: (Track) -> Unit,
    onClose: () -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        GlassBackdrop(current.artwork)
        Column(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(top = 56.dp)
                .glass(RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp, bottomStart = 0.dp, bottomEnd = 0.dp))
                .padding(horizontal = 14.dp),
        ) {
            Box(
                Modifier
                    .padding(top = 10.dp, bottom = 12.dp)
                    .align(Alignment.CenterHorizontally)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.4f)),
            )
            Row(Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Up next", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                    Text("${upNext.size} songs queued", color = SoftWhite, fontSize = 13.sp)
                }
                Box(
                    Modifier.size(40.dp).glass(CircleShape).clickable(onClick = onClose),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Rounded.Close, contentDescription = "Close queue", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.height(6.dp))
            Text("Now playing", color = SoftWhite, fontSize = 12.sp, letterSpacing = 1.5.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp))
            TrackRow(current.title, current.artist, current.artwork, onClick = {}, highlighted = true)
            Spacer(Modifier.height(10.dp))
            Text("Next", color = SoftWhite, fontSize = 12.sp, letterSpacing = 1.5.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp))
            LazyColumn(Modifier.fillMaxSize(), contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)) {
                items(upNext, key = { it.id }) { t ->
                    TrackRow(
                        title = t.title,
                        subtitle = t.artist,
                        artwork = t.artwork,
                        onClick = { onPlay(t) },
                        trailing = {
                            Box(Modifier.size(40.dp).clip(CircleShape).clickable { onRemove(t) }, contentAlignment = Alignment.Center) {
                                Icon(Icons.Rounded.Close, contentDescription = "Remove ${t.title}", tint = SoftWhite, modifier = Modifier.size(18.dp))
                            }
                            Icon(Icons.Rounded.DragHandle, contentDescription = "Reorder", tint = SoftWhite, modifier = Modifier.padding(end = 6.dp).size(22.dp))
                        },
                    )
                }
            }
        }
    }
}

private val sample = listOf(
    Track("1", "Midnight Drive", "AURA Sessions", durationMs = 212_000),
    Track("2", "Neon Tides", "Kairo", durationMs = 198_000),
    Track("3", "Glass Rain", "Mirelle", durationMs = 241_000),
    Track("4", "Slow Static", "Hollow Ave", durationMs = 187_000),
    Track("5", "Afterglow", "Sora", durationMs = 205_000),
)

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun HomePreview() {
    var tab by remember { mutableStateOf(0) }
    HomeScreen("Good evening", sample.take(4), sample, sample.take(3), sample[0], true, {}, {}, {}, tab, { tab = it })
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun QueuePreview() {
    QueueScreen(sample[0], sample.drop(1), {}, {}, {})
}
