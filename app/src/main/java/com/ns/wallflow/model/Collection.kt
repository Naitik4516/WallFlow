package com.ns.wallflow.model

import kotlinx.serialization.Serializable

@Serializable
data class Collection(
    val id: Int,
    val name: String,
    val totalWallpapers: Int,
    val coverImagePath: String
)
