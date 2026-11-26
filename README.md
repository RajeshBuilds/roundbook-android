# Roundbook

Roundbook is an offline Android field notebook for building inspections. A shift starts with seven inspections. Open an inspection to read its checklist, mark it complete, and find it again under **Completed**. Completion status persists when the app closes.

This is an original, AI-assisted sample with a deliberately seeded Android lifecycle memory leak for profiling practice. The inspection data is fictional.

## Run locally

Open the repository root in Android Studio, sync Gradle with the IDE's bundled JDK, and run the **app** debug configuration on an Android 8.0 (API 26) or newer device. The project compiles against Android SDK Platform 36. From a terminal with a suitable JDK and Android SDK configured, run `./gradlew assembleDebug`.

## Reproduce the profiling exercise

1. Start a fresh, debuggable app process and leave the rounds list visible. In Android Studio’s Profiler, select **Analyze Memory Usage (Heap Dump)** to capture the baseline. [Android Studio heap dump guide](https://developer.android.com/studio/profile/capture-heap-dump)
2. Open **Entrance mats**, read its checklist, and return to the list. Make five visits in total, keeping the app running and the inspection marked **Open**.
3. From the list, request garbage collection and capture a second heap dump. Compare the number of destroyed `InspectionActivity` instances and inspect a path to a GC root.
4. After changing the lifecycle code, repeat from a fresh process. The five finished detail screens should be collectible. Also mark an inspection complete, find it in **Completed**, reopen it, and verify that it appears under **Open** again.

The app and original content use the [MIT License](LICENSE). Gradle wrapper files retain their upstream license notices.
