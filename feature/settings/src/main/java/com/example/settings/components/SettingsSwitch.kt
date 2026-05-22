package com.example.settings.components

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.Border
import com.example.ui.theme.Income
import com.example.ui.theme.Surface

@Composable
fun SettingsSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = Income,
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = Surface,
            uncheckedBorderColor = Border
        )
    )
}