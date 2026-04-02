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
import com.aarav.imagegalleryapp.presentaion.preview.FullscreenPreview
import com.aarav.imagegalleryapp.presentaion.photos.PhotosScreen
import com.aarav.imagegalleryapp.presentaion.photos.PhotosViewModel

@Composable
fun NavGraph(
    navHostController: NavHostController,
    isGranted: Boolean,
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
                isGranted,
                navHostController,
                this
            )
            AddAlbumScreen(
                isGranted,
                navHostController,
                this
            )
            AddAlbumDetialScreen(
                navHostController,
                this
            )
            AddDisplayImageScreen(
                navHostController,
                this
            )
        }
    }
}

fun AddPhotosScreen(
    isGranted: Boolean,
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
            photosViewModel = sharedVM,
            isGranted = isGranted,
            navigateToDisplay = {
                navController.navigate(NavRoute.FullscreenPreview.path)
            }
        )
    }
}

fun AddAlbumScreen(
    isGranted: Boolean,
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
            isGranted = isGranted,
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
            },
            navigateToDisplay = {
                navController.navigate(NavRoute.FullscreenPreview.path)
            }
        )
    }
}

fun AddDisplayImageScreen(
    navController: NavController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = NavRoute.FullscreenPreview.path
    ) { backStackEntry ->

        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry("album_graph")
        }

        val sharedVM: PhotosViewModel = hiltViewModel(parentEntry)

        FullscreenPreview(
            photosViewModel = sharedVM,
            onBack = {
                navController.popBackStack()
            }
        )
    }
}