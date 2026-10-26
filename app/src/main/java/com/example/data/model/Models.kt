package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloads")
data class DownloadItem(
    @PrimaryKey val id: String,
    val title: String,
    val sourceUrl: String,
    val mediaType: String, // "VIDEO", "AUDIO", "IMAGE", "STATUS"
    val format: String, // "MP4", "MP3", "M4A", "MKV"
    val quality: String, // "1080p 60fps", "720p HD", "320 kbps HQ", etc.
    val totalBytes: Long,
    val downloadedBytes: Long,
    val speedBps: Long,
    val status: String, // "DOWNLOADING", "PAUSED", "COMPLETED", "FAILED", "QUEUED"
    val localPath: String,
    val thumbnailUrl: String,
    val siteDomain: String,
    val createdAt: Long = System.currentTimeMillis(),
    val threadCount: Int = 8,
    val isScheduled: Boolean = false,
    val isVipSpeed: Boolean = false,
    val isTrashed: Boolean = false
) {
    val progress: Float
        get() = if (totalBytes > 0) (downloadedBytes.toFloat() / totalBytes).coerceIn(0f, 1f) else 0f

    val formattedSize: String
        get() {
            val mb = totalBytes.toDouble() / (1024 * 1024)
            return if (mb >= 1024) {
                String.format("%.2f GB", mb / 1024)
            } else {
                String.format("%.1f MB", mb)
            }
        }

    val formattedSpeed: String
        get() {
            val mb = speedBps.toDouble() / (1024 * 1024)
            return if (mb >= 1.0) {
                String.format("%.1f MB/s", mb)
            } else {
                String.format("%d KB/s", (speedBps / 1024).toInt())
            }
        }
}

@Entity(tableName = "site_shortcuts")
data class SiteShortcut(
    @PrimaryKey val id: String,
    val name: String,
    val url: String,
    val iconKey: String,
    val category: String, // "Social", "Video", "Music", "Tools"
    val badge: String? = null,
    val isPinned: Boolean = true,
    val sortOrder: Int = 0
)

@Entity(tableName = "vault_items")
data class VaultItem(
    @PrimaryKey val id: String,
    val title: String,
    val disguisedName: String,
    val mediaType: String, // "VIDEO", "PHOTO", "AUDIO", "DOC"
    val fileSize: Long,
    val lockedAt: Long = System.currentTimeMillis(),
    val thumbnailUrl: String,
    val originalPath: String,
    val note: String = ""
)

@Entity(tableName = "bookmarks")
data class BookmarkItem(
    @PrimaryKey val id: String,
    val title: String,
    val url: String,
    val folder: String = "Favorites",
    val timestamp: Long = System.currentTimeMillis()
)

data class WhatsAppStatusItem(
    val id: String,
    val senderName: String,
    val mediaUrl: String,
    val isVideo: Boolean,
    val durationText: String,
    val timestampText: String,
    val isSaved: Boolean = false
)

data class BrowserTab(
    val id: String,
    val title: String,
    val url: String,
    val isIncognito: Boolean = false,
    val detectedMediaUrl: String? = null,
    val detectedMediaTitle: String? = null,
    val detectedMediaQuality: String = "1080p FHD"
)

data class StorageCategoryBreakdown(
    val category: String,
    val sizeBytes: Long,
    val colorHex: Long,
    val fileCount: Int
)
