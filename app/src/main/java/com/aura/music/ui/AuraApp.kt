package com.aura.music.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aura.music.data.SampleData
import com.aura.music.player.PlayerViewModel
import com.aura.music.ui.player.GlassNavBar
import com.aura.music.ui.player.GlassScaffold
import com.aura.music.ui.player.HomeScreen
import com.aura.music.ui.player.LibraryScreen
import com.aura.music.ui.player.LibraryTab
import com.aura.music.ui.player.LiquidGlassPlayer
import com.aura.music.ui.player.MiniPlayer
import com.aura.music.ui.player.PlayerActions
import com.aura.music.ui.player.PlayerUiState
import com.aura.music.ui.player.QueueScreen
import com.aura.music.ui.player.Track
import java.time.LocalTime

private object Routes {
    const val Home = "home"
    const val Search = "search"
    const val Library = "library"
    const val Settings = "settings"
    const val Player = "player"
    const val Queue = "queue"
}

private val TabRoutes = listOf(Routes.Home, Routes.Search, Routes.Library, Routes.Settings)

@Composable
fun AuraApp(vm: PlayerViewModel = viewModel()) {
    val s by vm.state.collectAsState()
    val nav = rememberNavController()
    val current = s.current
    val libraryEntries = remember {
        mapOf(
            LibraryTab.Songs to SampleData.tracks.map {
                com.aura.music.ui.player.LibraryEntry(it.id, it.title, it.artist, it.artwork)
            },
            LibraryTab.Albums to SampleData.albums,
            LibraryTab.Artists to SampleData.artists,
            LibraryTab.Playlists to SampleData.playlists,
        )
    }

    val onTab: (Int) -> Unit = { i ->
        nav.navigate(TabRoutes[i]) {
            popUpTo(Routes.Home) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
    val openPlayer = { nav.navigate(Routes.Player) { launchSingleTop = true } }

    NavHost(navController = nav, startDestination = Routes.Home) {
        composable(Routes.Home) {
            HomeScreen(
                greeting = greeting(),
                quickPicks = s.queue.take(4),
                recent = s.queue.drop(1),
                favorites = s.queue.filter { it.id in s.favorites },
                nowPlaying = current,
                isPlaying = s.isPlaying,
                onTrackClick = vm::play,
                onPlayPause = vm::togglePlay,
                onOpenPlayer = { openPlayer() },
                selectedTab = 0,
                onTabSelect = onTab,
            )
        }
        composable(Routes.Library) {
            LibraryScreen(
                entries = libraryEntries,
                nowPlaying = current,
                isPlaying = s.isPlaying,
                onEntryClick = { tab, entry ->
                    if (tab == LibraryTab.Songs) {
                        SampleData.tracks.firstOrNull { it.id == entry.id }?.let(vm::play)
                    }
                },
                onPlayPause = vm::togglePlay,
                onOpenPlayer = { openPlayer() },
                selectedTab = 2,
                onTabSelect = onTab,
            )
        }
        composable(Routes.Search) {
            PlaceholderScreen("Search", "Search is coming next.", 1, onTab, current, s.isPlaying, vm::togglePlay) { openPlayer() }
        }
        composable(Routes.Settings) {
            PlaceholderScreen("Settings", "Settings are coming next.", 3, onTab, current, s.isPlaying, vm::togglePlay) { openPlayer() }
        }
        composable(Routes.Player) {
            if (current != null) {
                LiquidGlassPlayer(
                    state = PlayerUiState(
                        title = current.title,
                        artist = current.artist,
                        artwork = current.artwork,
                        isPlaying = s.isPlaying,
                        positionMs = s.positionMs,
                        durationMs = current.durationMs,
                        isFavorite = current.id in s.favorites,
                        shuffle = s.shuffle,
                        repeat = s.repeat,
                    ),
                    actions = PlayerActions(
                        onBack = { nav.popBackStack() },
                        onPlayPause = vm::togglePlay,
                        onNext = vm::next,
                        onPrevious = vm::previous,
                        onSeek = vm::seek,
                        onFavorite = vm::toggleFavorite,
                        onShuffle = vm::toggleShuffle,
                        onRepeat = vm::cycleRepeat,
                        onQueue = { nav.navigate(Routes.Queue) { launchSingleTop = true } },
                    ),
                    selectedTab = 0,
                    onTabSelect = onTab,
                )
            }
        }
        composable(Routes.Queue) {
            if (current != null) {
                QueueScreen(
                    current = current,
                    upNext = s.queue.drop(s.index + 1),
                    onPlay = vm::play,
                    onRemove = vm::removeFromQueue,
                    onClose = { nav.popBackStack() },
                )
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(
    title: String,
    body: String,
    tab: Int,
    onTab: (Int) -> Unit,
    current: Track?,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onOpenPlayer: () -> Unit,
) {
    GlassScaffold(
        artwork = current?.artwork,
        bottomBar = {
            if (current != null) {
                MiniPlayer(current, isPlaying, onPlayPause, onOpenPlayer)
                Spacer(Modifier.height(10.dp))
            }
            GlassNavBar(tab, onTab)
        },
    ) {
        Column(Modifier.fillMaxSize().padding(24.dp)) {
            Text(title, color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(body, color = Color.White.copy(alpha = 0.65f), fontSize = 16.sp)
        }
    }
}

private fun greeting(): String = when (LocalTime.now().hour) {
    in 5..11 -> "Good morning"
    in 12..17 -> "Good afternoon"
    else -> "Good evening"
}
