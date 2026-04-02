package com.aarav.imagegalleryapp.domain

import androidx.paging.PagingData
import com.aarav.imagegalleryapp.data.model.ImageItem
import kotlinx.coroutines.flow.Flow

interface GalleryRepository {
//    suspend fun getAllImages(context: Context): Resource<List<ImageItem>>
//
//    suspend fun getAllAlbums(context: Context): Resource<List<Album>>

    fun getAllImages(): Flow<PagingData<ImageItem>>

}