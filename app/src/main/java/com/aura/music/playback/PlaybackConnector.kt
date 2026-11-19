package com.aura.music.playback

import android.content.ComponentName
import android.content.Context
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture

class PlaybackConnector(context: Context) {
    val controllerFuture: ListenableFuture<MediaController> =
        MediaController.Builder(
            context.applicationContext,
            SessionToken(context.applicationContext, ComponentName(context, AuraPlaybackService::class.java))
        ).buildAsync()

    fun release() {
        MediaController.releaseFuture(controllerFuture)
    }
}
