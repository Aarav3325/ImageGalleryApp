package com.aarav.imagegalleryapp.presentaion.photos

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aarav.imagegalleryapp.data.model.ImageItem
import com.aarav.imagegalleryapp.presentaion.components.CalendarComponent
import com.aarav.imagegalleryapp.utils.SnackbarManager
import com.aarav.imagegalleryapp.utils.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotosScreen(
    photosViewModel: PhotosViewModel,
    isGranted: Boolean,
    navigateToDisplay: () -> Unit
) {
    val uiState by photosViewModel.uiState.collectAsState()

    val images = photosViewModel.images.collectAsLazyPagingItems()

    val groupedList = remember(images.itemSnapshotList.items) {
        images.itemSnapshotList.items
            .groupBy { formatDate(it.dateAdded) }
            .mapValues { (_, list) -> list.chunked(3) }
    }

    LaunchedEffect(Unit) {
        photosViewModel.uiEvents.collect { event ->
            if (event is UiEvents.Error) {
                SnackbarManager.showMessage(event.message)
            }
        }
    }

    val context = LocalContext.current

    LaunchedEffect(isGranted) {
        photosViewModel.onPermissionResult(isGranted)
    }

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

//                true -> {
//                    CalendarComponent()
//                }

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
                    Column() {
                        LazyColumn(
                            //contentPadding = PaddingValues(horizontal = 16.dp),
                            modifier = Modifier.fillMaxSize()
                                .padding(bottom = 82.dp),
                        ) {

                            var globalIndex = 0
                            groupedList.forEach { (date, rows) ->

                                item {
                                    Text(
                                        text = date,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }

                                items(rows) { row ->

                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {

                                        row.forEachIndexed { localIndex, image ->

                                            val currentIndex = globalIndex

                                            PhotoGridCell(
                                                imageItem = image,
                                                index = localIndex,
                                                context = context,
                                                modifier = Modifier.weight(1f),
                                                onClick = {
                                                    photosViewModel.openPreview(currentIndex, ImageSource.ALL)
                                                    photosViewModel.onSelectImage(image)
                                                    navigateToDisplay()
                                                }
                                            )

                                            globalIndex++
                                        }

                                        repeat(3 - row.size) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
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
    index: Int,
    context: Context,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val model = ImageRequest.Builder(context)
        .data(imageItem.uri)
        .size(300)
        .build()

    AsyncImage(
        model = model,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .aspectRatio(1f)
            .padding(
                top = 1.dp,
                bottom = 1.dp,
                start = if (index == 0) 0.dp else 1.dp,
                end = if (index == 2) 0.dp else 1.dp
            )
            .clickable {
                onClick()
            }
    )
}