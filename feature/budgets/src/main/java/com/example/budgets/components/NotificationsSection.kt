package com.example.budgets.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.ui.theme.Accent
import com.example.ui.theme.Border
import com.example.ui.theme.Income
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.PennywiseTheme
import com.example.ui.theme.Surface
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun NotificationsSection(
    alertsEnabled: Boolean,
    alertThreshold: Float,
    onAlertsToggled: (Boolean) -> Unit,
    onThresholdChanged: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Section label
        Text(
            text = "NOTIFICATIONS",
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = 1.sp,
            color = TextSecondary
        )

        // Toggle row — Spending alerts
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Surface)
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.04f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Spending alerts",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
                Text(
                    text = "Get notified when approaching limit",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
            Switch(
                checked = alertsEnabled,
                onCheckedChange = onAlertsToggled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Income,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = SurfaceElevated,
                    uncheckedBorderColor = Border
                )
            )
        }

        // Slider row — only visible when alerts enabled
        if (alertsEnabled) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Surface)
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.04f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Label + current value
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Warn me at",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "${(alertThreshold * 100).toInt()}%",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Accent
                    )
                }

                // Slider — 50% to 100%
                Slider(
                    value = alertThreshold,
                    onValueChange = onThresholdChanged,
                    valueRange = 0.5f..1.0f,
                    steps = 4, // 50, 60, 70, 80, 90, 100 — 4 steps between
                    colors = SliderDefaults.colors(
                        thumbColor = Accent,
                        activeTrackColor = Accent,
                        inactiveTrackColor = Border,
                        activeTickColor = Color.Transparent,
                        inactiveTickColor = Color.Transparent
                    )
                )

                // Tick labels row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("50%", "60%", "70%", "80%", "90%", "100%").forEach { label ->
                        Text(
                            text = label,
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A
)
@Composable
private fun NotificationsSectionEnabledPreview() {
    PennywiseTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            NotificationsSection(
                alertsEnabled = true,
                alertThreshold = 0.8f,
                onAlertsToggled = {},
                onThresholdChanged = {}
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A
)
@Composable
private fun NotificationsSectionDisabledPreview() {
    PennywiseTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            NotificationsSection(
                alertsEnabled = false,
                alertThreshold = 0.8f,
                onAlertsToggled = {},
                onThresholdChanged = {}
            )
        }
    }
}