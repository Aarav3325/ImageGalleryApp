package com.aarav.imagegalleryapp.presentaion.preview

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
//
//    val allImages = photosViewModel.images.collectAsLazyPagingItems()
//    val albumImages = photosViewModel.albumImages.collectAsLazyPagingItems()

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val animateScale by animateFloatAsState(
        targetValue = scale,
        label = "scale"
    )

    val imagesUnpaged = uiState.images
    val albumImagesUnpaged = uiState.albums.filter {
        it.bucketId == uiState.selectedAlbum?.bucketId
    }.map {
        it.images
    }.flatten()

    LaunchedEffect(Unit) {
    }

    val images = remember(uiState.activeSource) {
        when (uiState.activeSource) {
            ImageSource.ALL -> imagesUnpaged
            ImageSource.ALBUM -> albumImagesUnpaged
        }
    }

    val startIndex = remember(images) {
        images.indexOfFirst {
            it.id == uiState.selectedImage?.id
        }.coerceAtLeast(0)
    }

    val pagerState = rememberPagerState(
        initialPage = startIndex,
        pageCount = {
            images.size
        }
    )

    LaunchedEffect(scale) {
        if (scale == 1f) {
            offset = Offset.Zero
        }
    }

    Box(Modifier.fillMaxSize()) {

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = if (scale == 1f) true else false
        ) { page ->

            val image = images[page]

            image?.let {
                AsyncImage(
                    model = it.uri,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {

                            detectTapGestures(
                                onDoubleTap = { tapOffset ->

                                    val newScale = if (scale > 1f) 1f else 2f

                                    val center = Offset(size.width / 2f, size.height / 2f)

                                    offset = if (newScale > 1f) {
                                        (center - tapOffset) * (newScale - 1f)
                                    } else {
                                        Offset.Zero
                                    }

                                    scale = newScale
                                }
                            )

                            detectTransformGestures { _, pan, zoom, _ ->

                                scale = (scale * zoom).coerceIn(1f, 5f)
                                offset += pan
                            }
                        }
                        .graphicsLayer {
                            scaleX = animateScale
                            scaleY = animateScale
                            translationX = offset.x
                            translationY = offset.y
                        }
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
                modifier = Modifier
                    .align(Alignment.CenterStart)
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