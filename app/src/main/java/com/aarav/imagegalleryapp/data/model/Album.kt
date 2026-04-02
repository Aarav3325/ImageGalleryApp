package com.aarav.imagegalleryapp.data.model

import android.net.Uri

data class Album(
    val bucketId: String,
    val name: String,
    val images: List<ImageItem>,
    val thumbnail: Uri,
    val imageCount: Int
)