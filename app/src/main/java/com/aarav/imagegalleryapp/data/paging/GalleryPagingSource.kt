package com.aarav.imagegalleryapp.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aarav.imagegalleryapp.data.datasource.MediaStoreDataSource
import com.aarav.imagegalleryapp.data.model.ImageItem
import javax.inject.Inject

class GalleryPagingSource
@Inject constructor(
    val mediaStoreDataSource: MediaStoreDataSource
): PagingSource<Int, ImageItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageItem> {
        val page = params.key ?: 0

        val pageSize = params.loadSize
        val offest = page * pageSize


        return try {
            val images = mediaStoreDataSource.loadImages(pageSize, offest)

            LoadResult.Page(
                data = images,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if(images.isEmpty()) null else page + 1
            )
        }
        catch (e: Exception) {
            Log.d("MYTAG", e.message.toString())
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ImageItem>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

}