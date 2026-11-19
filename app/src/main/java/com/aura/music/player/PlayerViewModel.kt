package com.aura.music.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.music.data.SampleData
import com.aura.music.ui.player.RepeatMode
import com.aura.music.ui.player.Track
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class PlayerState(
    val queue: List<Track>,
    val index: Int = 0,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val favorites: Set<String> = emptySet(),
    val shuffle: Boolean = false,
    val repeat: RepeatMode = RepeatMode.Off,
) {
    val current: Track? get() = queue.getOrNull(index)
}

/**
 * In-memory playback state with a simulated clock. Replace the body of each action with calls
 * to your real player (Media3 / MediaController) when you wire up audio.
 */
class PlayerViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        PlayerState(queue = SampleData.tracks, favorites = setOf("1"))
    )
    val state: StateFlow<PlayerState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                delay(TICK_MS)
                tick()
            }
        }
    }

    private fun tick() = _state.update { s ->
        val cur = s.current
        if (cur == null || !s.isPlaying) {
            s
        } else {
            val pos = s.positionMs + TICK_MS
            if (pos < cur.durationMs) s.copy(positionMs = pos) else advance(s, auto = true)
        }
    }

    private fun advance(s: PlayerState, auto: Boolean): PlayerState {
        if (s.queue.isEmpty()) return s
        if (auto && s.repeat == RepeatMode.One) return s.copy(positionMs = 0L)
        val next = if (s.shuffle && s.queue.size > 1) {
            var n = s.index
            while (n == s.index) n = Random.nextInt(s.queue.size)
            n
        } else {
            s.index + 1
        }
        return when {
            next < s.queue.size -> s.copy(index = next, positionMs = 0L)
            s.repeat == RepeatMode.All || !auto -> s.copy(index = 0, positionMs = 0L)
            else -> s.copy(isPlaying = false, positionMs = 0L)
        }
    }

    fun togglePlay() = _state.update { it.copy(isPlaying = !it.isPlaying) }

    fun next() = _state.update { advance(it, auto = false).copy(isPlaying = true) }

    fun previous() = _state.update { s ->
        if (s.positionMs > RESTART_THRESHOLD_MS || s.queue.isEmpty()) {
            s.copy(positionMs = 0L)
        } else {
            val prev = if (s.index > 0) s.index - 1 else s.queue.lastIndex
            s.copy(index = prev, positionMs = 0L)
        }
    }

    fun seek(positionMs: Long) = _state.update { it.copy(positionMs = positionMs) }

    fun toggleFavorite() = _state.update { s ->
        val id = s.current?.id ?: return@update s
        s.copy(favorites = if (id in s.favorites) s.favorites - id else s.favorites + id)
    }

    fun toggleShuffle() = _state.update { it.copy(shuffle = !it.shuffle) }

    fun cycleRepeat() = _state.update {
        it.copy(
            repeat = when (it.repeat) {
                RepeatMode.Off -> RepeatMode.All
                RepeatMode.All -> RepeatMode.One
                RepeatMode.One -> RepeatMode.Off
            }
        )
    }

    fun play(track: Track) = _state.update { s ->
        val i = s.queue.indexOfFirst { it.id == track.id }
        if (i >= 0) {
            s.copy(index = i, positionMs = 0L, isPlaying = true)
        } else {
            s.copy(queue = s.queue + track, index = s.queue.size, positionMs = 0L, isPlaying = true)
        }
    }

    fun removeFromQueue(track: Track) = _state.update { s ->
        val i = s.queue.indexOfFirst { it.id == track.id }
        if (i < 0 || i == s.index) {
            s
        } else {
            s.copy(
                queue = s.queue.filterIndexed { k, _ -> k != i },
                index = if (i < s.index) s.index - 1 else s.index,
            )
        }
    }

    private companion object {
        const val TICK_MS = 500L
        const val RESTART_THRESHOLD_MS = 3_000L
    }
}
