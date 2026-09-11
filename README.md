# City Bloxx

City Bloxx is a one-touch tower-building game for Android. A block moves over
the skyline; tap to release it onto the tower. Every overhanging section is
trimmed away, so each imperfect landing makes the next floor harder. Build as
high as possible before using all three chances, then tap once to begin again.

The game uses procedural shapes and colors, so the repository contains
everything needed to render and play it without an external art pipeline.

## Technology

- Java and a custom Android `View` provide the game loop, touch input, drawing,
  scoring, camera movement, and lifecycle integration.
- C++20 implements deterministic block-overlap physics and is connected to Java
  through JNI.
- Android NDK and CMake build the native library for arm64-v8a, armeabi-v7a,
  and x86_64.
- Gradle builds the application, while GitHub Actions checks the Android build
  and the standalone C++ physics test on every push and pull request.

## Build and play

Install Android Studio with Android SDK 35, NDK 27, CMake 3.22.1, and JDK 17.
Then clone the repository and run:

```bash
./gradlew assembleDebug
```

Install `app/build/outputs/apk/debug/app-debug.apk` on an Android device or open
the project in Android Studio and press Run.

## Contributing

Contributions are welcome. Fork the repository, create a focused branch, and
keep each pull request limited to one gameplay, accessibility, performance, or
platform improvement. Match the existing Java and C++ style, avoid generated
files and binary assets, and include a test for changes to native physics.

Before opening a pull request, run:

```bash
./gradlew testDebugUnitTest assembleDebug
c++ -std=c++20 -Wall -Wextra -Werror tests/physics_test.cpp -o /tmp/physics_test
/tmp/physics_test
```

Describe the player-visible result and the devices or emulator versions used
for testing. By submitting a contribution, you agree to license it under the
MIT License included in this repository.
