package com.vibedev.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.ui.unit.sp
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// The app's main color. Change it to restyle the whole app.
private val Brand = Color(0xFF1F6FEB)

private val LightColors = lightColorScheme(primary = Brand)
private val DarkColors = darkColorScheme(primary = Color(0xFFD0BCFF))

// Larger text than Material's defaults, for easy reading.
private val BigTypography = Typography().let { t ->
    t.copy(
        headlineMedium = t.headlineMedium.copy(fontSize = 30.sp, lineHeight = 38.sp),
        titleLarge = t.titleLarge.copy(fontSize = 24.sp, lineHeight = 32.sp),
        titleMedium = t.titleMedium.copy(fontSize = 20.sp, lineHeight = 28.sp),
        bodyLarge = t.bodyLarge.copy(fontSize = 20.sp, lineHeight = 28.sp),
        bodyMedium = t.bodyMedium.copy(fontSize = 18.sp, lineHeight = 26.sp),
        labelLarge = t.labelLarge.copy(fontSize = 18.sp, lineHeight = 24.sp),
    )
}

@Composable
fun VibeAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Android 12+ can match the phone's wallpaper colors. Set false to always use Brand.
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }
    MaterialTheme(colorScheme = colorScheme, typography = BigTypography, content = content)
}
