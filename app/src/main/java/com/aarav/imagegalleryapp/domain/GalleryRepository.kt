package com.aarav.imagegalleryapp.domain

import android.content.Context
import androidx.paging.PagingData
import com.aarav.imagegalleryapp.data.model.Album
import com.aarav.imagegalleryapp.data.model.ImageItem
import com.aarav.imagegalleryapp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface GalleryRepository {
    suspend fun getAllImages(context: Context): List<ImageItem>

    suspend fun getAllAlbums(context: Context): Resource<List<Album>>

    fun getAllImages(): Flow<PagingData<ImageItem>>

    fun getAlbumImages(bucketId: String): Flow<PagingData<ImageItem>>

}