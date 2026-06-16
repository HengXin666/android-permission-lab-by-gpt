# Open-source Android permission baseline

This repository intentionally uses `getActivity/XXPermissions` as the Android permission demo baseline in CI.

## Why XXPermissions

- It is a public Android permission framework focused on modern Android permission compatibility.
- It provides a demo app under the upstream repository's `app` module.
- It is more relevant to Chinese Android development than Google-only samples because it tracks many Android permission version differences and has Chinese documentation.
- It is licensed under Apache-2.0.

Upstream:

- <https://github.com/getActivity/XXPermissions>

## CI behavior

The GitHub Actions workflow has two jobs:

1. `Web permission lab build`
   - Builds this repository's React/Vite permission lab.

2. `Open-source Android permission demo smoke build`
   - Clones `getActivity/XXPermissions`.
   - Builds its demo APK with `./gradlew :app:assembleDebug`.
   - Uploads the APK artifact when available.

This is deliberate. The previously hand-written Capacitor Android shell was not generated from a standard Capacitor template and kept failing during Gradle evaluation. For now, CI should prove two stable things:

- our cross-platform permission model and debug UI can compile;
- a mature open-source Android permission demo can compile as the native reference.

## Future migration plan

When this repository needs a real native Android app instead of an open-source smoke reference, prefer one of these approaches:

1. Generate a standard Capacitor Android project locally with:

   ```bash
   npm install
   npm run build
   npx cap add android
   npx cap sync android
   ```

   Then commit the entire generated Android project, including Gradle wrapper files.

2. Or create a pure native Android demo module using XXPermissions as the dependency:

   ```gradle
   implementation 'com.github.getActivity:XXPermissions:<version>'
   ```

3. Keep OEM-only capabilities such as autostart, background popup, and battery unrestricted as manual settings flows. Do not model them as normal runtime permissions.
