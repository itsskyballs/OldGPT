# Gecko integration and Android 4.x compatibility

This document explains how to integrate GeckoView into the OldGPT Android project so older Android devices (Android 4.x) can use a more modern rendering engine.

Important notes
- Android 4.x (API 14-19) is very old. Modern web apps (including chat.openai.com) may still not work fully even with an embedded Gecko version due to TLS or JS feature requirements.
- Embedding Gecko requires matching Java bindings (GeckoView AAR) and native libraries (.so) for each architecture you wish to support.
- Including prebuilt binaries in the repository may increase its size and may be subject to Mozilla licensing. This scaffold does NOT include large binaries by default.

Steps to add Gecko libraries

1) Choose a GeckoView build
- For maximum compatibility with Android 4.x you will likely need an older Gecko ESR build compiled for armeabi-v7a and x86.
- Mozilla does not officially publish many legacy GeckoView builds for very old Android versions; you may need to compile Gecko yourself or find a community build.

2) Add the AAR and native libraries to the project
- Place the GeckoView AAR in `app/libs/` (create directory if it does not exist). Update `app/build.gradle` to include:

    implementation files('libs/geckoview.aar')

- Place native libraries in:

    app/src/main/jniLibs/armeabi-v7a/libxul.so
    app/src/main/jniLibs/x86/libxul.so

  (Exact .so names depend on the build; many older builds provide libxul.so and supporting binaries.)

3) Rebuild the project
- Import the project into Android Studio or build with your chosen tool (AIDE).
- If classes are available, implement proper initialization in `GeckoLoader.initWithGecko(...)` (see comments in code). Typical steps:
  - Create a GeckoRuntime for your context
  - Open a GeckoSession on that runtime
  - Attach the session to a GeckoView instance
  - Load the desired URI (https://chat.openai.com)

4) Test on device
- Install the built APK on a real Android 4.x device (arm or x86 as appropriate).
- Capture logcat to debug TLS / JS errors and missing symbol errors (which indicate mismatched native libs).

Fallback notes
- If Gecko is not available or initialization fails, the app falls back to the system WebView (improved settings are enabled). If the WebView cannot render the site, the app will display `assets/offline.html` with guidance.

Security and maintenance
- Running an old Gecko or old device exposes you to security vulnerabilities. Use caution when logging into services.

If you want, I can attempt to locate an older GeckoView build and add it to the repo — but you must explicitly confirm you accept adding third-party binaries and their license to this repository.
