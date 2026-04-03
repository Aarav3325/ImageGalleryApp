package com.aarav.imagegalleryapp.presentaion.photos

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aarav.imagegalleryapp.data.model.Album
import com.aarav.imagegalleryapp.data.model.ImageItem
import com.aarav.imagegalleryapp.domain.GalleryRepository
import com.aarav.imagegalleryapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotosViewModel
@Inject constructor(
    val repository: GalleryRepository,
    @ApplicationContext val context: Context
) : ViewModel() {


    private val _uiState = MutableStateFlow(PhotosUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<UiEvents>()
    val uiEvents = _uiEvents.asSharedFlow()

    private val permissionGranted = MutableStateFlow(false)

    fun onSelectImage(imageItem: ImageItem?) {
        _uiState.update {
            it.copy(
                selectedImage = imageItem
            )
        }
    }

    val images = permissionGranted
        .flatMapLatest { granted ->
            if (granted) {
                repository.getAllImages()
            } else {
                flowOf(PagingData.empty())
            }
        }
        .cachedIn(viewModelScope)

    val albumImages = uiState
        .map { it.selectedAlbum }
        .distinctUntilChanged()
        .flatMapLatest { album ->

            if (album != null) {
                repository.getAlbumImages(album.bucketId)
            } else {
                flowOf(PagingData.empty())
            }
        }
        .cachedIn(viewModelScope)


    fun onPermissionResult(granted: Boolean) {
        permissionGranted.value = granted

        _uiState.update {
            it.copy(
                isGranted = granted
            )
        }
    }

    fun getAllImages(context: Context) {

        viewModelScope.launch {
            val images = repository.getAllImages(context)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    images = images
                )
            }
        }
    }

    fun loadAlbums(
        context: Context
    ) {
        _uiState.update {
            it.copy(
                albumLoading = true
            )
        }

        viewModelScope.launch {

            when (val result = repository.getAllAlbums(context)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            albumLoading = false,
                            albums = result.data
                        )
                    }
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            albumLoading = false
                        )
                    }
                    emitError(result.message)
                }
            }
        }
    }

    fun changeAlbumSelection(album: Album?) {
        _uiState.update {
            it.copy(
                selectedAlbum = album
            )
        }

        Log.d("MYTAG", "current : ${uiState.value.selectedAlbum}")
    }

    fun openPreview(index: Int, source: ImageSource) {
        _uiState.update {
            it.copy(
                selectedIndex = index,
                activeSource = source
            )
        }
    }

    fun emitError(message: String) {
        viewModelScope.launch {
            _uiEvents.emit(
                UiEvents.Error(message)
            )
        }
    }
}

data class PhotosUiState(
    val images: List<ImageItem> = emptyList(),
    val selectedImage: ImageItem? = null,
    val selectedIndex: Int = 0,
    val activeSource: ImageSource = ImageSource.ALL,
    val isLoading: Boolean = false,
    val isGranted: Boolean = false,
    val albums: List<Album> = emptyList(),
    val albumLoading: Boolean = false,
    val selectedAlbum: Album? = null
)

sealed class UiEvents {
    data class Error(val message: String) : UiEvents()
}

enum class ImageSource {
    ALL,
    ALBUM
}