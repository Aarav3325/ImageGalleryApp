package com.aarav.imagegalleryapp.presentaion.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val navItems = listOf(
        NavItem.Photos,
        NavItem.Albums,
        NavItem.Search
    )

    val currentBackstackEntry by navController.currentBackStackEntryAsState()

    val currentRoute = currentBackstackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 8.dp,
    ) {

        navItems.forEachIndexed { index, item ->
            val isSelected = currentRoute == item.path

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.path) {
                        navController.navigate(item.path) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo("album_graph") {
                                saveState = true
                            }
                        }
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = "icon",
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        item.title
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}