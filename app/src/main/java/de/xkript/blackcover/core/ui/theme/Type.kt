package de.xkript.blackcover.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import de.xkript.blackcover.R

object BlackCoverFont {
    
    // Font for app texts
    val oswald = FontFamily(
        Font(R.font.oswald_light, weight = FontWeight.Light),
        Font(R.font.oswald_regular, weight = FontWeight.Normal),
    )
    
    val aBeeZee = FontFamily(
        Font(R.font.a_bee_zee_regular, weight = FontWeight.Normal),
    )
    
    val abrilFatface = FontFamily(
        Font(R.font.abril_fatface_regular, weight = FontWeight.Normal),
    )
    
    val agdasima = FontFamily(
        Font(R.font.agdasima_regular, weight = FontWeight.Normal),
    )
    
    val aldrich = FontFamily(
        Font(R.font.aldrich_regular, weight = FontWeight.Normal),
    )
    
    val allerta = FontFamily(
        Font(R.font.allerta_regular, weight = FontWeight.Normal),
    )
    
    val alumniSansPinstripe = FontFamily(
        Font(R.font.alumni_sans_pinstripe_regular, weight = FontWeight.Normal),
    )
    
    val audiowide = FontFamily(
        Font(R.font.audiowide_regular, weight = FontWeight.Normal),
    )
    
}

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)