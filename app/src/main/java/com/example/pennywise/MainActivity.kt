package com.example.pennywise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.domain.usecase.preference.GetOnboardingSeenUseCase
import com.example.pennywise.navigation.PennywiseNavGraph
import com.example.pennywise.navigation.Screen
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
                val hasSeenOnboarding by getOnboardingSeen()
                    .collectAsStateWithLifecycle<Boolean?>(initialValue = null)

                if (hasSeenOnboarding != null) {
                    val navController = rememberNavController()
                    PennywiseNavGraph(
                        navController = navController,
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