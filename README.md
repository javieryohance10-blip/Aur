# Aur

Native Android music player with a liquid glass UI, built with Jetpack Compose.

## Screens

- Home: quick picks, recently played, favorites, mini player
- Library: search plus Songs, Albums, Artists, and Playlists tabs
- Now playing: blurred artwork backdrop, glass controls, seek bar
- Queue: glass bottom sheet with now playing and up next
- Search and Settings: placeholders

## Structure

```
app/src/main/java/com/aura/music/
  MainActivity.kt            edge-to-edge entry point
  ui/AuraApp.kt              navigation and state wiring
  ui/player/                 glass components and all four screens
  player/PlayerViewModel.kt  in-memory playback state with a simulated clock
  data/SampleData.kt         sample tracks and library entries
```

## Run

1. Open the folder in Android Studio (Koala or newer) and let Gradle sync.
2. Run the `app` configuration on a device or emulator with Android 8.0 (API 26) or higher.

The backdrop blur needs Android 12 (API 31) or higher. Older devices show a dark gradient instead.

## Next steps

- Replace `PlayerViewModel` internals with a real player (Media3) and load tracks from MediaStore.
- Pass real artwork (`Uri`, URL, or `Bitmap`) into `Track.artwork`. Coil handles all three.
- Build the Search and Settings screens.
