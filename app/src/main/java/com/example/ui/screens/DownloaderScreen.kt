package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.DownloadItem
import com.example.ui.MainViewModel
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.RoyalGold

@Composable
fun DownloaderScreen(
    viewModel: MainViewModel,
    activeDownloads: List<DownloadItem>,
    completedDownloads: List<DownloadItem>,
    trashedDownloads: List<DownloadItem>,
    speedLimitEnabled: Boolean,
    speedLimitMb: Int,
    nightOwlActive: Boolean,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Downloading, 1: Completed
    var filterType by remember { mutableStateOf("ALL") } // "ALL", "VIDEO", "AUDIO"
    var showTrashModal by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("downloader_screen")
    ) {
        // Tab Row: Downloading vs Completed
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = RoyalGold,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = RoyalGold,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "DOWNLOADING (${activeDownloads.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "COMPLETED (${completedDownloads.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            )
        }

        // Subheader Controls: Speed Limiter, Night Owl toggle, Trash button
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Speed Limiter Pill
                Surface(
                    color = if (speedLimitEnabled) RoyalGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.clickable { viewModel.toggleSpeedLimiter() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = if (speedLimitEnabled) RoyalGold else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (speedLimitEnabled) "Limit: ${speedLimitMb}MB/s" else "Max Speed",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (speedLimitEnabled) RoyalGold else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Night Owl mode toggle
                Surface(
                    color = if (nightOwlActive) ElectricBlue.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.clickable { viewModel.toggleNightOwl() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.NightsStay,
                            contentDescription = null,
                            tint = if (nightOwlActive) ElectricBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (nightOwlActive) "Night Owl (12AM-6AM)" else "Normal Mode",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (nightOwlActive) ElectricBlue else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Trash Bin access
                IconButton(
                    onClick = { showTrashModal = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Trash Bin",
                        tint = if (trashedDownloads.isNotEmpty()) CrimsonRed else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Filter chips when on Completed tab
        if (selectedTab == 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterType == "ALL",
                    onClick = { filterType = "ALL" },
                    label = { Text("All (${completedDownloads.size})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = RoyalGold, selectedLabelColor = Color.Black)
                )
                FilterChip(
                    selected = filterType == "VIDEO",
                    onClick = { filterType = "VIDEO" },
                    label = { Text("Videos (${completedDownloads.count { it.mediaType == "VIDEO" }})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CrimsonRed, selectedLabelColor = Color.White)
                )
                FilterChip(
                    selected = filterType == "AUDIO",
                    onClick = { filterType = "AUDIO" },
                    label = { Text("Music (${completedDownloads.count { it.mediaType == "AUDIO" }})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EmeraldGreen, selectedLabelColor = Color.White)
                )
            }
        }

        // List Content
        if (selectedTab == 0) {
            // Active Downloads
            if (activeDownloads.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No active downloads right now",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Paste a link or pick from trending feed on Home to download!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(activeDownloads, key = { it.id }) { item ->
                        ActiveDownloadCard(
                            item = item,
                            onPause = { viewModel.pauseDownload(item.id) },
                            onResume = { viewModel.resumeDownload(item.id) },
                            onCancel = { viewModel.cancelDownload(item.id) }
                        )
                    }
                }
            }
        } else {
            // Completed Downloads
            val filtered = completedDownloads.filter {
                if (filterType == "ALL") true else it.mediaType == filterType
            }

            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No completed files in this category",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 96.dp, start = 16.dp, end = 16.dp, top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filtered, key = { it.id }) { item ->
                        CompletedMediaCard(
                            item = item,
                            onPlay = { viewModel.openMediaPlayer(item) },
                            onExtractAudio = { viewModel.extractAudio(item) },
                            onCutAudio = { viewModel.openAudioCutter(item) },
                            onMoveToVault = { viewModel.moveItemToVault(item) },
                            onMoveToTrash = { viewModel.moveToTrash(item.id) },
                            onShare = { viewModel.showUserMessage("Shared ${item.title} to WhatsApp/Telegram") }
                        )
                    }
                }
            }
        }
    }

    // Trash Bin Modal
    if (showTrashModal) {
        AlertDialog(
            onDismissRequest = { showTrashModal = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Trash Bin (${trashedDownloads.size})", fontWeight = FontWeight.Bold)
                    if (trashedDownloads.isNotEmpty()) {
                        TextButton(
                            onClick = {
                                viewModel.emptyTrash()
                                showTrashModal = false
                            }
                        ) {
                            Text("Empty All", color = CrimsonRed, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            text = {
                if (trashedDownloads.isEmpty()) {
                    Text("Trash bin is empty.")
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(trashedDownloads) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = item.formattedSize,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Row {
                                    IconButton(onClick = { viewModel.restoreFromTrash(item.id) }) {
                                        Icon(Icons.Default.RestoreFromTrash, contentDescription = "Restore", tint = EmeraldGreen)
                                    }
                                    IconButton(onClick = { viewModel.permanentlyDelete(item.id) }) {
                                        Icon(Icons.Default.DeleteSweep, contentDescription = "Delete", tint = CrimsonRed)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTrashModal = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun ActiveDownloadCard(
    item: DownloadItem,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Thumbnail or Type Icon
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.thumbnailUrl.isNotBlank()) {
                        AsyncImage(
                            model = item.thumbnailUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = if (item.mediaType == "AUDIO") Icons.Default.MusicNote else Icons.Default.Videocam,
                            contentDescription = null,
                            tint = RoyalGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${item.quality} • ${item.format} • ${item.threadCount} Threads",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Pause / Resume / Cancel Controls
                IconButton(
                    onClick = {
                        if (item.status == "DOWNLOADING") onPause() else onResume()
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (item.status == "DOWNLOADING") Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Toggle Pause",
                        tint = RoyalGold
                    )
                }
                IconButton(
                    onClick = onCancel,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel",
                        tint = CrimsonRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { item.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = RoyalGold,
                trackColor = MaterialTheme.colorScheme.surface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Thread Chunk visualization (8 chunk segments)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                for (i in 0 until item.threadCount) {
                    val isChunkDone = item.progress >= ((i + 1).toFloat() / item.threadCount)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (isChunkDone) EmeraldGreen else if (item.status == "DOWNLOADING") RoyalGold else Color.Gray)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stats row (Downloaded vs Total, Speed, ETA)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val downloadedMb = item.downloadedBytes / (1024 * 1024)
                val totalMb = item.totalBytes / (1024 * 1024)
                Text(
                    text = "${downloadedMb}MB / ${totalMb}MB (${(item.progress * 100).toInt()}%)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (item.status == "DOWNLOADING") "⚡ ${item.formattedSpeed}" else "PAUSED",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (item.status == "DOWNLOADING") EmeraldGreen else NeonOrange
                )
            }
        }
    }
}

@Composable
fun CompletedMediaCard(
    item: DownloadItem,
    onPlay: () -> Unit,
    onExtractAudio: () -> Unit,
    onCutAudio: () -> Unit,
    onMoveToVault: () -> Unit,
    onMoveToTrash: () -> Unit,
    onShare: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlay() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail with Play overlay icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                if (item.thumbnailUrl.isNotBlank()) {
                    AsyncImage(
                        model = item.thumbnailUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.65f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = RoyalGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (item.mediaType == "AUDIO") EmeraldGreen.copy(alpha = 0.2f) else CrimsonRed.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = item.format,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (item.mediaType == "AUDIO") EmeraldGreen else CrimsonRed,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${item.quality} • ${item.formattedSize}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Quick Play Button
            IconButton(
                onClick = onPlay,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = RoyalGold
                )
            }

            // More Options Dropdown
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    if (item.mediaType == "VIDEO") {
                        DropdownMenuItem(
                            text = { Text("Extract MP3 Audio") },
                            onClick = {
                                onExtractAudio()
                                showMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.MusicNote, contentDescription = null, tint = EmeraldGreen) }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Ringtone Cutter") },
                        onClick = {
                            onCutAudio()
                            showMenu = false
                        },
                        leadingIcon = { Icon(Icons.Default.Audiotrack, contentDescription = null, tint = RoyalGold) }
                    )
                    DropdownMenuItem(
                        text = { Text("Move to Private Vault") },
                        onClick = {
                            onMoveToVault()
                            showMenu = false
                        },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CrimsonRed) }
                    )
                    DropdownMenuItem(
                        text = { Text("Share Media") },
                        onClick = {
                            onShare()
                            showMenu = false
                        },
                        leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete (Move to Trash)") },
                        onClick = {
                            onMoveToTrash()
                            showMenu = false
                        },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = CrimsonRed) }
                    )
                }
            }
        }
    }
}
