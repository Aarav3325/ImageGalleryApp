package com.aarav.imagegalleryapp.presentaion.photos

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.aarav.imagegalleryapp.data.model.ImageItem
import com.aarav.imagegalleryapp.utils.SnackbarManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotosScreen(
    photosViewModel: PhotosViewModel
) {
    val uiState by photosViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        photosViewModel.uiEvents.collect { event ->
            if(event is UiEvents.Error) {
                SnackbarManager.showMessage(event.message)
            }
        }
    }

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
            photosViewModel.loadImages(context)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                expandedHeight = 52.dp,
                title = {
                    Text(
                        text = "Photos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        Modifier.align(Alignment.Center)
                    )
                }

                uiState.images.isEmpty() -> {
                    Text(
                        text = "No images found",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(uiState.images) {
                            PhotoGridCell(it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PhotoGridCell(
    imageItem: ImageItem
) {
    AsyncImage(
        model = imageItem.uri.toString(),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(120.dp)
            .padding(vertical = 8.dp)
    )
}