package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BookmarkItem
import com.example.data.model.DownloadItem
import com.example.data.model.SiteShortcut
import com.example.data.model.VaultItem
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    @Query("SELECT * FROM downloads WHERE isTrashed = 0 ORDER BY createdAt DESC")
    fun getAllDownloads(): Flow<List<DownloadItem>>

    @Query("SELECT * FROM downloads WHERE isTrashed = 0 AND status IN ('DOWNLOADING', 'PAUSED', 'QUEUED') ORDER BY createdAt DESC")
    fun getActiveDownloads(): Flow<List<DownloadItem>>

    @Query("SELECT * FROM downloads WHERE isTrashed = 0 AND status = 'COMPLETED' ORDER BY createdAt DESC")
    fun getCompletedDownloads(): Flow<List<DownloadItem>>

    @Query("SELECT * FROM downloads WHERE isTrashed = 1 ORDER BY createdAt DESC")
    fun getTrashedDownloads(): Flow<List<DownloadItem>>

    @Query("SELECT * FROM downloads WHERE id = :id LIMIT 1")
    suspend fun getDownloadById(id: String): DownloadItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(download: DownloadItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(downloads: List<DownloadItem>)

    @Update
    suspend fun update(download: DownloadItem)

    @Query("UPDATE downloads SET status = :status, downloadedBytes = :downloadedBytes, speedBps = :speedBps WHERE id = :id")
    suspend fun updateProgress(id: String, status: String, downloadedBytes: Long, speedBps: Long)

    @Query("UPDATE downloads SET isTrashed = 1 WHERE id = :id")
    suspend fun moveToTrash(id: String)

    @Query("UPDATE downloads SET isTrashed = 0 WHERE id = :id")
    suspend fun restoreFromTrash(id: String)

    @Query("DELETE FROM downloads WHERE id = :id")
    suspend fun permanentlyDelete(id: String)

    @Query("DELETE FROM downloads WHERE isTrashed = 1")
    suspend fun emptyTrash()
}

@Dao
interface SiteShortcutDao {
    @Query("SELECT * FROM site_shortcuts ORDER BY sortOrder ASC")
    fun getAllShortcuts(): Flow<List<SiteShortcut>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(shortcut: SiteShortcut)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(shortcuts: List<SiteShortcut>)

    @Query("DELETE FROM site_shortcuts WHERE id = :id")
    suspend fun delete(id: String)
}

@Dao
interface VaultDao {
    @Query("SELECT * FROM vault_items ORDER BY lockedAt DESC")
    fun getAllVaultItems(): Flow<List<VaultItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: VaultItem)

    @Query("DELETE FROM vault_items WHERE id = :id")
    suspend fun delete(id: String)
}

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: BookmarkItem)

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun delete(id: String)
}
