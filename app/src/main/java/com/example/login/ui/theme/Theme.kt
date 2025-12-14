package com.example.login.ui.theme
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.ui.theme.AppTypography

// --- COLORES CLAROS ---
val primaryLight = Color(0xFF855318)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFFFDCBE)
val onPrimaryContainerLight = Color(0xFF693C00)
val secondaryLight = Color(0xFF725A42)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFFFDCBE)
val onSecondaryContainerLight = Color(0xFF59422C)
val tertiaryLight = Color(0xFF58633A)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFDCE8B4)
val onTertiaryContainerLight = Color(0xFF414B24)
val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF93000A)
val backgroundLight = Color(0xFFFFF8F5)
val onBackgroundLight = Color(0xFF211A14)
val surfaceLight = Color(0xFFFFF8F5)
val onSurfaceLight = Color(0xFF211A14)
val surfaceVariantLight = Color(0xFFF2DFD1)
val onSurfaceVariantLight = Color(0xFF51453A)
val outlineLight = Color(0xFF837468)
val outlineVariantLight = Color(0xFFD5C3B5)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF372F28)
val inverseOnSurfaceLight = Color(0xFFFDEEE3)
val inversePrimaryLight = Color(0xFFFCB975)
val surfaceDimLight = Color(0xFFE6D7CD)
val surfaceBrightLight = Color(0xFFFFF8F5)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFFFF1E7)
val surfaceContainerLight = Color(0xFFFAEBE0)
val surfaceContainerHighLight = Color(0xFFF4E5DB)
val surfaceContainerHighestLight = Color(0xFFEFE0D5)

// --- COLORES OSCUROS ---
val primaryDark = Color(0xFFFCB975)
val onPrimaryDark = Color(0xFF4A2800)
val primaryContainerDark = Color(0xFF693C00)
val onPrimaryContainerDark = Color(0xFFFFDCBE)
val secondaryDark = Color(0xFFE1C1A4)
val onSecondaryDark = Color(0xFF402C18)
val secondaryContainerDark = Color(0xFF59422C)
val onSecondaryContainerDark = Color(0xFFFFDCBE)
val tertiaryDark = Color(0xFFC0CC9A)
val onTertiaryDark = Color(0xFF2A3410)
val tertiaryContainerDark = Color(0xFF414B24)
val onTertiaryContainerDark = Color(0xFFDCE8B4)
val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)
val backgroundDark = Color(0xFF19120C)
val onBackgroundDark = Color(0xFFEFE0D5)
val surfaceDark = Color(0xFF19120C)
val onSurfaceDark = Color(0xFFEFE0D5)
val surfaceVariantDark = Color(0xFF51453A)
val onSurfaceVariantDark = Color(0xFFD5C3B5)
val outlineDark = Color(0xFF9D8E81)
val outlineVariantDark = Color(0xFF51453A)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFEFE0D5)
val inverseOnSurfaceDark = Color(0xFF372F28)
val inversePrimaryDark = Color(0xFF855318)
val surfaceDimDark = Color(0xFF19120C)
val surfaceBrightDark = Color(0xFF403830)
val surfaceContainerLowestDark = Color(0xFF130D07)
val surfaceContainerLowDark = Color(0xFF211A14)
val surfaceContainerDark = Color(0xFF261E18)
val surfaceContainerHighDark = Color(0xFF302822)
val surfaceContainerHighestDark = Color(0xFF3C332C)

private val DarkColorScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark
)

private val LightColorScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight
)

@Composable
fun LoginTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,  // ← IMPORTANTE: ponlo a false temporalmente
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