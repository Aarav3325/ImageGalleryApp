package com.aarav.imagegalleryapp.presentaion.photos

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarav.imagegalleryapp.data.model.ImageItem
import com.aarav.imagegalleryapp.domain.GalleryRepository
import com.aarav.imagegalleryapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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

    fun loadImages(
        context: Context
    ) {

        _uiState.update {
            it.copy(
                isLoading = true
            )
        }

        viewModelScope.launch {
            when (val result = repository.getAllImages(context)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            images = result.data
                        )
                    }
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    emitError(result.message)
                }
            }
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
    val isLoading: Boolean = false
)

sealed class UiEvents {
    data class Error(val message: String) : UiEvents()
}