package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.PennywiseTheme
import com.example.ui.theme.Surface
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCategorySheet(
    onDismiss: () -> Unit,
    onCategoryCreated: (name: String, color: Color, icon: ImageVector) -> Unit
) {
    // Local state — everything lives here until "Create" is tapped
    // Replace String state with proper types
    var name by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(Color(0xFFFF8A3D)) }
    var selectedIcon by remember { mutableStateOf(Icons.Rounded.Restaurant) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 6.dp)
                    .width(32.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color.White.copy(alpha = 0.2f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            CreateCategoryHeader(onDismiss = onDismiss)

            CategoryPreviewCard(
                name = name,
                selectedColor = selectedColor,
                selectedIcon = selectedIcon
            )

            CategoryNameField(
                name = name,
                onNameChanged = { name = it }
            )

            ColorSwatchGrid(
                selectedColor = selectedColor,
                onColorSelected = { selectedColor = it }
            )

            IconPickerGrid(
                selectedIcon = selectedIcon,
                onIconSelected = { selectedIcon = it }
            )

            CreateCategoryButtons(
                name = name,
                onCancel = onDismiss,
                onCreate = {
                    onCategoryCreated(name, selectedColor, selectedIcon)
                }
            )

        }
    }
}

// ── Header ─────────────────────────────────────────────────────────────────

@Composable
fun CreateCategoryHeader(onDismiss: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 4.dp, bottom = 20.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "New category",
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            letterSpacing = (-0.3).sp,
            color = TextPrimary
        )
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(SurfaceElevated)
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Close",
                tint = TextPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// ── Previews ───────────────────────────────────────────────────────────────

@Preview(
    showBackground = true,
    backgroundColor = 0xFF141414
)
@Composable
private fun CreateCategoryHeaderPreview() {
    PennywiseTheme {
        CreateCategoryHeader(onDismiss = {})
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A,
    device = "spec:width=390dp,height=844dp,dpi=420"
)
@Composable
private fun CreateCategorySheetPreview() {
    PennywiseTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(Surface)
                    .padding(bottom = 28.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 6.dp)
                        .width(32.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .align(Alignment.CenterHorizontally)
                )
                CreateCategoryHeader(onDismiss = {})

                // Placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Preview + Name + Colors + Icons coming next",
                        color = TextSecondary,
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}