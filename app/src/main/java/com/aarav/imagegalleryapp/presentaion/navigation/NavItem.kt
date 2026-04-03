package com.aarav.imagegalleryapp.presentaion.navigation

import com.aarav.imagegalleryapp.R

sealed class NavItem(val path: String, val icon: Int, val title: String) {
    object Photos : NavItem("photos", R.drawable.photos, "Photos")
    object Albums : NavItem("albums", R.drawable.album, "Albums")
    object Search : NavItem("search", R.drawable.search, "Search")
}