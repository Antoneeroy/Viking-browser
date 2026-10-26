package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.NavDestination
import com.example.ui.components.DeveloperDebugOverlay
import com.example.ui.components.DownloadQualityDialog
import com.example.ui.components.VidKingTopBar
import com.example.ui.screens.AudioCutterDialog
import com.example.ui.screens.BrowserScreen
import com.example.ui.screens.DownloaderScreen
import com.example.ui.screens.FilesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MeScreen
import com.example.ui.screens.MediaPlayerDialog
import com.example.ui.screens.VaultScreen
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.VidKingTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

            VidKingTheme(themeMode = themeMode) {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: MainViewModel) {
    val currentNav by viewModel.currentNav.collectAsStateWithLifecycle()
    val blockedAdsCount by viewModel.blockedAdsCount.collectAsStateWithLifecycle()
    val vipUnlocked by viewModel.vipUnlocked.collectAsStateWithLifecycle()
    val vipCoins by viewModel.vipCoins.collectAsStateWithLifecycle()
    val checkInStreak by viewModel.checkInStreak.collectAsStateWithLifecycle()
    val hasCheckedInToday by viewModel.hasCheckedInToday.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val adBlockerEnabled by viewModel.adBlockerEnabled.collectAsStateWithLifecycle()
    val speedLimitEnabled by viewModel.speedLimitEnabled.collectAsStateWithLifecycle()
    val speedLimitMb by viewModel.speedLimitMb.collectAsStateWithLifecycle()
    val wifiOnly by viewModel.wifiOnly.collectAsStateWithLifecycle()
    val nightOwlSchedule by viewModel.nightOwlSchedule.collectAsStateWithLifecycle()
    val debugOverlayEnabled by viewModel.debugOverlayEnabled.collectAsStateWithLifecycle()
    val adultSitesAllowed by viewModel.adultSitesAllowed.collectAsStateWithLifecycle()

    val siteShortcuts by viewModel.siteShortcuts.collectAsStateWithLifecycle()
    val activeDownloads by viewModel.activeDownloads.collectAsStateWithLifecycle()
    val completedDownloads by viewModel.completedDownloads.collectAsStateWithLifecycle()
    val trashedDownloads by viewModel.trashedDownloads.collectAsStateWithLifecycle()
    val vaultItems by viewModel.vaultItems.collectAsStateWithLifecycle()
    val whatsAppStatuses by viewModel.whatsAppStatuses.collectAsStateWithLifecycle()

    val qualityTarget by viewModel.qualityDialogItem.collectAsStateWithLifecycle()
    val activePlayerItem by viewModel.activePlayerItem.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val playerCurrentMs by viewModel.playerCurrentMs.collectAsStateWithLifecycle()
    val playerDurationMs by viewModel.playerDurationMs.collectAsStateWithLifecycle()
    val playbackSpeed by viewModel.playbackSpeed.collectAsStateWithLifecycle()
    val playerVolume by viewModel.playerVolume.collectAsStateWithLifecycle()
    val playerBrightness by viewModel.playerBrightness.collectAsStateWithLifecycle()
    val equalizerPreset by viewModel.equalizerPreset.collectAsStateWithLifecycle()
    val sleepTimerMinutes by viewModel.sleepTimerMinutes.collectAsStateWithLifecycle()
    val isPipMode by viewModel.isPipMode.collectAsStateWithLifecycle()

    val isVaultUnlocked by viewModel.isVaultUnlocked.collectAsStateWithLifecycle()
    val calculatorInput by viewModel.calculatorInput.collectAsStateWithLifecycle()
    val stealthMode by viewModel.stealthCalculatorMode.collectAsStateWithLifecycle()

    val browserTabs by viewModel.browserTabs.collectAsStateWithLifecycle()
    val currentTabIndex by viewModel.currentTabIndex.collectAsStateWithLifecycle()
    val isDesktopMode by viewModel.isDesktopMode.collectAsStateWithLifecycle()
    val isReadingMode by viewModel.isReadingMode.collectAsStateWithLifecycle()

    val audioCutterItem by viewModel.audioCutterItem.collectAsStateWithLifecycle()
    val isDiagnosing by viewModel.isDiagnosing.collectAsStateWithLifecycle()
    val diagnosticResults by viewModel.diagnosticResults.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    var isVaultOpen by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            viewModel.dismissUserMessage()
        }
    }

    // Handle back button when in Vault or subviews
    BackHandler(enabled = isVaultOpen || currentNav != NavDestination.HOME) {
        if (isVaultOpen) {
            isVaultOpen = false
            viewModel.lockVault()
        } else if (currentNav != NavDestination.HOME) {
            viewModel.navigateTo(NavDestination.HOME)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("vidking_main_scaffold"),
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (!isVaultOpen && activePlayerItem == null) {
                VidKingTopBar(
                    title = when (currentNav) {
                        NavDestination.HOME -> "VidKing"
                        NavDestination.SITES -> "VidKing Browser"
                        NavDestination.DOWNLOADER -> "Download Manager"
                        NavDestination.FILES -> "My Media & Files"
                        NavDestination.ME -> "My Account & VIP"
                    },
                    subtitle = when (currentNav) {
                        NavDestination.HOME -> "High-Speed Media Downloader"
                        NavDestination.SITES -> "1000+ Sites • Ad-Block Active"
                        NavDestination.DOWNLOADER -> "${activeDownloads.size} Active • ${completedDownloads.size} Completed"
                        NavDestination.FILES -> "Universal Media Hub"
                        NavDestination.ME -> "Settings & Performance"
                    },
                    blockedAdsCount = blockedAdsCount,
                    vipUnlocked = vipUnlocked,
                    vipCoins = vipCoins,
                    onVipClick = { viewModel.navigateTo(NavDestination.ME) },
                    onAdBlockClick = { viewModel.toggleAdBlocker() },
                    onSearchClick = { viewModel.navigateTo(NavDestination.HOME) }
                )
            }
        },
        bottomBar = {
            if (!isVaultOpen && (activePlayerItem == null || isPipMode)) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("vidking_bottom_nav")
                ) {
                    NavigationBarItem(
                        selected = currentNav == NavDestination.HOME,
                        onClick = { viewModel.navigateTo(NavDestination.HOME) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = RoyalGold,
                            indicatorColor = RoyalGold
                        )
                    )
                    NavigationBarItem(
                        selected = currentNav == NavDestination.SITES,
                        onClick = { viewModel.navigateTo(NavDestination.SITES) },
                        icon = { Icon(Icons.Default.Explore, contentDescription = "Sites") },
                        label = { Text("Sites", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = RoyalGold,
                            indicatorColor = RoyalGold
                        )
                    )
                    NavigationBarItem(
                        selected = currentNav == NavDestination.DOWNLOADER,
                        onClick = { viewModel.navigateTo(NavDestination.DOWNLOADER) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (activeDownloads.isNotEmpty()) {
                                        Badge(containerColor = CrimsonRed) {
                                            Text(
                                                text = "${activeDownloads.size}",
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Download, contentDescription = "Downloader")
                            }
                        },
                        label = { Text("Downloader", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = RoyalGold,
                            indicatorColor = RoyalGold
                        )
                    )
                    NavigationBarItem(
                        selected = currentNav == NavDestination.FILES,
                        onClick = { viewModel.navigateTo(NavDestination.FILES) },
                        icon = { Icon(Icons.Default.Folder, contentDescription = "Files") },
                        label = { Text("Files", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = RoyalGold,
                            indicatorColor = RoyalGold
                        )
                    )
                    NavigationBarItem(
                        selected = currentNav == NavDestination.ME,
                        onClick = { viewModel.navigateTo(NavDestination.ME) },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Me") },
                        label = { Text("Me", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = RoyalGold,
                            indicatorColor = RoyalGold
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Developer Debug HUD Overlay (when enabled)
                if (debugOverlayEnabled) {
                    val totalSpeed = activeDownloads.sumOf { it.speedBps }
                    DeveloperDebugOverlay(
                        activeDownloadCount = activeDownloads.size,
                        completedCount = completedDownloads.size,
                        blockedAds = blockedAdsCount,
                        speedBps = totalSpeed,
                        threadCount = 16
                    )
                }

                if (isVaultOpen) {
                    VaultScreen(
                        viewModel = viewModel,
                        vaultItems = vaultItems,
                        isVaultUnlocked = isVaultUnlocked,
                        calculatorInput = calculatorInput,
                        stealthMode = stealthMode,
                        onBack = {
                            isVaultOpen = false
                            viewModel.lockVault()
                        }
                    )
                } else {
                    when (currentNav) {
                        NavDestination.HOME -> HomeScreen(
                            viewModel = viewModel,
                            siteShortcuts = siteShortcuts,
                            searchQuery = searchQuery,
                            vipCoins = vipCoins,
                            adultSitesAllowed = adultSitesAllowed,
                            onNavigateToSites = { url ->
                                viewModel.openUrlInBrowser(url, "Web")
                            },
                            onNavigateToFiles = { viewModel.navigateTo(NavDestination.FILES) },
                            onOpenVault = { isVaultOpen = true }
                        )

                        NavDestination.SITES -> BrowserScreen(
                            viewModel = viewModel,
                            browserTabs = browserTabs,
                            currentTabIndex = currentTabIndex,
                            isDesktopMode = isDesktopMode,
                            isReadingMode = isReadingMode,
                            adBlockerEnabled = adBlockerEnabled,
                            blockedAdsCount = blockedAdsCount,
                            adultSitesAllowed = adultSitesAllowed
                        )

                        NavDestination.DOWNLOADER -> DownloaderScreen(
                            viewModel = viewModel,
                            activeDownloads = activeDownloads,
                            completedDownloads = completedDownloads,
                            trashedDownloads = trashedDownloads,
                            speedLimitEnabled = speedLimitEnabled,
                            speedLimitMb = speedLimitMb,
                            nightOwlActive = nightOwlSchedule
                        )

                        NavDestination.FILES -> FilesScreen(
                            viewModel = viewModel,
                            completedDownloads = completedDownloads,
                            whatsAppStatuses = whatsAppStatuses,
                            onOpenVault = { isVaultOpen = true }
                        )

                        NavDestination.ME -> MeScreen(
                            viewModel = viewModel,
                            themeMode = themeMode,
                            vipUnlocked = vipUnlocked,
                            vipCoins = vipCoins,
                            checkInStreak = checkInStreak,
                            hasCheckedInToday = hasCheckedInToday,
                            adBlockerEnabled = adBlockerEnabled,
                            wifiOnly = wifiOnly,
                            nightOwl = nightOwlSchedule,
                            debugOverlayEnabled = debugOverlayEnabled,
                            adultSitesAllowed = adultSitesAllowed,
                            isDiagnosing = isDiagnosing,
                            diagnosticResults = diagnosticResults,
                            onOpenVault = { isVaultOpen = true }
                        )
                    }
                }
            }

            // Download Quality Selection Modal
            qualityTarget?.let { target ->
                DownloadQualityDialog(
                    target = target,
                    onDismiss = { viewModel.dismissQualityPicker() },
                    onConfirm = { qualityOption ->
                        viewModel.startDownloadWithQuality(target, qualityOption)
                    }
                )
            }

            // Universal Media Player Modal
            activePlayerItem?.let { item ->
                MediaPlayerDialog(
                    item = item,
                    isPlaying = isPlaying,
                    currentMs = playerCurrentMs,
                    durationMs = playerDurationMs,
                    speed = playbackSpeed,
                    volume = playerVolume,
                    brightness = playerBrightness,
                    equalizerPreset = equalizerPreset,
                    sleepTimerMinutes = sleepTimerMinutes,
                    isPipMode = isPipMode,
                    viewModel = viewModel,
                    onClose = { viewModel.closeMediaPlayer() }
                )
            }

            // Audio Cutter / Ringtone Maker Modal
            audioCutterItem?.let { item ->
                AudioCutterDialog(
                    item = item,
                    onDismiss = { viewModel.closeAudioCutter() },
                    onSave = { title, duration ->
                        viewModel.saveCutRingtone(title, duration)
                    }
                )
            }
        }
    }
}
