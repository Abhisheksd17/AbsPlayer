package com.example.absplayer

/**
 * The standard data model for a reel.
 * In a library, you could also provide an interface for users to implement.
 */
data class ReelItem(
    val id: String,
    val videoUrl: String,
    val thumbnailUrl: String? = null,
    val extraData: Map<String, Any> = emptyMap()
)
