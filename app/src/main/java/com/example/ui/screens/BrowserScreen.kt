package com.example.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.DownloadListener
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NoAdultContent
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.BrowserTab
import com.example.ui.MainViewModel
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RoyalGold

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserScreen(
    viewModel: MainViewModel,
    browserTabs: List<BrowserTab>,
    currentTabIndex: Int,
    isDesktopMode: Boolean,
    isReadingMode: Boolean,
    adBlockerEnabled: Boolean,
    blockedAdsCount: Int,
    adultSitesAllowed: Boolean,
    modifier: Modifier = Modifier
) {
    val currentTab = browserTabs.getOrNull(currentTabIndex) ?: browserTabs.first()
    var urlInput by remember(currentTab.url) { mutableStateOf(currentTab.url) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var pageLoadProgress by remember { mutableFloatStateOf(0f) }
    var isLoading by remember { mutableStateOf(false) }

    var isTabsSheetOpen by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showSourceDialog by remember { mutableStateOf(false) }
    var isBookmarked by remember { mutableStateOf(false) }
    var isAdultBlocked by remember { mutableStateOf(false) }
    var blockedUrlString by remember { mutableStateOf("") }

    // Media sniffer detected stream state
    var detectedStreamUrl by remember { mutableStateOf<String?>(null) }
    var detectedStreamTitle by remember { mutableStateOf("Live Media Stream") }

    // Helper to check if URL is adult domain
    fun isAdultSite(url: String): Boolean {
        val lower = url.lowercase()
        val adultKeywords = listOf(
            "porn", "xxx", "xvideos", "pornhub", "xhamster", "redtube",
            "xnxx", "youporn", "brazzers", "adult", "sex", "erotic"
        )
        return adultKeywords.any { lower.contains(it) }
    }

    // Load URL into WebView
    fun navigateToUrl(raw: String) {
        val trimmed = raw.trim()
        val finalUrl = when {
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
            trimmed.contains(".") && !trimmed.contains(" ") -> "https://$trimmed"
            else -> "https://www.google.com/search?q=${trimmed.replace(" ", "+")}"
        }

        urlInput = finalUrl

        if (!adultSitesAllowed && isAdultSite(finalUrl)) {
            isAdultBlocked = true
            blockedUrlString = finalUrl
        } else {
            isAdultBlocked = false
            webViewInstance?.loadUrl(finalUrl)
        }
    }

    // Effect to reload when adultSitesAllowed changes or tab URL changes
    LaunchedEffect(currentTab.url, adultSitesAllowed) {
        if (!adultSitesAllowed && isAdultSite(currentTab.url)) {
            isAdultBlocked = true
            blockedUrlString = currentTab.url
        } else {
            isAdultBlocked = false
            webViewInstance?.loadUrl(currentTab.url)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("browser_screen")
    ) {
        // Browser Top Controls
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (webViewInstance?.canGoBack() == true) {
                                webViewInstance?.goBack()
                            } else {
                                viewModel.showUserMessage("No previous page in history")
                            }
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = {
                            if (webViewInstance?.canGoForward() == true) {
                                webViewInstance?.goForward()
                            } else {
                                viewModel.showUserMessage("No next page")
                            }
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Forward",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // URL Omnibox supporting direct internet browsing A to Z
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("browser_url_input"),
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                imageVector = if (currentTab.isIncognito) Icons.Default.VisibilityOff else Icons.Default.Lock,
                                contentDescription = "Security Status",
                                tint = if (currentTab.isIncognito) ElectricBlue else EmeraldGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (urlInput != currentTab.url && urlInput.isNotBlank()) {
                                    // GO Button
                                    Button(
                                        onClick = { navigateToUrl(urlInput) },
                                        colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("GO", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    IconButton(
                                        onClick = { webViewInstance?.reload() },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "Refresh",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalGold,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Tab Count Switcher
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .clickable { isTabsSheetOpen = true }
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${browserTabs.size}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // 3-dots Menu
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (isDesktopMode) "Mobile Site View" else "Desktop Site View") },
                                onClick = {
                                    viewModel.toggleDesktopMode()
                                    webViewInstance?.settings?.userAgentString = if (!isDesktopMode) {
                                        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                                    } else null
                                    webViewInstance?.reload()
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (isDesktopMode) Icons.Default.PhoneAndroid else Icons.Default.DesktopWindows,
                                        contentDescription = null
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (adultSitesAllowed) "Turn OFF Adult Sites" else "Turn ON Adult Sites (18+)") },
                                onClick = {
                                    viewModel.toggleAdultSitesAllowed()
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (adultSitesAllowed) Icons.Default.Security else Icons.Default.NoAdultContent,
                                        contentDescription = null,
                                        tint = if (adultSitesAllowed) EmeraldGreen else CrimsonRed
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("View Page Source (Inspector)") },
                                onClick = {
                                    showSourceDialog = true
                                    showMenu = false
                                },
                                leadingIcon = { Icon(imageVector = Icons.Default.Code, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text(if (isBookmarked) "Remove Bookmark" else "Bookmark Page") },
                                onClick = {
                                    isBookmarked = !isBookmarked
                                    viewModel.showUserMessage(if (isBookmarked) "Bookmarked $urlInput" else "Bookmark removed")
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = null,
                                        tint = RoyalGold
                                    )
                                }
                            )
                        }
                    }
                }

                // Loading progress indicator
                if (isLoading && pageLoadProgress in 0.01f..0.99f) {
                    LinearProgressIndicator(
                        progress = { pageLoadProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        color = RoyalGold,
                        trackColor = Color.Transparent
                    )
                }

                // Sub-bar with Ad-block counter & Adult Sites mode status
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = if (adBlockerEnabled) EmeraldGreen else Color.Gray,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (adBlockerEnabled) "Ad-Blocker: $blockedAdsCount blocked" else "Ad-Blocker Off",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (adBlockerEnabled) EmeraldGreen else Color.Gray
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = if (adultSitesAllowed) CrimsonRed.copy(alpha = 0.2f) else EmeraldGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.clickable { viewModel.toggleAdultSitesAllowed() }
                        ) {
                            Text(
                                text = if (adultSitesAllowed) "18+ UNLOCKED (TAP TO LOCK)" else "SAFESEARCH ON (TAP TO UNLOCK)",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (adultSitesAllowed) CrimsonRed else EmeraldGreen,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Live Internet View / Adult Blocked Gate
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isAdultBlocked) {
                // Parental Control / SafeSearch Gate with Instant ON/OFF button
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(CrimsonRed.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = null,
                            tint = CrimsonRed,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "18+ Adult Site Restricted",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Parental Control SafeSearch filter is currently ON for: \n$blockedUrlString\n\nYou can turn adult site access ON or OFF below:",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Allow Adult Sites (18+ Access)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Enable unrestricted A to Z browsing",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = adultSitesAllowed,
                                onCheckedChange = {
                                    viewModel.toggleAdultSitesAllowed()
                                    if (it) {
                                        isAdultBlocked = false
                                        webViewInstance?.loadUrl(blockedUrlString)
                                    }
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = RoyalGold)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            navigateToUrl("https://www.google.com")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text("Back to Safe Search (Google)", color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            } else {
                // Real Live Android WebView connected to the Internet
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        WebView(context).apply {
                            webViewInstance = this

                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                databaseEnabled = true
                                useWideViewPort = true
                                loadWithOverviewMode = true
                                setSupportZoom(true)
                                builtInZoomControls = true
                                displayZoomControls = false
                                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                cacheMode = WebSettings.LOAD_DEFAULT
                            }

                            // Allow third party cookies
                            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

                            // Media Sniffer download listener: any direct file or video stream tapped triggers download picker!
                            setDownloadListener(DownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                                val titleFromUrl = Uri.parse(url).lastPathSegment ?: "Web Download"
                                viewModel.openDownloadQualityPicker(
                                    title = titleFromUrl,
                                    url = url,
                                    siteDomain = Uri.parse(url).host ?: "web"
                                )
                            })

                            webChromeClient = object : WebChromeClient() {
                                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                    pageLoadProgress = newProgress / 100f
                                    isLoading = newProgress < 100
                                }

                                override fun onReceivedTitle(view: WebView?, title: String?) {
                                    if (!title.isNullOrBlank()) {
                                        detectedStreamTitle = title
                                    }
                                }
                            }

                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    super.onPageStarted(view, url, favicon)
                                    isLoading = true
                                    if (url != null) {
                                        urlInput = url
                                        // Adult filter check
                                        if (!adultSitesAllowed && isAdultSite(url)) {
                                            isAdultBlocked = true
                                            blockedUrlString = url
                                            view?.stopLoading()
                                        }
                                    }
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    isLoading = false
                                    if (url != null) {
                                        urlInput = url
                                    }
                                }

                                override fun shouldInterceptRequest(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): WebResourceResponse? {
                                    val reqUrl = request?.url?.toString() ?: ""
                                    val lower = reqUrl.lowercase()

                                    // Media sniffer: check for direct streaming files
                                    if (lower.endsWith(".mp4") || lower.endsWith(".m3u8") ||
                                        lower.endsWith(".mpd") || lower.endsWith(".mp3") ||
                                        lower.contains("/videoplayback") || lower.contains(".mp4?")
                                    ) {
                                        detectedStreamUrl = reqUrl
                                    }
                                    return super.shouldInterceptRequest(view, request)
                                }
                            }

                            loadUrl(currentTab.url)
                        }
                    },
                    update = { view ->
                        webViewInstance = view
                    }
                )
            }

            // Media Sniffer Floating Badge / Action Pill
            val snifferUrl = detectedStreamUrl ?: currentTab.detectedMediaUrl
            if (snifferUrl != null && !isAdultBlocked) {
                Surface(
                    color = CrimsonRed,
                    shape = RoundedCornerShape(24.dp),
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 70.dp)
                        .clickable {
                            viewModel.openDownloadQualityPicker(
                                title = detectedStreamTitle,
                                url = snifferUrl,
                                siteDomain = Uri.parse(urlInput).host ?: "web"
                            )
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = CrimsonRed,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Media Stream Detected!",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "1080p FHD • Tap to Download",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Tabs Switcher Modal
    if (isTabsSheetOpen) {
        AlertDialog(
            onDismissRequest = { isTabsSheetOpen = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Open Tabs (${browserTabs.size})",
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = {
                            viewModel.addNewBrowserTab()
                            isTabsSheetOpen = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Tab", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(browserTabs) { tab ->
                        val index = browserTabs.indexOf(tab)
                        val isSelected = index == currentTabIndex
                        Surface(
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    viewModel.selectBrowserTab(index)
                                    isTabsSheetOpen = false
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tab.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = tab.url,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                if (browserTabs.size > 1) {
                                    IconButton(
                                        onClick = { viewModel.closeBrowserTab(index) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Close Tab",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { isTabsSheetOpen = false }) {
                    Text("Done")
                }
            }
        )
    }

    // Source Code Inspector Modal
    if (showSourceDialog) {
        AlertDialog(
            onDismissRequest = { showSourceDialog = false },
            title = { Text("HTML Source Inspector", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = """
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>${currentTab.title}</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="canonical" href="$urlInput">
</head>
<body>
  <!-- Live Web Document -->
  <div id="content">
    <video src="${detectedStreamUrl ?: "streaming_manifest.m3u8"}"></video>
  </div>
</body>
</html>
                        """.trimIndent(),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showSourceDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
