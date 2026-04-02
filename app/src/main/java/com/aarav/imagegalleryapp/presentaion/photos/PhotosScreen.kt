package com.aarav.imagegalleryapp.presentaion.photos

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aarav.imagegalleryapp.data.model.ImageItem
import com.aarav.imagegalleryapp.utils.SnackbarManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotosScreen(
    photosViewModel: PhotosViewModel
) {
    val uiState by photosViewModel.uiState.collectAsState()

    val images = photosViewModel.images.collectAsLazyPagingItems()

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
        photosViewModel.onPermissionResult(isGranted)
    }

//    LaunchedEffect(isGranted) {
//        if(isGranted) {
//            photosViewModel.loadImages(context)
//        }
//    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
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
                !isGranted -> {
                    Text(
                        text = "Permission required to show images",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                images.loadState.refresh is LoadState.Loading -> {
                    CircularProgressIndicator(
                        Modifier.align(Alignment.Center)
                    )
                }

                images.itemSnapshotList.isEmpty() -> {
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
                        items(images.itemCount) {
                            val image = images[it]
                            image?.let {
                                PhotoGridCell(it, context)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PhotoGridCell(
    imageItem: ImageItem,
    context: Context
) {
    val model = ImageRequest.Builder(context)
        .data(imageItem.uri)
        .size(300)
        .build()

    AsyncImage(
        model = model,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .aspectRatio(1f)
            .padding(vertical = 2.dp, horizontal = 1.dp)
    )
}