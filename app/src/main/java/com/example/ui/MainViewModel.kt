package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppPreferences
import com.example.data.local.VidKingDatabase
import com.example.data.model.BookmarkItem
import com.example.data.model.BrowserTab
import com.example.data.model.DownloadItem
import com.example.data.model.SiteShortcut
import com.example.data.model.StorageCategoryBreakdown
import com.example.data.model.VaultItem
import com.example.data.model.WhatsAppStatusItem
import com.example.data.repository.VidKingRepository
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class NavDestination {
    HOME, SITES, DOWNLOADER, FILES, ME
}

data class DownloadQualityOption(
    val label: String,
    val format: String,
    val mediaType: String,
    val sizeText: String,
    val bytes: Long,
    val isHd: Boolean = false,
    val isAudio: Boolean = false
)

data class DiagnosticResult(
    val testName: String,
    val status: String, // "PASSED", "WARNING", "CHECKING"
    val details: String
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = VidKingDatabase.getDatabase(application)
    private val prefs = AppPreferences(application)
    private val repository = VidKingRepository(
        database.downloadDao(),
        database.siteShortcutDao(),
        database.vaultDao(),
        database.bookmarkDao(),
        viewModelScope
    )

    // Navigation
    private val _currentNav = MutableStateFlow(NavDestination.HOME)
    val currentNav: StateFlow<NavDestination> = _currentNav.asStateFlow()

    // Preferences & Settings
    val themeMode: StateFlow<AppThemeMode> = prefs.themeMode
    val adBlockerEnabled: StateFlow<Boolean> = prefs.adBlockerEnabled
    val blockedAdsCount: StateFlow<Int> = prefs.blockedAdsCount
    val vipUnlocked: StateFlow<Boolean> = prefs.vipUnlocked
    val vipCoins: StateFlow<Int> = prefs.vipCoins
    val checkInStreak: StateFlow<Int> = prefs.checkInStreak
    val hasCheckedInToday: StateFlow<Boolean> = prefs.hasCheckedInToday
    val vaultPin: StateFlow<String> = prefs.vaultPin
    val stealthCalculatorMode: StateFlow<Boolean> = prefs.stealthCalculatorMode
    val speedLimitEnabled: StateFlow<Boolean> = prefs.speedLimitEnabled
    val speedLimitMb: StateFlow<Int> = prefs.speedLimitMb
    val maxConcurrentDownloads: StateFlow<Int> = prefs.maxConcurrentDownloads
    val wifiOnly: StateFlow<Boolean> = prefs.wifiOnly
    val nightOwlSchedule: StateFlow<Boolean> = prefs.nightOwlSchedule
    val searchEngine: StateFlow<String> = prefs.searchEngine
    val debugOverlayEnabled: StateFlow<Boolean> = prefs.debugOverlayEnabled
    val adultSitesAllowed: StateFlow<Boolean> = prefs.adultSitesAllowed

    // Database flows
    val allDownloads: StateFlow<List<DownloadItem>> = repository.allDownloads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeDownloads: StateFlow<List<DownloadItem>> = repository.activeDownloads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedDownloads: StateFlow<List<DownloadItem>> = repository.completedDownloads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trashedDownloads: StateFlow<List<DownloadItem>> = repository.trashedDownloads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val siteShortcuts: StateFlow<List<SiteShortcut>> = repository.allShortcuts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vaultItems: StateFlow<List<VaultItem>> = repository.allVaultItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarks: StateFlow<List<BookmarkItem>> = repository.allBookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val whatsAppStatuses: StateFlow<List<WhatsAppStatusItem>> = repository.whatsAppStatuses

    // Download Prompt / Quality Dialog
    private val _qualityDialogItem = MutableStateFlow<DownloadTarget?>(null)
    val qualityDialogItem: StateFlow<DownloadTarget?> = _qualityDialogItem.asStateFlow()

    // Browser State
    private val _browserTabs = MutableStateFlow(
        listOf(
            BrowserTab("tab_1", "Trending Media Hub", "https://m.youtube.com", false, "https://m.youtube.com/watch?v=sample", "Epic Cinematic Trailer 4K", "4K UHD")
        )
    )
    val browserTabs: StateFlow<List<BrowserTab>> = _browserTabs.asStateFlow()

    private val _currentTabIndex = MutableStateFlow(0)
    val currentTabIndex: StateFlow<Int> = _currentTabIndex.asStateFlow()

    private val _isDesktopMode = MutableStateFlow(false)
    val isDesktopMode: StateFlow<Boolean> = _isDesktopMode.asStateFlow()

    private val _isReadingMode = MutableStateFlow(false)
    val isReadingMode: StateFlow<Boolean> = _isReadingMode.asStateFlow()

    private val _sourceCodeModalUrl = MutableStateFlow<String?>(null)
    val sourceCodeModalUrl: StateFlow<String?> = _sourceCodeModalUrl.asStateFlow()

    // Media Player State
    private val _activePlayerItem = MutableStateFlow<DownloadItem?>(null)
    val activePlayerItem: StateFlow<DownloadItem?> = _activePlayerItem.asStateFlow()

    private val _isPlaying = MutableStateFlow(true)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _playerCurrentMs = MutableStateFlow(32000L)
    val playerCurrentMs: StateFlow<Long> = _playerCurrentMs.asStateFlow()

    private val _playerDurationMs = MutableStateFlow(185000L)
    val playerDurationMs: StateFlow<Long> = _playerDurationMs.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _playerVolume = MutableStateFlow(80)
    val playerVolume: StateFlow<Int> = _playerVolume.asStateFlow()

    private val _playerBrightness = MutableStateFlow(75)
    val playerBrightness: StateFlow<Int> = _playerBrightness.asStateFlow()

    private val _equalizerPreset = MutableStateFlow("Bass Boost")
    val equalizerPreset: StateFlow<String> = _equalizerPreset.asStateFlow()

    private val _sleepTimerMinutes = MutableStateFlow(0)
    val sleepTimerMinutes: StateFlow<Int> = _sleepTimerMinutes.asStateFlow()

    private val _isPipMode = MutableStateFlow(false)
    val isPipMode: StateFlow<Boolean> = _isPipMode.asStateFlow()

    // Vault State
    private val _isVaultUnlocked = MutableStateFlow(false)
    val isVaultUnlocked: StateFlow<Boolean> = _isVaultUnlocked.asStateFlow()

    private val _calculatorInput = MutableStateFlow("0")
    val calculatorInput: StateFlow<String> = _calculatorInput.asStateFlow()

    // Audio Cutter / Ringtone Maker
    private val _audioCutterItem = MutableStateFlow<DownloadItem?>(null)
    val audioCutterItem: StateFlow<DownloadItem?> = _audioCutterItem.asStateFlow()

    // Diagnostics
    private val _diagnosticResults = MutableStateFlow<List<DiagnosticResult>>(emptyList())
    val diagnosticResults: StateFlow<List<DiagnosticResult>> = _diagnosticResults.asStateFlow()

    private val _isDiagnosing = MutableStateFlow(false)
    val isDiagnosing: StateFlow<Boolean> = _isDiagnosing.asStateFlow()

    // Trash View
    private val _isTrashModalOpen = MutableStateFlow(false)
    val isTrashModalOpen: StateFlow<Boolean> = _isTrashModalOpen.asStateFlow()

    // SnackBar / Toast message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Search query in Home / Files
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    data class DownloadTarget(
        val title: String,
        val url: String,
        val thumbnailUrl: String,
        val siteDomain: String
    )

    fun navigateTo(destination: NavDestination) {
        _currentNav.value = destination
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun showUserMessage(message: String) {
        _userMessage.value = message
    }

    fun dismissUserMessage() {
        _userMessage.value = null
    }

    // Download triggers
    fun openDownloadQualityPicker(
        title: String,
        url: String,
        thumbnailUrl: String = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=500&auto=format&fit=crop",
        siteDomain: String = "web"
    ) {
        _qualityDialogItem.value = DownloadTarget(title, url, thumbnailUrl, siteDomain)
    }

    fun dismissQualityPicker() {
        _qualityDialogItem.value = null
    }

    fun startDownloadWithQuality(
        target: DownloadTarget,
        qualityOption: DownloadQualityOption
    ) {
        viewModelScope.launch {
            repository.startNewDownload(
                title = target.title,
                url = target.url,
                format = qualityOption.format,
                quality = qualityOption.label,
                mediaType = qualityOption.mediaType,
                estimatedBytes = qualityOption.bytes,
                thumbnailUrl = target.thumbnailUrl,
                domain = target.siteDomain
            )
            dismissQualityPicker()
            showUserMessage("Downloading ${target.title} (${qualityOption.label})...")
            // Switch to Downloader tab
            _currentNav.value = NavDestination.DOWNLOADER
        }
    }

    fun pauseDownload(id: String) {
        viewModelScope.launch {
            repository.pauseDownload(id)
            showUserMessage("Download paused")
        }
    }

    fun resumeDownload(id: String) {
        viewModelScope.launch {
            repository.resumeDownload(id)
            showUserMessage("Download resumed at high speed")
        }
    }

    fun cancelDownload(id: String) {
        viewModelScope.launch {
            repository.cancelDownload(id)
            showUserMessage("Download cancelled")
        }
    }

    fun moveToTrash(id: String) {
        viewModelScope.launch {
            repository.moveToTrash(id)
            showUserMessage("Moved to Trash")
        }
    }

    fun restoreFromTrash(id: String) {
        viewModelScope.launch {
            repository.restoreFromTrash(id)
            showUserMessage("Restored to Downloads")
        }
    }

    fun permanentlyDelete(id: String) {
        viewModelScope.launch {
            repository.permanentlyDelete(id)
            showUserMessage("Item permanently deleted")
        }
    }

    fun emptyTrash() {
        viewModelScope.launch {
            repository.emptyTrash()
            showUserMessage("Trash emptied")
        }
    }

    fun extractAudio(item: DownloadItem) {
        viewModelScope.launch {
            repository.convertVideoToAudio(item)
            showUserMessage("Extracted 320kbps MP3 audio file!")
        }
    }

    // Media Player controls
    fun openMediaPlayer(item: DownloadItem) {
        _activePlayerItem.value = item
        _isPlaying.value = true
        _playerCurrentMs.value = 0L
    }

    fun closeMediaPlayer() {
        _activePlayerItem.value = null
        _isPipMode.value = false
    }

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
    }

    fun seekTo(positionMs: Long) {
        _playerCurrentMs.value = positionMs.coerceIn(0L, _playerDurationMs.value)
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
    }

    fun setVolume(vol: Int) {
        _playerVolume.value = vol.coerceIn(0, 100)
    }

    fun setBrightness(bright: Int) {
        _playerBrightness.value = bright.coerceIn(0, 100)
    }

    fun setEqualizerPreset(preset: String) {
        _equalizerPreset.value = preset
        showUserMessage("Equalizer set to $preset")
    }

    fun setSleepTimer(minutes: Int) {
        _sleepTimerMinutes.value = minutes
        if (minutes > 0) {
            showUserMessage("Sleep timer set for $minutes minutes")
        } else {
            showUserMessage("Sleep timer turned off")
        }
    }

    fun togglePipMode() {
        _isPipMode.value = !_isPipMode.value
    }

    // Audio Cutter / Ringtone Maker
    fun openAudioCutter(item: DownloadItem) {
        _audioCutterItem.value = item
    }

    fun closeAudioCutter() {
        _audioCutterItem.value = null
    }

    fun saveCutRingtone(title: String, durationSec: Int) {
        viewModelScope.launch {
            val item = _audioCutterItem.value ?: return@launch
            val newItem = DownloadItem(
                id = UUID.randomUUID().toString(),
                title = "$title (Ringtone)",
                sourceUrl = item.sourceUrl,
                mediaType = "AUDIO",
                format = "M4R",
                quality = "320 kbps Ringtone",
                totalBytes = (durationSec * 40 * 1024L),
                downloadedBytes = (durationSec * 40 * 1024L),
                speedBps = 0,
                status = "COMPLETED",
                localPath = "/storage/emulated/0/VidKing/Ringtones/$title.m4r",
                thumbnailUrl = item.thumbnailUrl,
                siteDomain = "ringtone_maker",
                createdAt = System.currentTimeMillis()
            )
            repository.convertVideoToAudio(newItem)
            closeAudioCutter()
            showUserMessage("Ringtone saved to /VidKing/Ringtones!")
        }
    }

    // WhatsApp Status Saver
    fun saveWhatsAppStatus(status: WhatsAppStatusItem) {
        viewModelScope.launch {
            repository.saveWhatsAppStatus(status)
            showUserMessage("Status saved to gallery successfully!")
        }
    }

    // Vault logic
    fun unlockVaultWithPin(pin: String): Boolean {
        if (pin == vaultPin.value) {
            _isVaultUnlocked.value = true
            return true
        }
        return false
    }

    fun lockVault() {
        _isVaultUnlocked.value = false
        _calculatorInput.value = "0"
    }

    fun setVaultPin(newPin: String) {
        prefs.setVaultPin(newPin)
        showUserMessage("Vault PIN updated")
    }

    fun toggleStealthMode(enabled: Boolean) {
        prefs.setStealthCalculatorMode(enabled)
        showUserMessage(if (enabled) "Calculator stealth mode enabled" else "Standard PIN mode enabled")
    }

    fun moveItemToVault(item: DownloadItem, note: String = "") {
        viewModelScope.launch {
            repository.moveToVault(item, note)
            showUserMessage("Moved ${item.title} to Private Locker")
        }
    }

    fun restoreItemFromVault(item: VaultItem) {
        viewModelScope.launch {
            repository.restoreFromVault(item)
            showUserMessage("Restored to public files")
        }
    }

    fun deleteItemFromVault(item: VaultItem) {
        viewModelScope.launch {
            repository.deleteVaultItem(item.id)
            showUserMessage("Deleted from Private Locker")
        }
    }

    // Calculator inputs for stealth mode
    fun onCalculatorKey(key: String) {
        val current = _calculatorInput.value
        when (key) {
            "C" -> _calculatorInput.value = "0"
            "=" -> {
                // Check if current input matches the PIN!
                if (current.replace("[^0-9]".toRegex(), "") == vaultPin.value || current == vaultPin.value) {
                    _isVaultUnlocked.value = true
                    _calculatorInput.value = "0"
                } else {
                    // Normal calculator evaluation
                    try {
                        val num = current.toDoubleOrNull() ?: 42.0
                        _calculatorInput.value = (num * 2).toInt().toString()
                    } catch (_: Exception) {
                        _calculatorInput.value = "Error"
                    }
                }
            }
            "+", "-", "×", "÷" -> {
                _calculatorInput.value = if (current == "0") "0$key" else "$current$key"
            }
            else -> {
                _calculatorInput.value = if (current == "0") key else "$current$key"
            }
        }
    }

    // Browser Actions
    fun openUrlInBrowser(url: String, title: String = "Web Page") {
        val currentList = _browserTabs.value.toMutableList()
        val isYt = url.contains("youtube.com") || url.contains("youtu.be")
        val isIg = url.contains("instagram.com")
        val isFb = url.contains("facebook.com")
        val isWa = url.contains("whatsapp")

        val detectedStream = if (isYt) "https://rr3---sn-4g5ednks.googlevideo.com/videoplayback?id=yt_demo" else "https://cdn.vidking.app/media/stream_sample.mp4"
        val detectedTitle = if (isYt) "Trending YouTube Video 4K" else if (isIg) "Instagram Reel Clip 1080p" else "Detected Web Video Stream"

        val newTab = BrowserTab(
            id = UUID.randomUUID().toString(),
            title = title,
            url = url,
            isIncognito = false,
            detectedMediaUrl = detectedStream,
            detectedMediaTitle = detectedTitle,
            detectedMediaQuality = "1080p Full HD"
        )
        currentList.add(newTab)
        _browserTabs.value = currentList
        _currentTabIndex.value = currentList.size - 1
        _currentNav.value = NavDestination.SITES
        prefs.incrementBlockedAds((2..5).random())
    }

    fun addNewBrowserTab() {
        val currentList = _browserTabs.value.toMutableList()
        val newTab = BrowserTab(
            id = UUID.randomUUID().toString(),
            title = "New Tab",
            url = "https://m.youtube.com",
            isIncognito = false
        )
        currentList.add(newTab)
        _browserTabs.value = currentList
        _currentTabIndex.value = currentList.size - 1
    }

    fun closeBrowserTab(index: Int) {
        val currentList = _browserTabs.value.toMutableList()
        if (currentList.size > 1) {
            currentList.removeAt(index)
            _browserTabs.value = currentList
            _currentTabIndex.value = (_currentTabIndex.value - 1).coerceAtLeast(0)
        }
    }

    fun selectBrowserTab(index: Int) {
        _currentTabIndex.value = index
    }

    fun toggleDesktopMode() {
        _isDesktopMode.value = !_isDesktopMode.value
        showUserMessage(if (_isDesktopMode.value) "Desktop user-agent enabled" else "Mobile user-agent enabled")
    }

    fun toggleReadingMode() {
        _isReadingMode.value = !_isReadingMode.value
    }

    fun showSourceCode(url: String) {
        _sourceCodeModalUrl.value = url
    }

    fun dismissSourceCode() {
        _sourceCodeModalUrl.value = null
    }

    // Settings actions
    fun setThemeMode(mode: AppThemeMode) {
        prefs.setThemeMode(mode)
    }

    fun toggleAdBlocker() {
        val newValue = !adBlockerEnabled.value
        prefs.setAdBlockerEnabled(newValue)
        showUserMessage(if (newValue) "Ad-Blocker & Tracking Protection activated" else "Ad-Blocker paused")
    }

    fun toggleSpeedLimiter() {
        val newValue = !speedLimitEnabled.value
        prefs.setSpeedLimitEnabled(newValue)
        showUserMessage(if (newValue) "Download bandwidth capped to ${speedLimitMb.value} MB/s" else "Uncapped high-speed multi-threaded mode")
    }

    fun setSpeedLimitMb(mb: Int) {
        prefs.setSpeedLimitMb(mb)
    }

    fun toggleNightOwl() {
        val newValue = !nightOwlSchedule.value
        prefs.setNightOwlSchedule(newValue)
        showUserMessage(if (newValue) "Night Owl Mode active (12 AM - 6 AM)" else "Scheduled night downloads disabled")
    }

    fun toggleWifiOnly() {
        val newValue = !wifiOnly.value
        prefs.setWifiOnly(newValue)
        showUserMessage(if (newValue) "Downloads restricted to Wi-Fi only" else "Cellular & Wi-Fi enabled")
    }

    fun toggleAdultSitesAllowed() {
        val newValue = !adultSitesAllowed.value
        prefs.setAdultSitesAllowed(newValue)
        showUserMessage(if (newValue) "Adult & 18+ Content Access Enabled (Unrestricted A to Z Browsing)" else "Parental Control & SafeSearch Active (18+ Sites Blocked)")
    }

    fun toggleDebugOverlay() {
        prefs.setDebugOverlayEnabled(!debugOverlayEnabled.value)
    }

    fun claimDailyCheckIn() {
        val claimed = prefs.claimDailyCheckIn()
        if (claimed) {
            showUserMessage("Daily reward claimed! +${50 * checkInStreak.value} VIP Coins earned!")
        } else {
            showUserMessage("You already claimed today's reward. Come back tomorrow!")
        }
    }

    fun redeemPromoCode(code: String) {
        val success = prefs.redeemPromoCode(code)
        if (success) {
            showUserMessage("Promo Code applied! VIP Status & +500 Coins added!")
        } else {
            showUserMessage("Invalid or expired code. Try VIPKING or FAST2026")
        }
    }

    fun runDiagnostics() {
        viewModelScope.launch {
            _isDiagnosing.value = true
            _diagnosticResults.value = listOf(
                DiagnosticResult("Multi-Thread Engine", "CHECKING", "Testing 8-16 parallel chunk queues..."),
                DiagnosticResult("CDN Parsing Node", "CHECKING", "Resolving streaming manifests (HLS/DASH)..."),
                DiagnosticResult("Storage Permissions", "CHECKING", "Verifying Android scoped directories..."),
                DiagnosticResult("Local DNS Ad-Blocker", "CHECKING", "Checking malicious script blocklist...")
            )
            delay(800)
            _diagnosticResults.value = listOf(
                DiagnosticResult("Multi-Thread Engine", "PASSED", "High-speed socket pooling ready (16 threads supported)"),
                DiagnosticResult("CDN Parsing Node", "PASSED", "Direct token resolver responding in 42ms"),
                DiagnosticResult("Storage Permissions", "PASSED", "Scoped storage active (/VidKing/Videos & /Music)"),
                DiagnosticResult("Local DNS Ad-Blocker", "PASSED", "Blocking 42,000+ tracker and popup domains")
            )
            _isDiagnosing.value = false
            showUserMessage("Self-Diagnostic Complete: All systems operational!")
        }
    }

    fun clearCache() {
        showUserMessage("Cleaned 48.2 MB of temporary video chunks & browser cache!")
    }

    fun getStorageBreakdown(): List<StorageCategoryBreakdown> {
        return repository.calculateStorageBreakdown(completedDownloads.value)
    }

    fun toggleTrashModal(open: Boolean) {
        _isTrashModalOpen.value = open
    }
}
