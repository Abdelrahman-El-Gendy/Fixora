package com.fixora.core.designsystem.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RatingBar(
    rating: Float,
    modifier: Modifier = Modifier,
    starSize: Dp = 18.dp,
    starColor: Color = Color(0xFFFFB300), // Amber-600
    maxStars: Int = 5
) {
    Row(modifier = modifier) {
        for (i in 1..maxStars) {
            val starIcon = when {
                rating >= i -> Icons.Filled.Star
                rating > i - 1 && rating < i -> Icons.Filled.StarHalf
                else -> Icons.Filled.StarOutline
            }

            Icon(
                imageVector = starIcon,
                contentDescription = "Star $i",
                tint = starColor,
                modifier = Modifier.size(starSize)
            )
            if (i < maxStars) {
                Spacer(modifier = Modifier.width(2.dp))
            }
        }
    }
}
