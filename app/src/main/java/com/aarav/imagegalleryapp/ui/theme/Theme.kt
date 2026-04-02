package com.aarav.imagegalleryapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.aarav.imagegalleryapp.R

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun ImageGalleryAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

val hankenGrotesk = FontFamily(
    Font(R.font.hanken_grotesk_thin, FontWeight.Thin),
    Font(R.font.hanken_grotesk_thin_italic, FontWeight.Thin, FontStyle.Italic),
    Font(R.font.hanken_grotesk_extra_light, FontWeight.ExtraLight),
    Font(R.font.hanken_grotesk_extra_light_italic, FontWeight.ExtraLight, FontStyle.Italic),
    Font(R.font.hanken_grotesk_light, FontWeight.Light),
    Font(R.font.hanken_grotesk_light_italic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.hanken_grotesk_regular, FontWeight.Normal),
    Font(R.font.hanken_grotesk_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.hanken_grotesk_medium, FontWeight.Medium),
    Font(R.font.hanken_grotesk_medium_italic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.hanken_grotesk_semi_bold, FontWeight.SemiBold),
    Font(R.font.hanken_grotesk_semi_bold_italic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.hanken_grotesk_bold, FontWeight.Bold),
    Font(R.font.hanken_grotesk_bold_italic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.hanken_grotesk_extra_bold, FontWeight.ExtraBold),
    Font(R.font.hanken_grotesk_extra_bold_italic, FontWeight.ExtraBold, FontStyle.Italic),
    Font(R.font.hanken_grotesk_black, FontWeight.Black),
    Font(R.font.hanken_grotesk_black_italic, FontWeight.Black, FontStyle.Italic),
)