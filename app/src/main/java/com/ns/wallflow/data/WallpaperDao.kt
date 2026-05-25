package com.ns.wallflow.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface WallpaperDao {

    @Insert
    suspend fun insertWallpaper(wallpaper: WallpaperEntity): Long

    @Insert
    suspend fun insertAllWallpapers(wallpapers: List<WallpaperEntity>)

    @Query("SELECT * FROM wallpapers ORDER BY createdAt")
    fun getAllWallpapersFlow(): kotlinx.coroutines.flow.Flow<List<WallpaperEntity>>

    @Query("SELECT * FROM wallpapers ORDER BY createdAt")
    suspend fun getAllWallpapers(): List<WallpaperEntity>


    @Query("SELECT * FROM wallpapers WHERE brightnessTag = :brightness ORDER BY createdAt")
    fun getWallpapersByBrightnessFlow(brightness: String): kotlinx.coroutines.flow.Flow<List<WallpaperEntity>>

    @Query("SELECT * FROM wallpapers WHERE timePhaseTag = :phase ORDER BY createdAt")
    fun getWallpapersByTimePhaseFlow(phase: String): kotlinx.coroutines.flow.Flow<List<WallpaperEntity>>

    @Query("SELECT * FROM wallpapers WHERE brightnessTag = :brightness ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWallpaperByBrightness(brightness: String): WallpaperEntity?

    @Query("SELECT * FROM wallpapers WHERE timePhaseTag = :phase ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWallpaperByTimePhase(phase: String): WallpaperEntity?

    @Query("SELECT * FROM wallpapers WHERE collectionId = :collectionId AND timePhaseTag = :phase ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWallpaperFromCollection(collectionId: Int, phase: String): WallpaperEntity?

    @Query("DELETE FROM wallpapers WHERE id = :wallpaperId")
    suspend fun deleteWallpaperById(wallpaperId: Int)
}

@Dao
interface CollectionDao {

    @Insert
    suspend fun createCollection(collection: CollectionEntity): Long

    @Query("SELECT * FROM collections ORDER BY createdAt")
    suspend fun getAllCollections(): List<CollectionEntity>

    @Query("UPDATE wallpapers SET collectionId = :collectionId WHERE id = :wallpaperId")
    suspend fun assignWallpaperToCollection(wallpaperId: Int, collectionId: Int?)

    @Transaction
    @Query("SELECT * FROM collections WHERE id = :collectionId")
    suspend fun getCollectionWithWallpapers(collectionId: Int): CollectionWithWallpapers?

    @Query("DELETE FROM collections WHERE id = :collectionId")
    suspend fun deleteCollectionById(collectionId: Int)
}