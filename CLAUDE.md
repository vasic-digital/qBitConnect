# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

qBitController is a Kotlin Multiplatform app using Compose Multiplatform that allows users to control qBittorrent servers from Android, iOS, Windows, Linux, and macOS devices. The project uses Gradle as the build system with custom build plugins and a multi-module architecture.

## Build System and Development Commands

### Core Build Commands
- `./gradlew build` - Build the entire project
- `./gradlew lintKotlin` - Run Kotlin code style checks (required for PRs)
- `./gradlew clean` - Clean build artifacts

### Android Development
- `./gradlew :composeApp:assembleDebug` - Build Android debug APK
- `./gradlew :composeApp:assembleFirebaseDebug` - Build Firebase-enabled debug APK
- `./gradlew :composeApp:installDebug` - Install debug APK on connected device

### Desktop Development
- `./gradlew :composeApp:run` - Run desktop application
- `./gradlew :composeApp:packageDistributionForCurrentOS` - Package for current OS
- `./gradlew :composeApp:packageDmg` - Create macOS DMG (macOS only)
- `./gradlew :composeApp:packageMsi` - Create Windows MSI
- `./gradlew :composeApp:packageAppImage` - Create Linux AppImage

### Flatpak (Linux)
- `./gradlew :composeApp:prepareFlatpak` - Prepare Flatpak bundle
- `./gradlew :composeApp:installFlatpak` - Install via Flatpak
- `./gradlew :composeApp:runFlatpak` - Run installed Flatpak

## Project Architecture

### Module Structure
- **composeApp** - Main application module containing all UI and business logic
- **preferences** - Shared preferences/settings module
- **baselineProfile** - Android baseline profiles for performance optimization
- **buildSrc** - Custom Gradle plugins and build logic
- **iosApp** - iOS application wrapper

### Source Set Organization
The project uses Kotlin Multiplatform with the following source sets:

- `commonMain` - Shared code across all platforms
- `androidMain` - Android-specific implementations
- `desktopMain` - Desktop (JVM) implementations
- `iosMain` - iOS implementations
- `jvmMain` - Shared JVM code (Android + Desktop)
- `nonAndroidMain` - Shared non-Android code (Desktop + iOS)

### Key Architectural Components

**Data Layer:**
- `ServerManager` - Manages qBittorrent server configurations and persistence
- `SettingsManager` - Handles app settings and preferences
- Repositories for different features (TorrentList, AddTorrent, RSS, etc.)
- Ktor HTTP client for API communication

**UI Layer:**
- Compose Multiplatform UI with Material 3 design
- Navigation using Compose Navigation
- Koin for dependency injection
- Platform-specific UI adaptations in respective source sets

**Networking:**
- Ktor client with OkHttp engine on JVM platforms
- Darwin engine on iOS
- Custom authentication handling for qBittorrent API

### Custom Build Plugins
Located in `buildSrc/src/main/java/dev/bartuzen/qbitcontroller/plugin/`:
- **LanguagePlugin** - Handles localization and string resources
- **IosPlugin** - iOS-specific build configurations

## Key Dependencies and Technologies

- **Compose Multiplatform** - UI framework
- **Kotlin Multiplatform** - Cross-platform development
- **Koin** - Dependency injection
- **Ktor** - HTTP client for API calls
- **Kotlinx Serialization** - JSON serialization
- **Multiplatform Settings** - Cross-platform settings storage
- **Material Kolor** - Dynamic theming support
- **Coil** - Image loading with SVG support

## Code Style and Quality

### Linting
The project uses Kotlinter for code style enforcement:
- All code must pass `./gradlew lintKotlin` before merging
- Kotlin official code style is enforced
- Custom lint rules exclude build directories

### Build Flavors (Android)
- **free** - Default flavor without Firebase
- **firebase** - Includes Firebase Analytics and Crashlytics

## Platform-Specific Notes

### Android
- Minimum SDK: 21, Target SDK: 36
- Uses R8 code shrinking in release builds
- Supports baseline profiles for performance
- Two build types: debug (with `.debug` suffix) and release

### Desktop
- Supports macOS, Windows, and Linux
- Uses ProGuard for release builds
- Custom distribution formats per platform
- Built-in update checker for desktop versions

### iOS
- Framework-based architecture with static linking
- Uses Darwin HTTP engine for networking
- Automatic Info.plist generation with supported languages

## Version Management

App versions are centrally managed in `buildSrc/src/main/java/dev/bartuzen/qbitcontroller/Versions.kt`:
- `AppVersion` - Semantic version string
- `AppVersionCode` - Integer version code
- Platform-specific SDK versions and Java compatibility

## Development Workflow

1. Code changes must pass `./gradlew lintKotlin`
2. Test on target platforms before submitting PRs
3. Use appropriate source sets for platform-specific code
4. Follow existing patterns for dependency injection and state management
5. Maintain compatibility across all supported platforms