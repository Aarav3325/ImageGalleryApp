package com.aarav.imagegalleryapp.presentaion.albums

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aarav.imagegalleryapp.R
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.aarav.imagegalleryapp.presentaion.photos.ImageSource
import com.aarav.imagegalleryapp.presentaion.photos.PhotoGridCell
import com.aarav.imagegalleryapp.presentaion.photos.PhotosViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailScreen(
    photosViewModel: PhotosViewModel,
    onBack: () -> Unit,
    navigateToDisplay: () -> Unit
) {
    val uiState by photosViewModel.uiState.collectAsState()

    val albumImages = photosViewModel.albumImages.collectAsLazyPagingItems()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        albumImages.refresh()
        Log.d("MYTAG", "selected : ${albumImages.itemCount}")
    }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = uiState.selectedAlbum?.name ?: "Albums",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                onBack()
                                delay(200)
                                photosViewModel.changeAlbumSelection(null)
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.back),
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            when {

                albumImages.loadState.refresh is LoadState.Loading -> {
                    CircularProgressIndicator(
                        Modifier.align(Alignment.Center)
                    )
                }

                albumImages.itemCount == 0 -> {
                    Text(
                        text = "No images found",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyVerticalGrid(
                        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 16.dp),
                        columns = GridCells.Fixed(3),
                    ) {
                        items(albumImages.itemCount) { index ->
                            val image = albumImages[index]
                            image?.let {
                                PhotoGridCell(
                                    imageItem = it,
                                    index = index,
                                    context = context,
                                    onClick = {
                                        photosViewModel.openPreview(index, ImageSource.ALBUM)
                                       //photosViewModel.onSelectImage(it)
                                        navigateToDisplay()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}