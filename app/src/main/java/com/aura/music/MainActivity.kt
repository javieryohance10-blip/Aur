package com.aura.music

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import com.aura.music.data.AudioTrack
import com.aura.music.data.MediaStoreAudioRepository
import com.aura.music.playback.PlaybackConnector

class MainActivity : ComponentActivity() {
    private val tracks = mutableStateListOf<AudioTrack>()
    private lateinit var playbackConnector: PlaybackConnector
    private var controller: MediaController? = null

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) loadTracks()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playbackConnector = PlaybackConnector(this)
        playbackConnector.controllerFuture.addListener({
            runCatching { playbackConnector.controllerFuture.get() }.onSuccess { controller = it }
        }, ContextCompat.getMainExecutor(this))

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("AURA", style = MaterialTheme.typography.headlineLarge)
                        Text("MUSIC", style = MaterialTheme.typography.labelLarge)
                        Text("Your library", style = MaterialTheme.typography.titleLarge)
                        if (tracks.isEmpty()) {
                            Text("Grant audio access to scan music stored on this device.")
                            Text("Scan library", modifier = Modifier.clickable { requestAudioPermission() }.padding(8.dp))
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(tracks, key = { it.id }) { track ->
                                    Row(modifier = Modifier.fillMaxWidth().clickable { play(track) }.padding(vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(track.title, style = MaterialTheme.typography.titleMedium)
                                            Text(track.artist, style = MaterialTheme.typography.bodySmall)
                                        }
                                        Text("PLAY")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (hasAudioPermission()) loadTracks() else requestAudioPermission()
    }

    private fun audioPermission(): String = if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_AUDIO else Manifest.permission.READ_EXTERNAL_STORAGE
    private fun hasAudioPermission(): Boolean = ContextCompat.checkSelfPermission(this, audioPermission()) == PackageManager.PERMISSION_GRANTED
    private fun requestAudioPermission() = permissionLauncher.launch(audioPermission())
    private fun loadTracks() {
        tracks.clear()
        tracks.addAll(runCatching { MediaStoreAudioRepository(this).getTracks() }.getOrDefault(emptyList()))
    }
    private fun play(track: AudioTrack) {
        controller?.apply {
            setMediaItem(MediaItem.fromUri(track.uri))
            prepare()
            play()
        }
    }

    override fun onDestroy() {
        playbackConnector.release()
        controller = null
        super.onDestroy()
    }
}
