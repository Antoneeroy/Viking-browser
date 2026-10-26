package com.example.data.repository

import com.example.data.local.BookmarkDao
import com.example.data.local.DownloadDao
import com.example.data.local.SiteShortcutDao
import com.example.data.local.VaultDao
import com.example.data.model.BookmarkItem
import com.example.data.model.DownloadItem
import com.example.data.model.SiteShortcut
import com.example.data.model.StorageCategoryBreakdown
import com.example.data.model.VaultItem
import com.example.data.model.WhatsAppStatusItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

class VidKingRepository(
    private val downloadDao: DownloadDao,
    private val siteShortcutDao: SiteShortcutDao,
    private val vaultDao: VaultDao,
    private val bookmarkDao: BookmarkDao,
    private val scope: CoroutineScope
) {
    val allDownloads: Flow<List<DownloadItem>> = downloadDao.getAllDownloads()
    val activeDownloads: Flow<List<DownloadItem>> = downloadDao.getActiveDownloads()
    val completedDownloads: Flow<List<DownloadItem>> = downloadDao.getCompletedDownloads()
    val trashedDownloads: Flow<List<DownloadItem>> = downloadDao.getTrashedDownloads()
    val allShortcuts: Flow<List<SiteShortcut>> = siteShortcutDao.getAllShortcuts()
    val allVaultItems: Flow<List<VaultItem>> = vaultDao.getAllVaultItems()
    val allBookmarks: Flow<List<BookmarkItem>> = bookmarkDao.getAllBookmarks()

    private val _whatsAppStatuses = MutableStateFlow<List<WhatsAppStatusItem>>(emptyList())
    val whatsAppStatuses: StateFlow<List<WhatsAppStatusItem>> = _whatsAppStatuses.asStateFlow()

    private val downloadJobs = mutableMapOf<String, Job>()

    init {
        scope.launch(Dispatchers.IO) {
            seedDefaultDataIfEmpty()
            seedDemoWhatsAppStatuses()
            startDownloadEngineLoop()
        }
    }

    private suspend fun seedDefaultDataIfEmpty() {
        val defaultShortcuts = listOf(
            SiteShortcut("yt", "YouTube", "https://m.youtube.com", "youtube", "Video", "4K", true, 0),
            SiteShortcut("ig", "Instagram", "https://instagram.com", "instagram", "Social", "Reels", true, 1),
            SiteShortcut("wa", "WhatsApp Status", "https://web.whatsapp.com", "whatsapp", "Tools", "NEW", true, 2),
            SiteShortcut("fb", "Facebook", "https://m.facebook.com", "facebook", "Social", null, true, 3),
            SiteShortcut("tk", "TikTok", "https://www.tiktok.com", "tiktok", "Video", "No Watermark", true, 4),
            SiteShortcut("tw", "X (Twitter)", "https://x.com", "twitter", "Social", null, true, 5),
            SiteShortcut("pt", "Pinterest", "https://pinterest.com", "pinterest", "Social", "HD Pins", true, 6),
            SiteShortcut("sc", "SoundCloud", "https://soundcloud.com", "soundcloud", "Music", "Lossless", true, 7),
            SiteShortcut("dm", "DailyMotion", "https://www.dailymotion.com", "dailymotion", "Video", null, true, 8),
            SiteShortcut("vm", "Vimeo", "https://vimeo.com", "vimeo", "Video", "Pro", true, 9),
            SiteShortcut("rd", "Reddit", "https://reddit.com", "reddit", "Social", null, false, 10),
            SiteShortcut("twch", "Twitch", "https://twitch.tv", "twitch", "Video", "Live", false, 11)
        )
        siteShortcutDao.insertAll(defaultShortcuts)

        // Seed initial completed files and one active downloading item for rich UX
        val initialDownloads = listOf(
            DownloadItem(
                id = "demo_1",
                title = "Cyberpunk 2077 Night City 4K Ultra Cinematic Showcase",
                sourceUrl = "https://youtube.com/watch?v=demo_cyberpunk",
                mediaType = "VIDEO",
                format = "MP4",
                quality = "4K 60fps Ultra",
                totalBytes = 284 * 1024 * 1024L,
                downloadedBytes = 284 * 1024 * 1024L,
                speedBps = 0,
                status = "COMPLETED",
                localPath = "/storage/emulated/0/VidKing/Videos/Cyberpunk_2077_4K.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=500&auto=format&fit=crop",
                siteDomain = "youtube.com",
                createdAt = System.currentTimeMillis() - 7200000
            ),
            DownloadItem(
                id = "demo_2",
                title = "The Weeknd - Blinding Lights (Official Studio Master)",
                sourceUrl = "https://soundcloud.com/theweeknd/blinding-lights",
                mediaType = "AUDIO",
                format = "MP3",
                quality = "320 kbps Master",
                totalBytes = 11 * 1024 * 1024L,
                downloadedBytes = 11 * 1024 * 1024L,
                speedBps = 0,
                status = "COMPLETED",
                localPath = "/storage/emulated/0/VidKing/Music/Blinding_Lights.mp3",
                thumbnailUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500&auto=format&fit=crop",
                siteDomain = "soundcloud.com",
                createdAt = System.currentTimeMillis() - 18000000
            ),
            DownloadItem(
                id = "demo_3",
                title = "Viral Sunset Drone Reel @ Amalfi Coast",
                sourceUrl = "https://instagram.com/reel/amalfi_drone",
                mediaType = "VIDEO",
                format = "MP4",
                quality = "1080p FHD",
                totalBytes = 46 * 1024 * 1024L,
                downloadedBytes = 46 * 1024 * 1024L,
                speedBps = 0,
                status = "COMPLETED",
                localPath = "/storage/emulated/0/VidKing/Videos/Amalfi_Reel.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1533105079780-92b9be482077?w=500&auto=format&fit=crop",
                siteDomain = "instagram.com",
                createdAt = System.currentTimeMillis() - 43200000
            ),
            DownloadItem(
                id = "demo_4",
                title = "Top 50 Lo-Fi Beats to Relax / Study / Code to [Full Session]",
                sourceUrl = "https://youtube.com/watch?v=lofi_study",
                mediaType = "AUDIO",
                format = "MP3",
                quality = "320 kbps",
                totalBytes = 85 * 1024 * 1024L,
                downloadedBytes = 28 * 1024 * 1024L,
                speedBps = 4850000L, // 4.85 MB/s
                status = "DOWNLOADING",
                localPath = "/storage/emulated/0/VidKing/Music/LoFi_Beats.mp3",
                thumbnailUrl = "https://images.unsplash.com/photo-1518791841217-8f162f1e1131?w=500&auto=format&fit=crop",
                siteDomain = "youtube.com",
                createdAt = System.currentTimeMillis() - 300000,
                threadCount = 8
            )
        )
        downloadDao.insertAll(initialDownloads)

        // Seed sample vault item
        val initialVaultItem = VaultItem(
            id = "vault_secret_1",
            title = "Personal Travel Passport & Flight Tickets",
            disguisedName = "system_cache_backup_09.dat",
            mediaType = "PHOTO",
            fileSize = 4 * 1024 * 1024L,
            thumbnailUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=500&auto=format&fit=crop",
            originalPath = "/storage/emulated/0/Documents/passports.jpg",
            note = "Biometric protected travel credentials"
        )
        vaultDao.insert(initialVaultItem)
    }

    private fun seedDemoWhatsAppStatuses() {
        val statuses = listOf(
            WhatsAppStatusItem(
                id = "wa_stat_1",
                senderName = "Alex Rivera",
                mediaUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=500&auto=format&fit=crop",
                isVideo = false,
                durationText = "Image",
                timestampText = "12 min ago"
            ),
            WhatsAppStatusItem(
                id = "wa_stat_2",
                senderName = "Sophie Taylor",
                mediaUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500&auto=format&fit=crop",
                isVideo = true,
                durationText = "0:30",
                timestampText = "45 min ago"
            ),
            WhatsAppStatusItem(
                id = "wa_stat_3",
                senderName = "David Chen",
                mediaUrl = "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?w=500&auto=format&fit=crop",
                isVideo = true,
                durationText = "0:15",
                timestampText = "2 hrs ago"
            ),
            WhatsAppStatusItem(
                id = "wa_stat_4",
                senderName = "Emma Watson",
                mediaUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=500&auto=format&fit=crop",
                isVideo = false,
                durationText = "Image",
                timestampText = "3 hrs ago"
            )
        )
        _whatsAppStatuses.value = statuses
    }

    private fun startDownloadEngineLoop() {
        scope.launch(Dispatchers.IO) {
            while (isActive) {
                delay(1000)
                // Periodically update downloading items
                val downloadingItems = mutableListOf<DownloadItem>()
                // Query active
                downloadDao.getAllDownloads().collect { list ->
                    val active = list.filter { it.status == "DOWNLOADING" && !it.isTrashed }
                    for (item in active) {
                        val chunkIncrement = (2_500_000L..5_800_000L).random()
                        val newDownloaded = (item.downloadedBytes + chunkIncrement).coerceAtMost(item.totalBytes)
                        val newStatus = if (newDownloaded >= item.totalBytes) "COMPLETED" else "DOWNLOADING"
                        val newSpeed = if (newStatus == "COMPLETED") 0L else chunkIncrement

                        downloadDao.update(
                            item.copy(
                                downloadedBytes = newDownloaded,
                                status = newStatus,
                                speedBps = newSpeed
                            )
                        )
                    }
                }
            }
        }
    }

    suspend fun startNewDownload(
        title: String,
        url: String,
        format: String,
        quality: String,
        mediaType: String,
        estimatedBytes: Long,
        thumbnailUrl: String,
        domain: String = "web"
    ): DownloadItem {
        val id = UUID.randomUUID().toString()
        val extension = format.lowercase()
        val path = "/storage/emulated/0/VidKing/${if (mediaType == "AUDIO") "Music" else "Videos"}/${title.replace(" ", "_")}.$extension"

        val item = DownloadItem(
            id = id,
            title = title,
            sourceUrl = url,
            mediaType = mediaType,
            format = format,
            quality = quality,
            totalBytes = estimatedBytes,
            downloadedBytes = 0L,
            speedBps = 4_500_000L,
            status = "DOWNLOADING",
            localPath = path,
            thumbnailUrl = thumbnailUrl,
            siteDomain = domain,
            createdAt = System.currentTimeMillis(),
            threadCount = 8
        )
        downloadDao.insertOrUpdate(item)
        return item
    }

    suspend fun pauseDownload(id: String) {
        val item = downloadDao.getDownloadById(id) ?: return
        downloadDao.update(item.copy(status = "PAUSED", speedBps = 0))
    }

    suspend fun resumeDownload(id: String) {
        val item = downloadDao.getDownloadById(id) ?: return
        downloadDao.update(item.copy(status = "DOWNLOADING", speedBps = 4_200_000L))
    }

    suspend fun cancelDownload(id: String) {
        downloadDao.permanentlyDelete(id)
    }

    suspend fun moveToTrash(id: String) {
        downloadDao.moveToTrash(id)
    }

    suspend fun restoreFromTrash(id: String) {
        downloadDao.restoreFromTrash(id)
    }

    suspend fun permanentlyDelete(id: String) {
        downloadDao.permanentlyDelete(id)
    }

    suspend fun emptyTrash() {
        downloadDao.emptyTrash()
    }

    suspend fun convertVideoToAudio(downloadItem: DownloadItem): DownloadItem {
        val audioTitle = "${downloadItem.title} (Extracted Audio)"
        val audioBytes = (downloadItem.totalBytes * 0.15).toLong().coerceAtLeast(4 * 1024 * 1024L)
        val newItem = DownloadItem(
            id = UUID.randomUUID().toString(),
            title = audioTitle,
            sourceUrl = downloadItem.sourceUrl,
            mediaType = "AUDIO",
            format = "MP3",
            quality = "320 kbps HQ",
            totalBytes = audioBytes,
            downloadedBytes = audioBytes,
            speedBps = 0,
            status = "COMPLETED",
            localPath = "/storage/emulated/0/VidKing/Music/${audioTitle.replace(" ", "_")}.mp3",
            thumbnailUrl = downloadItem.thumbnailUrl,
            siteDomain = downloadItem.siteDomain,
            createdAt = System.currentTimeMillis()
        )
        downloadDao.insertOrUpdate(newItem)
        return newItem
    }

    suspend fun addBookmark(title: String, url: String) {
        val bookmark = BookmarkItem(
            id = UUID.randomUUID().toString(),
            title = title,
            url = url,
            folder = "Favorites"
        )
        bookmarkDao.insert(bookmark)
    }

    suspend fun deleteBookmark(id: String) {
        bookmarkDao.delete(id)
    }

    suspend fun addSiteShortcut(shortcut: SiteShortcut) {
        siteShortcutDao.insertOrUpdate(shortcut)
    }

    suspend fun deleteSiteShortcut(id: String) {
        siteShortcutDao.delete(id)
    }

    suspend fun moveToVault(downloadItem: DownloadItem, note: String = "") {
        val vaultItem = VaultItem(
            id = UUID.randomUUID().toString(),
            title = downloadItem.title,
            disguisedName = "sys_enc_${System.currentTimeMillis() % 10000}.bin",
            mediaType = downloadItem.mediaType,
            fileSize = downloadItem.totalBytes,
            thumbnailUrl = downloadItem.thumbnailUrl,
            originalPath = downloadItem.localPath,
            note = note
        )
        vaultDao.insert(vaultItem)
        // Remove from public downloads list
        downloadDao.permanentlyDelete(downloadItem.id)
    }

    suspend fun restoreFromVault(vaultItem: VaultItem) {
        val downloadItem = DownloadItem(
            id = UUID.randomUUID().toString(),
            title = vaultItem.title,
            sourceUrl = "local_vault",
            mediaType = vaultItem.mediaType,
            format = if (vaultItem.mediaType == "AUDIO") "MP3" else "MP4",
            quality = "Original",
            totalBytes = vaultItem.fileSize,
            downloadedBytes = vaultItem.fileSize,
            speedBps = 0,
            status = "COMPLETED",
            localPath = vaultItem.originalPath,
            thumbnailUrl = vaultItem.thumbnailUrl,
            siteDomain = "device"
        )
        downloadDao.insertOrUpdate(downloadItem)
        vaultDao.delete(vaultItem.id)
    }

    suspend fun deleteVaultItem(id: String) {
        vaultDao.delete(id)
    }

    suspend fun saveWhatsAppStatus(status: WhatsAppStatusItem): DownloadItem {
        val newItem = DownloadItem(
            id = UUID.randomUUID().toString(),
            title = "WhatsApp Status by ${status.senderName}",
            sourceUrl = "whatsapp_status://${status.id}",
            mediaType = if (status.isVideo) "VIDEO" else "IMAGE",
            format = if (status.isVideo) "MP4" else "JPG",
            quality = "Original Status",
            totalBytes = if (status.isVideo) 15 * 1024 * 1024L else 2 * 1024 * 1024L,
            downloadedBytes = if (status.isVideo) 15 * 1024 * 1024L else 2 * 1024 * 1024L,
            speedBps = 0,
            status = "COMPLETED",
            localPath = "/storage/emulated/0/VidKing/Status/Status_${status.id}.${if (status.isVideo) "mp4" else "jpg"}",
            thumbnailUrl = status.mediaUrl,
            siteDomain = "whatsapp"
        )
        downloadDao.insertOrUpdate(newItem)

        // Mark as saved in memory
        _whatsAppStatuses.value = _whatsAppStatuses.value.map {
            if (it.id == status.id) it.copy(isSaved = true) else it
        }
        return newItem
    }

    fun calculateStorageBreakdown(completedList: List<DownloadItem>): List<StorageCategoryBreakdown> {
        var videoSize = 0L
        var videoCount = 0
        var audioSize = 0L
        var audioCount = 0
        var otherSize = 0L
        var otherCount = 0

        for (item in completedList) {
            when (item.mediaType) {
                "VIDEO" -> {
                    videoSize += item.totalBytes
                    videoCount++
                }
                "AUDIO" -> {
                    audioSize += item.totalBytes
                    audioCount++
                }
                else -> {
                    otherSize += item.totalBytes
                    otherCount++
                }
            }
        }

        val cacheSize = 48 * 1024 * 1024L // 48 MB app cache
        val appDataSize = 18 * 1024 * 1024L

        return listOf(
            StorageCategoryBreakdown("Videos", videoSize, 0xFFFFB703, videoCount),
            StorageCategoryBreakdown("Music & Audio", audioSize, 0xFFE63946, audioCount),
            StorageCategoryBreakdown("App Cache", cacheSize, 0xFF219EBC, 1),
            StorageCategoryBreakdown("Other Media", otherSize + appDataSize, 0xFF9E9AA7, otherCount + 1)
        )
    }
}
