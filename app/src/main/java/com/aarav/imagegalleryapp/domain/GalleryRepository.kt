package com.aarav.imagegalleryapp.domain

import android.content.Context
import com.aarav.imagegalleryapp.data.model.Album
import com.aarav.imagegalleryapp.data.model.ImageItem
import com.aarav.imagegalleryapp.utils.Resource

interface GalleryRepository {
    suspend fun getAllImages(context: Context): Resource<List<ImageItem>>

    suspend fun getAllAlbums(context: Context): Resource<List<Album>>
}