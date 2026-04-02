package com.aarav.imagegalleryapp.presentaion.navigation

sealed class NavRoute(val path: String) {
    object Photos : NavRoute("photos")

    object Albums : NavRoute("albums")
}