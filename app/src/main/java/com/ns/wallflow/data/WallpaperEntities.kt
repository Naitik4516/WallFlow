package com.ns.wallflow.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.ns.wallflow.model.Wallpaper
import com.ns.wallflow.model.WallpaperBrightness
import com.ns.wallflow.model.WallpaperTimePhase

@Entity(tableName = "collections")
data class CollectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "wallpapers",
    foreignKeys = [
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["collectionId"]), Index(value = ["timePhaseTag"]), Index(value = ["brightnessTag"])]
)
data class WallpaperEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val filePath: String,
    val timePhaseTag: String, // "MORNING", "AFTERNOON", "EVENING", "NIGHT"
    val brightnessTag: String, // "LIGHT", "DARK"
    val collectionId: Int? = null,
    val isFavourite: Boolean = false,
    val originalUri: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class CollectionWithWallpapers(
    @Embedded val collection: CollectionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "collectionId"
    )
    val wallpapers: List<WallpaperEntity>
)

fun WallpaperEntity.toWallpaper(collectionName: String? = null): Wallpaper {
    return Wallpaper(
        id = this.id,
        filePath = this.filePath,
        collection = collectionName,
        addedAt = this.createdAt,
        timePhase = WallpaperTimePhase.valueOf(this.timePhaseTag),
        brightness = WallpaperBrightness.valueOf(this.brightnessTag),
        isFavourite = this.isFavourite,
        originalUri = this.originalUri
    )
}
