package com.aarav.imagegalleryapp.presentaion.photos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.aarav.imagegalleryapp.data.model.ImageItem
import com.aarav.imagegalleryapp.domain.GalleryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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

    val images = repository.getAllImages()
        .cachedIn(viewModelScope)


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
    val isLoading: Boolean = false
)

sealed class UiEvents {
    data class Error(val message: String) : UiEvents()
}