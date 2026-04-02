package com.aarav.imagegalleryapp.presentaion.photos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aarav.imagegalleryapp.data.model.ImageItem
import com.aarav.imagegalleryapp.domain.GalleryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotosViewModel
@Inject constructor(
    val repository: GalleryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhotosUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<UiEvents>()
    val uiEvents = _uiEvents.asSharedFlow()

    private val permissionGranted = MutableStateFlow(false)

    val images = permissionGranted
        .flatMapLatest {
            granted ->
            if(granted) {
                repository.getAllImages()
            }
            else {
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
    val isLoading: Boolean = false,
    val isGranted: Boolean = false
)

sealed class UiEvents {
    data class Error(val message: String) : UiEvents()
}