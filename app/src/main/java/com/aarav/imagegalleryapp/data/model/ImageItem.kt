package com.aarav.imagegalleryapp.data.model

import android.net.Uri

data class ImageItem(
    val id: Long,
    val displayName: String,
    val uri: Uri,
    val dateAdded: Long,
    val bucketId: String,
    val bucketName: String
)