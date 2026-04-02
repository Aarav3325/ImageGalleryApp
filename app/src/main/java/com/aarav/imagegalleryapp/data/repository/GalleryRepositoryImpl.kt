package com.aarav.imagegalleryapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aarav.imagegalleryapp.data.datasource.MediaStoreDataSource
import com.aarav.imagegalleryapp.data.model.ImageItem
import com.aarav.imagegalleryapp.data.paging.GalleryPagingSource
import com.aarav.imagegalleryapp.domain.GalleryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GalleryRepositoryImpl
@Inject constructor(
    val mediaStoreDataSource: MediaStoreDataSource
) : GalleryRepository {

    override fun getAllImages(): Flow<PagingData<ImageItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 50,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                GalleryPagingSource(
                    mediaStoreDataSource
                )
            }
        ).flow
    }

}