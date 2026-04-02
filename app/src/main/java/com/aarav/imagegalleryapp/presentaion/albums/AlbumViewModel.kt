package com.aarav.imagegalleryapp.presentaion.albums

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarav.imagegalleryapp.data.model.Album
import com.aarav.imagegalleryapp.domain.GalleryRepository
import com.aarav.imagegalleryapp.presentaion.photos.UiEvents
import com.aarav.imagegalleryapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel
@Inject constructor(
    val repository: GalleryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlbumUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<UiEvents>()
    val uiEvents = _uiEvents.asSharedFlow()

    fun emitError(message: String) {
        viewModelScope.launch {
            _uiEvents.emit(
                UiEvents.Error(message)
            )
        }
    }
}

data class AlbumUiState(
    val albums: List<Album> = emptyList(),
    val isLoading: Boolean = false
)