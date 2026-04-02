package com.aarav.imagegalleryapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aarav.imagegalleryapp.presentaion.navigation.BottomNavigationBar
import com.aarav.imagegalleryapp.presentaion.navigation.NavGraph
import com.aarav.imagegalleryapp.presentaion.navigation.NavItem
import com.aarav.imagegalleryapp.presentaion.navigation.NavRoute
import com.aarav.imagegalleryapp.ui.theme.ImageGalleryAppTheme
import com.aarav.imagegalleryapp.utils.SnackbarManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {


            val context = LocalContext.current

            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }


            var isGranted by remember {
                mutableStateOf(
                    ContextCompat
                        .checkSelfPermission(
                            context,
                            permission
                        ) == PackageManager.PERMISSION_GRANTED
                )
            }


            RequestStoragePermission(
                context,
                sharedPreferences,
                isGranted,
                onGranted = {
                    isGranted = it
                },
                permission
            )

            val navController = rememberNavController()

            val snackbarHost = remember {
                SnackbarHostState()
            }

            LaunchedEffect(snackbarHost) {
                SnackbarManager.bind(snackbarHost)
            }


            val currentBackstackEntry by navController.currentBackStackEntryAsState()

            val currentRoute = currentBackstackEntry?.destination?.route

            val showBottomRoutes = listOf(
                NavRoute.Photos.path,
                NavRoute.Albums.path
            )

            val isBottomBarVisible = currentRoute in showBottomRoutes

            ImageGalleryAppTheme {
                Scaffold(
                    bottomBar = {
                        AnimatedVisibility(isBottomBarVisible) {
                            BottomNavigationBar(navController)
                        }
                    },
                    snackbarHost = {
                        SnackbarHost(snackbarHost)
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    NavGraph(
                        navController,
                        isGranted,
                        Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun RequestStoragePermission(
    context: Context,
    sharedPreferences: SharedPreferences,
    isGranted: Boolean,
    onGranted: (Boolean) -> Unit,
    permission: String
) {


    LaunchedEffect(isGranted) {
        val alreadyGranted = sharedPreferences.getBoolean("storage_permission", false)

        if (!alreadyGranted && isGranted) {
            Toast.makeText(
                context,
                "Permission Granted",
                Toast.LENGTH_SHORT
            ).show()

            sharedPreferences.edit(commit = true) {
                putBoolean("storage_permission", true)
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        onGranted(it)
    }

    LaunchedEffect(Unit) {
        if (!isGranted) {
            launcher.launch(permission)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ImageGalleryAppTheme {
        Greeting("Android")
    }
}
