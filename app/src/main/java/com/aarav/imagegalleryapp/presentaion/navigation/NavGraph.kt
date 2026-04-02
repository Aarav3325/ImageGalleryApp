package com.aarav.imagegalleryapp.presentaion.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.aarav.imagegalleryapp.presentaion.albums.AlbumDetailScreen
import com.aarav.imagegalleryapp.presentaion.albums.AlbumScreen
import com.aarav.imagegalleryapp.presentaion.photos.PhotosScreen
import com.aarav.imagegalleryapp.presentaion.photos.PhotosViewModel

@Composable
fun NavGraph(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navHostController,
        startDestination = "album_graph",
    ) {

        navigation(
            route = "album_graph",
            startDestination = NavRoute.Photos.path
        ) {
            AddPhotosScreen(
                navHostController,
                this
            )
            AddAlbumScreen(
                navHostController,
                this
            )
            AddAlbumDetialScreen(
                navHostController,
                this
            )
        }
    }
}

fun AddPhotosScreen(
    navController: NavController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = NavRoute.Photos.path
    ) { backStackEntry ->

        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry("album_graph")
        }

        val sharedVM: PhotosViewModel = hiltViewModel(parentEntry)

        PhotosScreen(
            photosViewModel = sharedVM
        )
    }
}

fun AddAlbumScreen(
    navController: NavController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = NavRoute.Albums.path
    ) { backStackEntry ->

        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry("album_graph")
        }

        val sharedVM: PhotosViewModel = hiltViewModel(parentEntry)

        AlbumScreen(
            photosViewModel = sharedVM,
            navigateToDetail = {
                navController.navigate(NavRoute.AlbumDetail.path)
            }
        )
    }
}

fun AddAlbumDetialScreen(
    navController: NavController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = NavRoute.AlbumDetail.path
    ) { backStackEntry ->

        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry("album_graph")
        }

        val sharedVM: PhotosViewModel = hiltViewModel(parentEntry)

        AlbumDetailScreen(
            photosViewModel = sharedVM,
            onBack = {
                navController.popBackStack()
            }
        )
    }
}