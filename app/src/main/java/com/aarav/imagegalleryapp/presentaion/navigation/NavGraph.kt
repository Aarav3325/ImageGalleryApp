package com.aarav.imagegalleryapp.presentaion.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aarav.imagegalleryapp.presentaion.albums.AlbumScreen
import com.aarav.imagegalleryapp.presentaion.photos.PhotosScreen

@Composable
fun NavGraph(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navHostController,
        startDestination = NavRoute.Photos.path,
    ) {
        AddPhotosScreen(
            navHostController,
            this
        )
        AddAlbumScreen(
            navHostController,
            this
        )
    }
}

fun AddPhotosScreen(
    navController: NavController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = NavRoute.Photos.path
    ) {
        PhotosScreen(
            photosViewModel = hiltViewModel()
        )
    }
}

fun AddAlbumScreen(
    navController: NavController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = NavRoute.Albums.path
    ) {
        AlbumScreen(
            albumViewModel = hiltViewModel()
        )
    }
}