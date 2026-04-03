package com.aarav.imagegalleryapp.presentaion.search

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.aarav.imagegalleryapp.presentaion.components.CalendarComponent
import com.aarav.imagegalleryapp.presentaion.photos.ImageSource
import com.aarav.imagegalleryapp.presentaion.photos.PhotoGridCell
import com.aarav.imagegalleryapp.presentaion.photos.PhotosViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    photosViewModel: PhotosViewModel,
    navigateToDisplay: () -> Unit
) {

    val uiState by photosViewModel.uiState.collectAsState()



    var selectedDay by remember {
        mutableStateOf<LocalDate>(LocalDate.now())
    }
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    val final = selectedDay?.atStartOfDay(ZoneId.systemDefault())
        ?.toInstant()
        ?.toEpochMilli()
    val context = LocalContext.current

    val filteredImages by remember(selectedDay, uiState.images) {
        derivedStateOf {

            selectedDay?.let { selectedDate ->

                uiState.images.filter { image ->
                    val imageDate = java.time.Instant.ofEpochMilli(image.dateAdded)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()

                    Log.d("SEARCH", "img: $imageDate")
                    Log.d("SEARCH", "sel: $selectedDate")
                    imageDate == selectedDate
                }

            } ?: emptyList()
        }
    }

    val imageDates by remember(uiState.images) {
        derivedStateOf {
            uiState.images.map { image ->
                java.time.Instant.ofEpochMilli(image.dateAdded)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            }.toSet()
        }
    }


//    LaunchedEffect(Unit) {
//        photosViewModel.getAllImages(context)
//        photosViewModel.loadAlbums(context)
//    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                title = {
                    Text(
                        text = "Search",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            CalendarComponent(
                selectedDay = selectedDay,
                onSelect = {
                    selectedDay = it
                },
                imageDates
            )


            Log.d("SEARCH", "Search Screen: $final")

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize()
            ) {

                item(
                    span = { GridItemSpan(3) }
                ) {
                    if(filteredImages.isNotEmpty()) {
                        Text(
                            text = selectedDay?.format(formatter) ?: "No date selected",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 12.dp)
                        )
                    }
                    else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No photos found for selected date",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Log.d("SEARCH", "Search Screen: ${filteredImages.size}")
                items(filteredImages.size) { index ->
                    val image = filteredImages[index]

                    PhotoGridCell(
                        imageItem = image,
                        index = index,
                        context = context,
                        onClick = {
                            photosViewModel.openPreview(index, ImageSource.ALL)
                            photosViewModel.onSelectImage(image)
                            navigateToDisplay()
                        }
                    )
                }
            }

        }
    }
}