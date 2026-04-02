package com.aarav.imagegalleryapp.presentaion.preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.aarav.imagegalleryapp.R
import com.aarav.imagegalleryapp.presentaion.photos.PhotosViewModel
import com.aarav.imagegalleryapp.utils.formatDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullscreenPreview(
    photosViewModel: PhotosViewModel,
    onBack: () -> Unit
) {

    val uiState by photosViewModel.uiState.collectAsState()

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp)
        ) {
            IconButton(
                modifier = Modifier.align(Alignment.CenterStart)
                    .padding(start = 4.dp),
                onClick = {
                    scope.launch {
                        onBack()
                        delay(200)
                        photosViewModel.onSelectImage(null)
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.back),
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp)
                )
            }

            val date = uiState.selectedImage?.dateAdded

            Text(
                text = date?.let { formatDate(it) } ?: "",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        when {
            uiState.selectedImage == null -> {
                CircularProgressIndicator(
                    Modifier.align(Alignment.Center)
                )
            }

            else -> {
                AsyncImage(
                    model = uiState.selectedImage?.uri,
                    contentScale = ContentScale.Fit,
                    contentDescription = "Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(1.0f)
                )
            }
        }
    }
}