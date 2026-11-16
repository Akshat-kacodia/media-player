# Mediapalyer

Mediapalyer is an Android video playback sample built with Kotlin, Jetpack Compose, and
AndroidX Media3. The app presents the *Tears of Steel* sample film and opens a dedicated
playback screen for streaming, ad playback, and media-track selection.

## Features

- DASH video playback using AndroidX Media3 ExoPlayer
- Google IMA ad support with a VMAP sample ad tag
- Play, pause, seek, rewind, and fast-forward controls
- Fullscreen landscape playback with system-bar controls
- Video, audio, and subtitle track selection
- Subtitle rendering through Media3 cues
- Jetpack Compose UI with Material 3 theming
- Playback lifecycle management when the activity starts and stops

## Technology stack

- Kotlin 1.9.0
- Android Gradle Plugin 8.4.0
- Gradle 8.6
- Jetpack Compose
- AndroidX Media3 1.5.1
- Minimum Android SDK: 25
- Compile SDK: 35
- Target SDK: 34

## Requirements

- Android Studio with Android SDK 35 installed
- JDK 8 or a compatible JDK supported by your Android Studio installation
- An Android device or emulator running Android 7.1 (API 25) or newer
- Internet access for Gradle dependencies and streamed media

## Getting started

1. Clone or download this repository.
2. Open the project in Android Studio.
3. Allow Android Studio to sync the Gradle project and install any requested SDK
   components.
4. Connect an Android device or start an emulator.
5. Run the `app` configuration.

The application requests the `INTERNET` permission because the sample video and ad
content are streamed from remote URLs.

## Build from the command line

On Windows:

```powershell
.\gradlew.bat assembleDebug
```

Install the debug APK on a connected device:

```powershell
.\gradlew.bat installDebug
```

Run unit tests:

```powershell
.\gradlew.bat test
```

Run instrumented tests on a connected device or emulator:

```powershell
.\gradlew.bat connectedAndroidTest
```

The generated debug APK is written to:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Project structure

```text
app/src/main/
├── java/com/example/mediapalyer/
│   ├── HomeActivity.kt          # Home screen and playback entry point
│   ├── PlaybackActivity.kt      # Playback screen and lifecycle integration
│   ├── home/                    # Home-screen state
│   ├── playback/                # Player, actions, and playback state
│   └── ui/theme/                # Compose theme definitions
├── res/                         # Drawables, launcher assets, and themes
└── AndroidManifest.xml          # Activities and network permission
```

## Sample media configuration

The default media configuration is defined in
`app/src/main/java/com/example/mediapalyer/home/HomeUiModel.kt`:

- Stream: the Google-hosted *Tears of Steel* MPEG-DASH manifest
- Attribution: Blender Foundation / mango.blender.org
- Ad tag: Google IMA VMAP sample ad tag

To use another stream, update the `streamUrl` and, if needed, `adTagUrl` values in
`HomeUiModel`. The supplied stream must be compatible with Media3 and reachable from
the device or emulator.

## Testing

The project includes JVM unit-test and Android instrumented-test source sets:

- `app/src/test` for local unit tests
- `app/src/androidTest` for device or emulator tests

Add tests alongside the relevant source set and run the corresponding Gradle task
listed above.

## License and media attribution

This repository does not currently declare a software license. The sample media is
attributed in the app to the Blender Foundation at
<https://mango.blender.org/>. Review the applicable terms before redistributing the
sample media or deploying the default stream in production.
