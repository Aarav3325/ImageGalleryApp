package com.aarav.imagegalleryapp.presentaion.preview

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.aarav.imagegalleryapp.R
import com.aarav.imagegalleryapp.presentaion.photos.ImageSource
import com.aarav.imagegalleryapp.presentaion.photos.PhotosViewModel
import com.aarav.imagegalleryapp.utils.formatDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FullscreenPreview(
    photosViewModel: PhotosViewModel,
    onBack: () -> Unit
) {

    val uiState by photosViewModel.uiState.collectAsState()

    val allImages = photosViewModel.images.collectAsLazyPagingItems()
    val albumImages = photosViewModel.albumImages.collectAsLazyPagingItems()

    val images = remember(uiState.activeSource) {
        when (uiState.activeSource) {
            ImageSource.ALL -> allImages
            ImageSource.ALBUM -> albumImages
        }
    }

    val startIndex = remember(images.itemSnapshotList.items) {
        images.itemSnapshotList.items.indexOfFirst {
            it.id == uiState.selectedImage?.id
        }.coerceAtLeast(0)
    }
    val pagerState = rememberPagerState(
        initialPage = startIndex,
        pageCount = {
            images.itemCount
        }
    )

    Box(Modifier.fillMaxSize()) {

        HorizontalPager(
            state = pagerState
        ) { page ->

            val image = images[page]

            image?.let {
                AsyncImage(
                    model = it.uri,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        val currentImage = images[pagerState.currentPage]

        val date = currentImage?.dateAdded


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp)
        ) {
            IconButton(
                modifier = Modifier.align(Alignment.CenterStart)
                    .padding(start = 4.dp),
                onClick = {
                    onBack()
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.back),
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = date?.let { formatDate(it) } ?: "",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}