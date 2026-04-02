package com.aarav.imagegalleryapp.data

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import com.aarav.imagegalleryapp.data.model.Album
import com.aarav.imagegalleryapp.data.model.ImageItem
import com.aarav.imagegalleryapp.domain.GalleryRepository
import com.aarav.imagegalleryapp.utils.Resource
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class GalleryRepositoryImpl
@Inject constructor() : GalleryRepository {

    private var cachedImages: List<ImageItem>? = null

    override suspend fun getAllImages(context: Context): Resource<List<ImageItem>> {
        cachedImages?.let { return Resource.Success(it) }

        val imageList = mutableListOf<ImageItem>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
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

        try {
            query?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
                val bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)



                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)

                    val contextUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    val imageItem = ImageItem(
                        id,
                        cursor.getString(displayNameColumn),
                        contextUri,
                        cursor.getLong(dateAddedColumn),
                        cursor.getString(bucketIdColumn),
                        cursor.getString(bucketNameColumn)
                    )

                    imageList.add(imageItem)
                }
            }

            cachedImages = imageList

            return Resource.Success(imageList)
        }
        catch (e: Exception) {
            return Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun getAllAlbums(context: Context): Resource<List<Album>> {

        val images = cachedImages ?: when (val result = getAllImages(context)) {
            is Resource.Success -> result.data
            is Resource.Error -> return Resource.Error(result.message)
        }

        val albums = images
            .groupBy { it.bucketId }
            .map { (_, imageList) ->
                Album(
                    name = imageList.first().bucketName,
                    thumbnail = imageList.first().uri,
                    imageCount = imageList.size,
                    images = imageList
                )
            }

        return Resource.Success(albums)
    }

}