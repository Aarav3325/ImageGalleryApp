package com.aarav.imagegalleryapp.presentaion.albums

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.aarav.imagegalleryapp.data.model.Album
import com.aarav.imagegalleryapp.presentaion.photos.PhotosViewModel
import com.aarav.imagegalleryapp.presentaion.photos.UiEvents
import com.aarav.imagegalleryapp.utils.SnackbarManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumScreen(
    photosViewModel: PhotosViewModel,
    navigateToDetail: () -> Unit
) {
    val uiState by photosViewModel.uiState.collectAsState()

    val context = LocalContext.current

    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    var isGranted by remember {
        mutableStateOf(
            ContextCompat
                .checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
        )
    }

    LaunchedEffect(isGranted) {
        if(isGranted) {
            photosViewModel.loadAlbums(context)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Albums",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
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
                !isGranted -> {
                    Text(
                        text = "Permission required to show albums",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.isLoading -> {
                    CircularProgressIndicator(
                        Modifier.align(Alignment.Center)
                    )
                }

                uiState.albums.isEmpty() -> {
                    Text(
                        "No albums found",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.albums) {
                            AlbumGridCell(
                                album = it,
                                onClick = {
                                    photosViewModel.changeAlbumSelection(it)
                                    navigateToDetail()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlbumGridCell(
    album: Album,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
    ) {
        AsyncImage(
            model = album.thumbnail,
            contentDescription = "Album thumbnail",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .aspectRatio(1.1f)
                .clip(
                    RoundedCornerShape(12.dp)
                )
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant,
                    RoundedCornerShape(12.dp)
                )
                .clickable {
                    onClick()
                }
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = album.name,
            style = MaterialTheme.typography.titleMedium,
        )

        Text(
            text = "${album.imageCount} photos",
            style = MaterialTheme.typography.bodySmall
        )
    }
}