package com.example.onboarding


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.Background
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.PennywiseTheme
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary


@Composable
fun OnboardingScreen(
    onNavigateToDashboard: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                OnboardingUiEffect.NavigateToDashboard -> onNavigateToDashboard()
            }
        }
    }

    OnboardingContent(
        onGetStarted = { viewModel.onEvent(OnboardingUiEvent.OnGetStarted) }
    )
}

@Composable
private fun OnboardingContent(
    onGetStarted: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Hero image — fills top portion and bleeds behind status bar
        Image(
            painter = painterResource(id = R.drawable.onboarding_hero),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.62f)
                .align(Alignment.TopCenter)
        )

        // Content anchored to bottom
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp)
                .padding(
                    bottom = WindowInsets.navigationBars
                        .asPaddingValues()
                        .calculateBottomPadding() + 24.dp
                ),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Control over your finances is in your hands",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    lineHeight = 38.sp,
                    letterSpacing = (-0.8).sp,
                    color = TextPrimary
                )
                Text(
                    text = "Your path to conscious spending and control starts here",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = TextSecondary
                )
            }
            GetStartedButton(onClick = onGetStarted)
        }
    }
}

// ── Get started button ─────────────────────────────────────────────────────

@Composable
private fun GetStartedButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = TextPrimary,
            contentColor = Background
        )
    ) {
        Text(
            text = "Get started",
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
    }
}


@Preview(
    name = "Onboarding Screen",
    showBackground = true,
    backgroundColor = 0xFF0A0A0A,
    device = "spec:width=390dp,height=844dp,dpi=420"
)
@Composable
private fun OnboardingScreenPreview() {
    PennywiseTheme {
        OnboardingContent(
            onGetStarted = {}
        )
    }
}


@Preview(
    name = "Onboarding Screen",
    showBackground = true,
    backgroundColor = 0xFF0A0A0A,
    device = "spec:width=390dp,height=844dp,dpi=420"
)
@Composable
private fun GetStartedButtonPreview() {
    PennywiseTheme {
        Box(modifier = Modifier.padding(24.dp)) {
            GetStartedButton(onClick = {})
        }
    }
}
