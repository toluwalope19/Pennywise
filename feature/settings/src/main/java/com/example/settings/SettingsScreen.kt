package com.example.settings


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Summarize
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.settings.components.ProfileSection
import com.example.settings.components.SectionLabel
import com.example.settings.components.SettingsDivider
import com.example.settings.components.SettingsGroupCard
import com.example.settings.components.SettingsRow
import com.example.settings.components.SettingsSingleColumnLayout
import com.example.settings.components.SettingsSwitch
import com.example.settings.components.SettingsTabletLayout
import com.example.settings.components.TrailingChevron
import com.example.settings.components.TrailingText
import com.example.ui.PennywiseWindowLayout
import com.example.ui.theme.Background
import com.example.ui.theme.Expense
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.PennywiseTheme
import com.example.ui.theme.TextPrimary
import com.example.ui.utils.shareCSV

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    windowLayout: PennywiseWindowLayout = PennywiseWindowLayout.PhonePortrait,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SettingsUiEffect.NavigateBack -> onNavigateBack()
                is SettingsUiEffect.ExportCSV -> {
                    shareCSV(context, effect.csv) // ← handle here
                }
                is SettingsUiEffect.ShowError -> { /* Snackbar later */ }
            }
        }
    }

    SettingsContent(
        state = state,
        windowLayout = windowLayout,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContent(
    state: SettingsUiState,
    windowLayout: PennywiseWindowLayout,
    onEvent: (SettingsUiEvent) -> Unit
) {
    Scaffold(
        containerColor = Background,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = WindowInsets(top = 0),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Background
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { onEvent(SettingsUiEvent.OnBackClicked) }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                title = {
                    Text(
                        text = "Settings",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                }
            )
        }
    ) { paddingValues ->

        val isWideLayout = windowLayout is PennywiseWindowLayout.TabletLandscape ||
                windowLayout is PennywiseWindowLayout.Foldable

        if (isWideLayout) {
            SettingsTabletLayout(
                state = state,
                onEvent = onEvent,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            SettingsSingleColumnLayout(
                state = state,
                onEvent = onEvent,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF141414)
@Composable
private fun ProfileSectionPreview() {
    PennywiseTheme {
        ProfileSection(
            state = SettingsUiState()
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
private fun SettingsGroupCardPreview() {
    PennywiseTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            SettingsGroupCard {
                SettingsRow(
                    icon = Icons.Rounded.Payments,
                    iconGradient = Brush.linearGradient(
                        listOf(Color(0xFFFFD25A), Color(0xFFE8A625))
                    ),
                    label = "Currency",
                    trailing = { TrailingText("₦ Naira") },
                    onClick = {}
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Rounded.DarkMode,
                    iconGradient = Brush.linearGradient(
                        listOf(Color(0xFF4FD1FF), Color(0xFF2680E8))
                    ),
                    label = "Theme",
                    trailing = { TrailingText("Dark") },
                    onClick = {}
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A,
    device = "spec:width=390dp,height=844dp,dpi=420"
)
@Composable
private fun SettingsScreenPreview() {
    PennywiseTheme {
        SettingsContent(
            state = SettingsUiState(),
            onEvent = {},
            windowLayout = PennywiseWindowLayout.PhonePortrait,
        )
    }
}