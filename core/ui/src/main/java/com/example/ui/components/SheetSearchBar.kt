package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.PennywiseTheme
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary


@Composable
fun SheetSearchBar(
    query: String,
    onQueryChanged: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 16.dp)
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceElevated)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.04f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Search,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )
        BasicTextField(
            value = query,
            onValueChange = onQueryChanged,
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = TextPrimary
            ),
            decorationBox = { innerTextField ->
                if (query.isEmpty()) {
                    Text(
                        text = "Search categories",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
                innerTextField()
            },
            modifier = Modifier.weight(1f)
        )
        // Clear button — only when typing
        if (query.isNotEmpty()) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Clear",
                tint = TextSecondary,
                modifier = Modifier
                    .size(18.dp)
                    .clickable { onQueryChanged("") }
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF141414
)
@Composable
private fun SheetSearchBarTypingPreview() {
    PennywiseTheme {
        Box(modifier = Modifier.padding(vertical = 8.dp)) {
            SheetSearchBar(
                query = "Food",
                onQueryChanged = {}
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF141414
)
@Composable
private fun SheetSearchBarPreview() {
    PennywiseTheme {
        Box(modifier = Modifier.padding(vertical = 8.dp)) {
            SheetSearchBar(
                query = "",
                onQueryChanged = {}
            )
        }
    }
}