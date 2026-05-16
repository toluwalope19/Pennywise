package com.example.ui.components


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.LocalGroceryStore
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.EducationGradientEnd
import com.example.ui.theme.EducationGradientStart
import com.example.ui.theme.FoodGradientEnd
import com.example.ui.theme.FoodGradientStart
import com.example.ui.theme.HealthGradientEnd
import com.example.ui.theme.HealthGradientStart
import com.example.ui.theme.Income
import com.example.ui.theme.ShoppingGradientEnd
import com.example.ui.theme.ShoppingGradientStart
import com.example.ui.theme.TransportGradientEnd
import com.example.ui.theme.TransportGradientStart
import com.example.ui.theme.TravelGradientEnd
import com.example.ui.theme.TravelGradientStart
import com.example.ui.theme.UtilitiesGradientEnd
import com.example.ui.theme.UtilitiesGradientStart


enum class CategoryType(
    val gradientStart: Color,
    val gradientEnd: Color,
    val icon: ImageVector,
    val displayName: String
) {
    FOOD(
        FoodGradientStart, FoodGradientEnd,
        Icons.Rounded.Restaurant, "Food"
    ),
    GROCERIES(
        FoodGradientStart, FoodGradientEnd,
        Icons.Rounded.LocalGroceryStore, "Groceries"
    ),
    SHOPPING(
        ShoppingGradientStart, ShoppingGradientEnd,
        Icons.Rounded.ShoppingBag, "Shopping"
    ),
    HEALTH(
        HealthGradientStart, HealthGradientEnd,
        Icons.Rounded.FitnessCenter, "Health & Fitness"
    ),
    TRANSPORT(
        TransportGradientStart, TransportGradientEnd,
        Icons.Rounded.DirectionsCar, "Transport"
    ),
    EDUCATION(
        EducationGradientStart, EducationGradientEnd,
        Icons.Rounded.MenuBook, "Education"
    ),
    UTILITIES(
        UtilitiesGradientStart, UtilitiesGradientEnd,
        Icons.Rounded.Bolt, "Utilities"
    ),
    TRAVEL(
        TravelGradientStart, TravelGradientEnd,
        Icons.Rounded.Flight, "Travel"
    ),
    INCOME(
        Income, Color(0xFF0BA67B),
        Icons.Rounded.Payments, "Income"
    ),
    SAVINGS(
        Income, Color(0xFF0BA67B),
        Icons.Rounded.Savings, "Savings"
    ),
    OTHER(
        Color(0xFF8C8C8C), Color(0xFF555555),
        Icons.Rounded.MoreHoriz, "Other"
    );

    companion object {
        fun fromName(name: String): CategoryType =
            entries.firstOrNull {
                it.name.equals(name, ignoreCase = true)
            } ?: OTHER
    }
}