package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.BookmarkItem
import com.example.data.model.DownloadItem
import com.example.data.model.SiteShortcut
import com.example.data.model.VaultItem

@Database(
    entities = [
        DownloadItem::class,
        SiteShortcut::class,
        VaultItem::class,
        BookmarkItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class VidKingDatabase : RoomDatabase() {
    abstract fun downloadDao(): DownloadDao
    abstract fun siteShortcutDao(): SiteShortcutDao
    abstract fun vaultDao(): VaultDao
    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        @Volatile
        private var INSTANCE: VidKingDatabase? = null

        fun getDatabase(context: Context): VidKingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VidKingDatabase::class.java,
                    "vidking_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
