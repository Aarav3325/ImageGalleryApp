package com.aarav.imagegalleryapp.data.datasource

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.aarav.imagegalleryapp.data.model.ImageItem
import com.aarav.imagegalleryapp.utils.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MediaStoreDataSource
@Inject constructor(
    @ApplicationContext val context: Context
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun loadImages(pageSize: Int, offset: Int): List<ImageItem> {

        val imageList = ArrayList<ImageItem>(pageSize)

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )

        val bundle = Bundle().apply {
            putInt(ContentResolver.QUERY_ARG_LIMIT, pageSize)
            putInt(ContentResolver.QUERY_ARG_OFFSET, offset)
            putStringArray(
                ContentResolver.QUERY_ARG_SORT_COLUMNS,
                arrayOf(MediaStore.Images.Media.DATE_ADDED)
            )
            putInt(
                ContentResolver.QUERY_ARG_SORT_DIRECTION,
                ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
            )
        }

        val query = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            bundle,
            null
        )

        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)

                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                imageList.add(
                    ImageItem(
                        id,
                        cursor.getString(displayNameColumn),
                        uri,
                        cursor.getLong(dateAddedColumn),
                        cursor.getString(bucketIdColumn),
                        cursor.getString(bucketNameColumn)
                    )
                )
            }
        }

        return imageList
    }
}