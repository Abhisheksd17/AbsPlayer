High-Performance Reels Library for Android

AbsPlayer is a professional-grade Jetpack Compose library designed to deliver a seamless, Instagram-style vertical video "Reels" experience. It solves the common performance pitfalls of video lists—such as swiping latency, high memory usage, and excessive bandwidth consumption—using a sophisticated 3-layer caching and prefetching architecture.

🚀 Key Features
•0-Latency Swiping: Uses a single-player "Playlist Mode" that keeps the video decoder warm, ensuring transitions between reels are instantaneous and glitch-free.
•3-Layer Smart Caching:
  ◦Layer 1 (Disk Cache): Persistent 500MB LRU (Least Recently Used) cache using Media3 SimpleCache.
  ◦Layer 2 (Partial Prefetcher): Proactively downloads only the first 2MB of upcoming videos. This ensures instant startup while saving up to 90% of bandwidth on skipped reels.
  ◦Layer 3 (Stream-While-Cache): Simultaneously streams and caches the current video in real-time.
•Intelligent Aspect Ratio Handling: Automatically measures video dimensions and resizes the player to fit the screen perfectly. Unlike standard players, it never crops the video, respecting the original content's framing (IG-style).
Advanced Interaction Model:
  ◦2x Speed Toggle: Long-press on the right side of the screen to double playback speed.
  ◦Custom UI Slot API: A declarative "Overlay" system that allows developers to plug in their own Like buttons, comments, and user profiles on top of the player.
  ◦Smart Seek Bar: High-performance slider with visibility logic that mimics modern social media apps.


📦 Installation (JitPack)Add the JitPack repository to your settings.gradle.kts:

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
} 



🛠️ How to Use
1. Initialize the LibraryInitialize the player engine once in your Application class or MainActivity
 AbsPlayer.init(this)

2. Basic Implementation
 Pass your list of ReelItem objects to the ReelsViewer Composable:

val myReels = listOf(
    ReelItem(id = "1", videoUrl = "https://example.com/video1.mp4"),
    ReelItem(id = "2", videoUrl = "https://example.com/video2.mp4")
)

AbsPlayer.ReelsViewer(
    reels = myReels,
    modifier = Modifier.fillMaxSize()
)


3. Advanced Usage (Custom Overlay)
Use the Slot API to build your custom UI on top of the optimized player:

AbsPlayer.ReelsViewer(reels = myReels) { reel, state ->
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.BottomEnd)) {
            LikeButton(isLiked = reel.extraData["isLiked"] as Boolean)
            ShareButton()
        }
        
        if (!state.isPlaying) {
            CustomPauseIcon()
        }
    }
}


🧩 Architecture Deep Dive

The "Glitch-Free" LogicMost players stutter because they re-initialize the hardware decoder on every swipe. 
AbsPlayer maintains a single ExoPlayer instance and loads the entire list as a internal playlist. 
Swiping simply triggers a seekTo operation, which keeps the hardware decoder active and ready to render frames immediately.
Bandwidth OptimizationInstead of downloading the entire 20MB-50MB video for every reel in the feed, our ReelPrefetcher only pulls the first 2MB of the next two videos.
1.If the user skips, you only used 2MB.
2.If the user stays, the player seamlessly transitions from the 2MB cache to a live stream for the remainder of the file.

Video MeasurementThe library uses onVideoSizeChanged listeners to calculate the exact screenWidth * videoHeight / videoWidth.
It caps this height to the screen size, creating a centered "Letterbox" effect for landscape or square videos, ensuring no content is ever cut off by RESIZE_MODE_ZOOM.


🛠 Tech Stack•Jetpack Compose: For a modern, declarative UI.
•Media3 (ExoPlayer): The gold standard for Android video playback.
•Kotlin Coroutines: For non-blocking prefetch and progress tracking.
•Maven Publish: Ready for distribution as an AAR library.


📄 LicenseThis 
library is available under the MIT License. 
Feel free to use it in your commercial projects!



  
