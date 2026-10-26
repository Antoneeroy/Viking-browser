package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.SiteShortcut
import com.example.ui.MainViewModel
import com.example.ui.NavDestination
import com.example.ui.components.OmniSearchBar
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight

data class TrendingCard(
    val id: String,
    val title: String,
    val channel: String,
    val views: String,
    val duration: String,
    val quality: String,
    val imageUrl: String,
    val sourceUrl: String,
    val domain: String
)

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    siteShortcuts: List<SiteShortcut>,
    searchQuery: String,
    vipCoins: Int,
    adultSitesAllowed: Boolean,
    onNavigateToSites: (String) -> Unit,
    onNavigateToFiles: () -> Unit,
    onOpenVault: () -> Unit,
    modifier: Modifier = Modifier
) {
    val trendingVideos = listOf(
        TrendingCard(
            id = "t1",
            title = "Unreal Engine 5.5 Next-Gen Photorealism Tech Demo (120FPS)",
            channel = "Graphics Foundry",
            views = "4.2M views",
            duration = "14:28",
            quality = "4K 60fps",
            imageUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=600&auto=format&fit=crop",
            sourceUrl = "https://youtube.com/watch?v=unreal_engine_5_demo",
            domain = "youtube.com"
        ),
        TrendingCard(
            id = "t2",
            title = "Deep Focus Synthwave & Retrowave Cyber Mix 2026",
            channel = "Neon Horizon Records",
            views = "1.8M views",
            duration = "1:45:00",
            quality = "Lossless HQ",
            imageUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=600&auto=format&fit=crop",
            sourceUrl = "https://soundcloud.com/neon-records/synthwave-focus",
            domain = "soundcloud.com"
        ),
        TrendingCard(
            id = "t3",
            title = "Hyperlapse Tokyo Shinjuku Rainy Neon Crosswalk Reel",
            channel = "Tokyo Streets",
            views = "890K views",
            duration = "0:45",
            quality = "1080p FHD",
            imageUrl = "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?w=600&auto=format&fit=crop",
            sourceUrl = "https://instagram.com/reel/tokyo_rain",
            domain = "instagram.com"
        ),
        TrendingCard(
            id = "t4",
            title = "Formula 1 2026 Engine Sound & Aerodynamics Breakdown",
            channel = "Motorsport Velocity",
            views = "950K views",
            duration = "22:10",
            quality = "1080p 60fps",
            imageUrl = "https://images.unsplash.com/photo-1568605117036-5fe5e7bab0b7?w=600&auto=format&fit=crop",
            sourceUrl = "https://youtube.com/watch?v=f1_2026_breakdown",
            domain = "youtube.com"
        )
    )

    var showAtoZDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Search & Omnibox
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                OmniSearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.setSearchQuery(it) },
                    onSearch = { query ->
                        val trimmed = query.trim()
                        val finalUrl = when {
                            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
                            trimmed.contains(".") && !trimmed.contains(" ") -> "https://$trimmed"
                            else -> "https://www.google.com/search?q=${trimmed.replace(" ", "+")}"
                        }
                        viewModel.openUrlInBrowser(finalUrl, query)
                    },
                    onPasteQuickDownload = {
                        viewModel.openDownloadQualityPicker(
                            title = "Clipboard Detected: Top Trending Reel (Instagram)",
                            url = "https://instagram.com/reel/trending_clip",
                            siteDomain = "instagram.com"
                        )
                    }
                )
            }
        }

        // VIP High Speed Banner
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(RoyalGold, NeonOrange))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "VIP 16-Thread Turbo Boost",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Daily streak bonus ready • $vipCoins coins available",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.claimDailyCheckIn() },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "CLAIM",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Quick Utility Actions
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionChip(
                    icon = Icons.Default.Whatshot,
                    label = "WA Status",
                    color = EmeraldGreen,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.openUrlInBrowser("https://web.whatsapp.com", "WhatsApp Status Saver")
                    }
                )
                QuickActionChip(
                    icon = Icons.Default.Lock,
                    label = "Private Vault",
                    color = CrimsonRed,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenVault
                )
                QuickActionChip(
                    icon = Icons.Default.MusicNote,
                    label = "MP3 Extractor",
                    color = RoyalGold,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToFiles
                )
                QuickActionChip(
                    icon = Icons.Default.Headphones,
                    label = "Ringtone Tool",
                    color = ElectricBlue,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToFiles
                )
            }
        }

        // Site Shortcuts Section
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "1000+ SUPPORTED PLATFORMS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = RoyalGold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "See All (A-Z) & 18+",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalGold,
                        modifier = Modifier.clickable { showAtoZDialog = true }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Grid of shortcuts
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(175.dp),
                    userScrollEnabled = false,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(siteShortcuts.take(10)) { shortcut ->
                        ShortcutItem(
                            shortcut = shortcut,
                            onClick = { onNavigateToSites(shortcut.url) }
                        )
                    }
                }
            }
        }

        // Trending Media Section
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Whatshot,
                            contentDescription = null,
                            tint = CrimsonRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Trending Now",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Auto-Sniff Active",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = EmeraldGreen
                    )
                }
            }
        }

        // Video list cards
        items(trendingVideos) { video ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable {
                        viewModel.openDownloadQualityPicker(
                            title = video.title,
                            url = video.sourceUrl,
                            thumbnailUrl = video.imageUrl,
                            siteDomain = video.domain
                        )
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column {
                    // Video Thumbnail
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(190.dp)
                    ) {
                        AsyncImage(
                            model = video.imageUrl,
                            contentDescription = video.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Quality Badge
                        Surface(
                            color = Color.Black.copy(alpha = 0.75f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = video.quality,
                                color = RoyalGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }

                        // Duration badge
                        Surface(
                            color = Color.Black.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = video.duration,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Card Info Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = video.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${video.channel} • ${video.views} • ${video.domain}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Download Action Circle
                        Button(
                            onClick = {
                                viewModel.openDownloadQualityPicker(
                                    title = video.title,
                                    url = video.sourceUrl,
                                    thumbnailUrl = video.imageUrl,
                                    siteDomain = video.domain
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                            shape = CircleShape,
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download Video",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAtoZDialog) {
        AtoZSitesDialog(
            adultSitesAllowed = adultSitesAllowed,
            onToggleAdult = { viewModel.toggleAdultSitesAllowed() },
            onSelectSite = { url, title ->
                showAtoZDialog = false
                viewModel.openUrlInBrowser(url, title)
            },
            onDismiss = { showAtoZDialog = false }
        )
    }
}

@Composable
fun QuickActionChip(
    icon: ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ShortcutItem(
    shortcut: SiteShortcut,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(2.dp)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(getSiteColor(shortcut.iconKey)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = shortcut.name.take(2).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
            }
            if (shortcut.badge != null) {
                Surface(
                    color = CrimsonRed,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(start = 2.dp)
                ) {
                    Text(
                        text = shortcut.badge,
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = shortcut.name,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

fun getSiteColor(key: String): Color {
    return when (key.lowercase()) {
        "youtube" -> Color(0xFFFF0000)
        "instagram" -> Color(0xFFC13584)
        "whatsapp" -> Color(0xFF25D366)
        "facebook" -> Color(0xFF1877F2)
        "tiktok" -> Color(0xFF000000)
        "twitter" -> Color(0xFF1DA1F2)
        "pinterest" -> Color(0xFFBD081C)
        "soundcloud" -> Color(0xFFFF5500)
        "dailymotion" -> Color(0xFF0066DC)
        "vimeo" -> Color(0xFF1AB7EA)
        "twitch" -> Color(0xFF9146FF)
        "reddit" -> Color(0xFFFF4500)
        else -> Color(0xFF4A4458)
    }
}

data class AtoZSiteEntry(
    val name: String,
    val url: String,
    val category: String,
    val letter: String,
    val isAdult: Boolean = false
)

@Composable
fun AtoZSitesDialog(
    adultSitesAllowed: Boolean,
    onToggleAdult: () -> Unit,
    onSelectSite: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: All A-Z Sites, 1: 18+ Adult Zone
    var siteSearch by remember { mutableStateOf("") }
    var selectedLetter by remember { mutableStateOf("ALL") }

    val allSitesList = remember {
        listOf(
            AtoZSiteEntry("Archive.org (Wayback)", "https://archive.org", "Library", "A"),
            AtoZSiteEntry("Animepahe", "https://animepahe.ru", "Anime", "A"),
            AtoZSiteEntry("Audiomack", "https://audiomack.com", "Music", "A"),
            AtoZSiteEntry("Amazon Prime Video", "https://primevideo.com", "Video", "A"),
            AtoZSiteEntry("BBC News", "https://www.bbc.com", "News", "B"),
            AtoZSiteEntry("Bilibili", "https://www.bilibili.tv", "Video", "B"),
            AtoZSiteEntry("Bandcamp", "https://bandcamp.com", "Music", "B"),
            AtoZSiteEntry("Bing Search", "https://www.bing.com", "Search", "B"),
            AtoZSiteEntry("CNN World", "https://www.cnn.com", "News", "C"),
            AtoZSiteEntry("Crunchyroll", "https://www.crunchyroll.com", "Anime", "C"),
            AtoZSiteEntry("Canva", "https://www.canva.com", "Tools", "C"),
            AtoZSiteEntry("Coursera", "https://www.coursera.org", "Education", "C"),
            AtoZSiteEntry("DailyMotion", "https://www.dailymotion.com", "Video", "D"),
            AtoZSiteEntry("DuckDuckGo", "https://duckduckgo.com", "Search", "D"),
            AtoZSiteEntry("DeviantArt", "https://www.deviantart.com", "Art", "D"),
            AtoZSiteEntry("Discord Web", "https://discord.com", "Social", "D"),
            AtoZSiteEntry("ESPN Sports", "https://www.espn.com", "Sports", "E"),
            AtoZSiteEntry("eBay", "https://www.ebay.com", "Shop", "E"),
            AtoZSiteEntry("Engadget", "https://www.engadget.com", "Tech", "E"),
            AtoZSiteEntry("Facebook", "https://m.facebook.com", "Social", "F"),
            AtoZSiteEntry("Flickr", "https://www.flickr.com", "Photos", "F"),
            AtoZSiteEntry("Fast.com (Speed Test)", "https://fast.com", "Tools", "F"),
            AtoZSiteEntry("Google", "https://www.google.com", "Search", "G"),
            AtoZSiteEntry("GitHub", "https://github.com", "Dev", "G"),
            AtoZSiteEntry("Giphy", "https://giphy.com", "GIFs", "G"),
            AtoZSiteEntry("Hotstar", "https://www.hotstar.com", "Video", "H"),
            AtoZSiteEntry("Hacker News", "https://news.ycombinator.com", "Tech", "H"),
            AtoZSiteEntry("Instagram", "https://instagram.com", "Social", "I"),
            AtoZSiteEntry("IMDb Movies", "https://www.imdb.com", "Cinema", "I"),
            AtoZSiteEntry("Imgur", "https://imgur.com", "Images", "I"),
            AtoZSiteEntry("JioCinema", "https://www.jiocinema.com", "Video", "J"),
            AtoZSiteEntry("JustWatch", "https://www.justwatch.com", "Cinema", "J"),
            AtoZSiteEntry("Khan Academy", "https://www.khanacademy.org", "Education", "K"),
            AtoZSiteEntry("Kickstarter", "https://www.kickstarter.com", "Crowd", "K"),
            AtoZSiteEntry("LinkedIn", "https://www.linkedin.com", "Jobs", "L"),
            AtoZSiteEntry("Last.fm", "https://www.last.fm", "Music", "L"),
            AtoZSiteEntry("Medium", "https://medium.com", "Articles", "M"),
            AtoZSiteEntry("Mixcloud", "https://www.mixcloud.com", "DJs", "M"),
            AtoZSiteEntry("MyAnimeList", "https://myanimelist.net", "Anime", "M"),
            AtoZSiteEntry("Netflix", "https://www.netflix.com", "Video", "N"),
            AtoZSiteEntry("National Geographic", "https://www.nationalgeographic.com", "Nature", "N"),
            AtoZSiteEntry("OpenSubtitles", "https://www.opensubtitles.org", "Subtitles", "O"),
            AtoZSiteEntry("Outlook Web", "https://outlook.live.com", "Email", "O"),
            AtoZSiteEntry("Pinterest", "https://pinterest.com", "Social", "P"),
            AtoZSiteEntry("Pixabay HD", "https://pixabay.com", "Free Media", "P"),
            AtoZSiteEntry("Quora Q&A", "https://www.quora.com", "Community", "Q"),
            AtoZSiteEntry("Reddit", "https://reddit.com", "Social", "R"),
            AtoZSiteEntry("Reuters", "https://www.reuters.com", "News", "R"),
            AtoZSiteEntry("Rumble", "https://rumble.com", "Video", "R"),
            AtoZSiteEntry("SoundCloud", "https://soundcloud.com", "Music", "S"),
            AtoZSiteEntry("Spotify Web", "https://open.spotify.com", "Music", "S"),
            AtoZSiteEntry("Steam Community", "https://steamcommunity.com", "Gaming", "S"),
            AtoZSiteEntry("TikTok", "https://www.tiktok.com", "Video", "T"),
            AtoZSiteEntry("Twitter / X", "https://x.com", "Social", "T"),
            AtoZSiteEntry("Twitch Live", "https://twitch.tv", "Gaming", "T"),
            AtoZSiteEntry("Telegram Web", "https://web.telegram.org", "Chat", "T"),
            AtoZSiteEntry("Unsplash 4K", "https://unsplash.com", "Photos", "U"),
            AtoZSiteEntry("Udemy", "https://www.udemy.com", "Courses", "U"),
            AtoZSiteEntry("Vimeo", "https://vimeo.com", "Video", "V"),
            AtoZSiteEntry("VKontakte", "https://vk.com", "Social", "V"),
            AtoZSiteEntry("WhatsApp Web", "https://web.whatsapp.com", "Tools", "W"),
            AtoZSiteEntry("Wikipedia", "https://wikipedia.org", "Encyclopedia", "W"),
            AtoZSiteEntry("Wayback Machine", "https://web.archive.org", "Archive", "W"),
            AtoZSiteEntry("Xbox Cloud", "https://www.xbox.com/play", "Games", "X"),
            AtoZSiteEntry("YouTube", "https://m.youtube.com", "Video", "Y"),
            AtoZSiteEntry("Yahoo News", "https://www.yahoo.com", "Portal", "Y"),
            AtoZSiteEntry("Zee5", "https://www.zee5.com", "Video", "Z"),
            AtoZSiteEntry("Zoom Web", "https://zoom.us", "Video", "Z")
        )
    }

    val adultSitesList = remember {
        listOf(
            AtoZSiteEntry("XVideos Portal", "https://www.xvideos.com", "18+ Adult", "X", true),
            AtoZSiteEntry("Pornhub Video", "https://www.pornhub.com", "18+ Adult", "P", true),
            AtoZSiteEntry("XHamster Stream", "https://www.xhamster.com", "18+ Adult", "X", true),
            AtoZSiteEntry("RedTube", "https://www.redtube.com", "18+ Adult", "R", true),
            AtoZSiteEntry("XNXX Clips", "https://www.xnxx.com", "18+ Adult", "X", true),
            AtoZSiteEntry("YouPorn HD", "https://www.youporn.com", "18+ Adult", "Y", true)
        )
    }

    val letters = listOf("ALL") + ('A'..'Z').map { it.toString() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "1000+ Sites Directory (A to Z)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                Text(
                    text = "Browse any site on the live internet with one tap",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Category Tabs (A-Z Sites vs 18+ Adult Zone)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { selectedTab = 0 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTab == 0) RoyalGold else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "A-Z All Sites (${allSitesList.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 0) Color.Black else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Button(
                        onClick = { selectedTab = 1 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTab == 1) CrimsonRed else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (adultSitesAllowed) "18+ Adult (ON)" else "18+ Adult (OFF)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 1) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (selectedTab == 1) {
                    // 18+ Adult Zone with ON / OFF Toggle Button
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (adultSitesAllowed) Icons.Default.Security else Icons.Default.Block,
                                        contentDescription = null,
                                        tint = if (adultSitesAllowed) EmeraldGreen else CrimsonRed,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Adult & 18+ Sites Mode",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = if (adultSitesAllowed) "STATUS: ON (UNRESTRICTED)" else "STATUS: OFF (SAFE FILTER ACTIVE)",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (adultSitesAllowed) EmeraldGreen else CrimsonRed
                                        )
                                    }
                                }

                                Switch(
                                    checked = adultSitesAllowed,
                                    onCheckedChange = { onToggleAdult() },
                                    colors = SwitchDefaults.colors(checkedThumbColor = RoyalGold)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!adultSitesAllowed) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = CrimsonRed, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "18+ Adult Sites are Currently Disabled",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Parental Control SafeSearch is protecting web browsing. Click the ON button above to enable adult sites access.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = onToggleAdult,
                                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("TURN ON ADULT SITES", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }
                    } else {
                        // Unlocked Adult Platforms
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(adultSitesList) { site ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onSelectSite(site.url, site.name) }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(text = site.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text(text = site.url, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Surface(
                                            color = CrimsonRed.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "18+ LIVE",
                                                color = CrimsonRed,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // A to Z All Sites List
                    OutlinedTextField(
                        value = siteSearch,
                        onValueChange = { siteSearch = it },
                        placeholder = { Text("Search 1000+ sites (e.g. YouTube, BBC, Reddit...)", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = RoyalGold, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Alphabet horizontal jump row
                    ScrollableTabRow(
                        selectedTabIndex = letters.indexOf(selectedLetter).coerceAtLeast(0),
                        edgePadding = 4.dp,
                        containerColor = Color.Transparent,
                        divider = {},
                        indicator = {}
                    ) {
                        letters.forEach { l ->
                            val isSel = selectedLetter == l
                            Surface(
                                color = if (isSel) RoyalGold else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .padding(horizontal = 2.dp)
                                    .clickable { selectedLetter = l }
                            ) {
                                Text(
                                    text = l,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.Black else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val filteredSites = allSitesList.filter { site ->
                        val matchesLetter = if (selectedLetter == "ALL") true else site.letter == selectedLetter
                        val matchesSearch = if (siteSearch.isBlank()) true else site.name.contains(siteSearch, ignoreCase = true) || site.url.contains(siteSearch, ignoreCase = true)
                        matchesLetter && matchesSearch
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(filteredSites) { site ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectSite(site.url, site.name) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(30.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(RoyalGold.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = site.letter, fontWeight = FontWeight.Bold, color = RoyalGold, fontSize = 12.sp)
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(text = site.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text(text = site.url, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = site.category,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = RoyalGold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

