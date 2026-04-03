package com.aarav.imagegalleryapp.data.repository

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aarav.imagegalleryapp.data.datasource.MediaStoreDataSource
import com.aarav.imagegalleryapp.data.model.Album
import com.aarav.imagegalleryapp.data.model.ImageItem
import com.aarav.imagegalleryapp.data.paging.AlbumPagingSource
import com.aarav.imagegalleryapp.data.paging.GalleryPagingSource
import com.aarav.imagegalleryapp.domain.GalleryRepository
import com.aarav.imagegalleryapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GalleryRepositoryImpl
@Inject constructor(
    val mediaStoreDataSource: MediaStoreDataSource
) : GalleryRepository {


    private var cachedImages: List<ImageItem>? = null


    override suspend fun getAllImages(context: Context): List<ImageItem> {
        val imageList = mutableListOf<ImageItem>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val query = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )

        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val displayNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateModifiedColumn = cursor.getColumnIndexOrThrow(
                MediaStore.Images.Media.DATE_MODIFIED
            )
            val dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)


            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)

                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val dateTaken = cursor.getLong(dateTakenColumn)
                val dateAdded = cursor.getLong(dateAddedColumn)
                val dateModified = cursor.getLong(dateModifiedColumn)

                val finalDate = when {
                    dateTaken > 0 -> dateTaken
                    dateModified > 0 -> dateModified * 1000
                    dateAdded > 0 -> dateAdded * 1000
                    else -> System.currentTimeMillis()
                }

                imageList.add(
                    ImageItem(
                        id,
                        cursor.getString(displayNameColumn),
                        uri,
                        finalDate,
                        cursor.getString(bucketIdColumn),
                        cursor.getString(bucketNameColumn)
                    )
                )
            }
        }

        cachedImages = imageList


        return imageList
    }

    override suspend fun getAllAlbums(context: Context): Resource<List<Album>> {

        val images = cachedImages ?: getAllImages(context)

        val albums = images
            .groupBy { it.bucketId }
            .map { (_, imageList) ->
                Album(
                    name = imageList.first().bucketName,
                    images = imageList,
                    thumbnail = imageList.first().uri,
                    imageCount = imageList.size,
                    bucketId = imageList.first().bucketId
                )
            }

        return Resource.Success(albums)
    }

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

    override fun getAlbumImages(bucketId: String): Flow<PagingData<ImageItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 50,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                AlbumPagingSource(
                    mediaStoreDataSource,
                    bucketId
                )
            }
        ).flow
    }

}