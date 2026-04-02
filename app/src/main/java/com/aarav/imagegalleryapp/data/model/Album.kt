package com.aarav.imagegalleryapp.data.model

import android.net.Uri

data class Album(
    val name: String,
    val thumbnail: Uri,
    val imageCount: Int,
    val images: List<ImageItem>
)