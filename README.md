<div align="center">
  <img src="assets/Fairy-Music.png" alt="FAIRY MUSIC Logo" width="140" style="border-radius: 28px;"/>

  <h1>FAIRY MUSIC</h1>

  <p><b>A modern Android music app with ad-free streaming, synced lyrics, offline playback, and an intuitive user experience.</b></p>
  <p>Developed by <b>Benny</b> (<a href="https://www.instagram.com/lg_dark_7">@lg_dark_7</a>)</p>
</div>

---

## Overview

**FAIRY MUSIC** delivers a seamless, premium listening experience by leveraging YouTube Music's vast library — without the ads. It adds powerful extras including offline downloads, real-time synchronized lyrics, background playback, and environment-aware music recognition.

---

- **Developer Instagram**: [Benny (@lg_dark_7)](https://www.instagram.com/lg_dark_7)
- **Source Code & Releases**: [FAIRY MUSIC on GitHub](https://github.com/lgdark7/FAIRY-MUSIC)

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Installation & Setup](#installation--setup)
- [Special Thanks](#special-thanks)
- [Legal Disclaimer & Terms of Use](#legal-disclaimer--terms-of-use)

---

## Features

### Highlights

> - **Ad-Free Streaming** — Stream without any interruptions.
> - **Synchronized Lyrics** — Real-time synced lyrics with word-by-word highlighting and multiple animation styles.
> - **Offline Playback & Downloads** — Download tracks, albums, and playlists for offline listening.
> - **Seamless Background Playback** — Continue listening with the screen off or while multitasking in other apps.
> - **Data Saver Mode** — Reduce mobile data consumption on cellular networks.
> - **Import & Sync from Spotify** — Easily mirror your favorite Spotify playlists into local collections.
> - **Listen Together** — Stream and sync playback in real time with friends.
> - **Local Media Library** — Seamlessly plays music stored locally on your device storage.

---

## Installation & Setup

### Android Installation

Download the latest pre-compiled universal APK directly from the [Releases Page](https://github.com/lgdark7/FAIRY-MUSIC/releases/latest).

<details>
<summary><b>Building from Source</b></summary>
<br>

1. **Clone the Repository**

   ```bash
   git clone https://github.com/lgdark7/FAIRY-MUSIC.git
   cd FAIRY-MUSIC
   ```

2. **Configure Android SDK**
   Create a `local.properties` file in the project root:

   ```bash
   sdk.dir=/path/to/your/android/sdk
   ```

3. **Build the Application**

   To build the Universal Debug APK:
   ```bash
   ./gradlew assembleUniversalFossDebug
   ```

   The generated APK will be located at:
   `app/build/outputs/apk/universalFoss/debug/app-universal-foss-debug.apk`

</details>

---

## Special Thanks

FAIRY MUSIC is built upon foundational open-source music streaming architectures and libraries. Sincere thanks to the open-source community:

| Project | Description |
| :--- | :--- |
| **[Metrolist](https://github.com/MetrolistGroup/Metrolist)** & **[Vivi Music](https://github.com/vivizzz007/vivi-music)** | Foundational inspiration and architecture reference |
| **[ArchiveTune](https://github.com/koiverse/ArchiveTune)** | Material You UI inspiration |
| **[Better Lyrics](https://better-lyrics.boidu.dev/)** | Lyrics enhancement and synchronization |
| **[SimpMusic](https://github.com/maxrave-dev/SimpMusic)** | Lyrics implementation reference |
| **[Music Recognizer](https://github.com/aleksey-saenko/MusicRecognizer)** | Audio recognition engine |
| **[BravePipe](https://github.com/bravepipeproject/BravePipe)** | Decryption handling and backup playback engine |

---

## Legal Disclaimer & Terms of Use

### 1. 100% Free, Open-Source & Strictly Non-Commercial
FAIRY MUSIC is an open-source project (FOSS) created for educational and personal use. It is free, contains no ads, subscriptions, or paywalls.

### 2. Custom Client with Public APIs
FAIRY MUSIC acts as a custom client and browser interface parsing publicly accessible media content and metadata from YouTube and YouTube Music.

### 3. Support Content Creators
We strongly encourage supporting content creators and artists directly by subscribing to official premium subscriptions and streaming services.

---

<div align="center">
  <p>Licensed under <a href="LICENSE">GPL-3.0</a></p>
</div>
