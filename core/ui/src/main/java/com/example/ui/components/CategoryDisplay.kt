package com.example.ui.components


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Cake
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.ContentCut
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalBar
import androidx.compose.material.icons.rounded.LocalFlorist
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.domain.model.Category
import androidx.core.graphics.toColorInt

// core/ui/src/main/java/com/pennywise/ui/components/CategoryDisplay.kt
data class CategoryDisplay(
    val name: String,
    val icon: ImageVector,
    val color: Color
)

fun Category.toDisplay(): CategoryDisplay {
    // Try to match a built-in category first
    val categoryType = CategoryType.fromName(name)
    val isBuiltIn = categoryType != CategoryType.OTHER ||
            name.equals("other", ignoreCase = true)

    return if (isBuiltIn) {
        CategoryDisplay(
            name = categoryType.displayName,
            icon = categoryType.icon,
            color = categoryType.gradientStart
        )
    } else {
        // Custom category — parse stored color and icon
        val parsedColor = try {
            Color(color.toColorInt())
        } catch (e: Exception) {
            Color(0xFF8C8C8C)
        }

        val parsedIcon = iconNameToVector(icon)

        CategoryDisplay(
            name = name,
            icon = parsedIcon,
            color = parsedColor
        )
    }
}

// Map stored icon name back to ImageVector.
// Normalises "Rounded.SportsEsports" / "Rounded/SportsEsports" → "SportsEsports"
// so categories saved before the separator fix still resolve correctly.
private fun iconNameToVector(iconName: String): ImageVector {
    val name = iconName.substringAfterLast("/").substringAfterLast(".")
    return when (name) {
        "Pets" -> Icons.Rounded.Pets
        "Favorite" -> Icons.Rounded.Favorite
        "CardGiftcard" -> Icons.Rounded.CardGiftcard
        "Cake" -> Icons.Rounded.Cake
        "SportsEsports" -> Icons.Rounded.SportsEsports
        "Checkroom" -> Icons.Rounded.Checkroom
        "Eco" -> Icons.Rounded.Eco
        "ContentCut" -> Icons.Rounded.ContentCut
        "School" -> Icons.Rounded.School
        "Work" -> Icons.Rounded.Work
        "Smartphone" -> Icons.Rounded.Smartphone
        "Build" -> Icons.Rounded.Build
        "Home" -> Icons.Rounded.Home
        "LocalFlorist" -> Icons.Rounded.LocalFlorist
        "Coffee" -> Icons.Rounded.Coffee
        "LocalBar" -> Icons.Rounded.LocalBar
        "AttachMoney" -> Icons.Rounded.AttachMoney
        "MoreHoriz" -> Icons.Rounded.MoreHoriz
        else -> Icons.Rounded.MoreHoriz
    }
}
