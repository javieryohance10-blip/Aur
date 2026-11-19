# AURA Music

A native Android music player foundation built with Kotlin, Jetpack Compose, Media3, and MediaStore.

## Project status

Milestone 1 establishes the Android application shell, a Media3 background playback service, and a permission-aware local audio library. Streaming provider integrations are intentionally kept behind a source interface until their supported APIs and licensing requirements are verified.

## Requirements

- JDK 17
- Android SDK 35
- Gradle 8.11.1

## Build

```bash
gradle :app:assembleDebug
```

## Package

`com.aura.music`

## License

See [LICENSE](LICENSE).