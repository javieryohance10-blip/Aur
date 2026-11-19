package com.aura.music.data

import com.aura.music.ui.player.LibraryEntry
import com.aura.music.ui.player.Track

object SampleData {
    val tracks = listOf(
        Track("1", "Midnight Drive", "AURA Sessions", durationMs = 212_000),
        Track("2", "Neon Tides", "Kairo", durationMs = 198_000),
        Track("3", "Glass Rain", "Mirelle", durationMs = 241_000),
        Track("4", "Slow Static", "Hollow Ave", durationMs = 187_000),
        Track("5", "Afterglow", "Sora", durationMs = 205_000),
        Track("6", "Violet Hours", "Kairo", durationMs = 226_000),
        Track("7", "Paper Skies", "Mirelle", durationMs = 193_000),
        Track("8", "Low Orbit", "Hollow Ave", durationMs = 258_000),
    )

    val albums = listOf(
        LibraryEntry("al1", "Night Circuits", "AURA Sessions"),
        LibraryEntry("al2", "Tidal", "Kairo"),
        LibraryEntry("al3", "Clear Weather", "Mirelle"),
        LibraryEntry("al4", "Static Bloom", "Hollow Ave"),
    )

    val artists = listOf(
        LibraryEntry("ar1", "AURA Sessions", "1 song"),
        LibraryEntry("ar2", "Kairo", "2 songs"),
        LibraryEntry("ar3", "Mirelle", "2 songs"),
        LibraryEntry("ar4", "Hollow Ave", "2 songs"),
        LibraryEntry("ar5", "Sora", "1 song"),
    )

    val playlists = listOf(
        LibraryEntry("pl1", "Late night", "12 songs"),
        LibraryEntry("pl2", "Focus", "24 songs"),
        LibraryEntry("pl3", "Road trip", "18 songs"),
    )
}
