package com.vibedev.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.vibedev.app.R

// Nunito (bundled, SIL Open Font License): rounded and friendly.
@OptIn(ExperimentalTextApi::class)
private fun nunito(weight: FontWeight) = Font(
    R.font.nunito,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
)

private val Nunito = FontFamily(
    nunito(FontWeight.Normal),
    nunito(FontWeight.Medium),
    nunito(FontWeight.SemiBold),
    nunito(FontWeight.Bold),
    nunito(FontWeight.ExtraBold),
)

// Larger than Material's defaults, for easy reading.
val AppTypography = Typography().let { t ->
    t.copy(
        displaySmall = t.displaySmall.copy(fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 36.sp, lineHeight = 44.sp),
        headlineMedium = t.headlineMedium.copy(fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 30.sp, lineHeight = 38.sp),
        headlineSmall = t.headlineSmall.copy(fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 26.sp, lineHeight = 34.sp),
        titleLarge = t.titleLarge.copy(fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 32.sp),
        titleMedium = t.titleMedium.copy(fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 28.sp),
        titleSmall = t.titleSmall.copy(fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 17.sp, lineHeight = 24.sp),
        bodyLarge = t.bodyLarge.copy(fontFamily = Nunito, fontSize = 20.sp, lineHeight = 29.sp),
        bodyMedium = t.bodyMedium.copy(fontFamily = Nunito, fontSize = 18.sp, lineHeight = 26.sp),
        bodySmall = t.bodySmall.copy(fontFamily = Nunito, fontSize = 16.sp, lineHeight = 22.sp),
        labelLarge = t.labelLarge.copy(fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 18.sp, lineHeight = 24.sp),
        labelMedium = t.labelMedium.copy(fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 15.sp, lineHeight = 20.sp),
        labelSmall = t.labelSmall.copy(fontFamily = Nunito, fontSize = 13.sp, lineHeight = 18.sp),
    )
}
