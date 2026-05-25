package com.example.pennywise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import com.example.domain.usecase.preference.GetOnboardingSeenUseCase
import com.example.pennywise.navigation.PennywiseNavGraph
import com.example.pennywise.navigation.Screen
import com.example.ui.computeWindowLayout
import com.example.ui.theme.PennywiseTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var getOnboardingSeen: GetOnboardingSeenUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.dark(
                android.graphics.Color.TRANSPARENT
            )
        )
        setContent {
            PennywiseTheme {

                // Reactive — updates when window size changes (rotation, foldable)
                val windowSizeClass by rememberUpdatedState(
                    WindowSizeClass.compute(
                        with(LocalDensity.current) {
                            resources.configuration.screenWidthDp.toFloat()
                        },
                        with(LocalDensity.current) {
                            resources.configuration.screenHeightDp.toFloat()
                        }
                    )
                )


                // WindowLayoutInfo — reactive to fold/unfold state
                val windowLayoutInfo by WindowInfoTracker
                    .getOrCreate(this@MainActivity)
                    .windowLayoutInfo(this@MainActivity)
                    .collectAsStateWithLifecycle(
                        initialValue = null
                    )

                // Extract FoldingFeature if present
                val foldingFeature = windowLayoutInfo
                    ?.displayFeatures
                    ?.filterIsInstance<FoldingFeature>()
                    ?.firstOrNull()

                // Combine into single PennywiseWindowLayout
                val windowLayout by rememberUpdatedState(
                    computeWindowLayout(
                        windowSizeClass = windowSizeClass,
                        foldingFeature = foldingFeature
                    )
                )

                val hasSeenOnboarding by getOnboardingSeen()
                    .collectAsStateWithLifecycle<Boolean?>(initialValue = null)

                if (hasSeenOnboarding != null) {
                    val navController = rememberNavController()
                    PennywiseNavGraph(
                        navController = navController,
                        windowLayout = windowLayout,
                        startDestination = if (hasSeenOnboarding == true) {
                            Screen.Dashboard.route
                        } else {
                            Screen.Onboarding.route
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PennywiseTheme {
        Greeting("Android")
    }
}