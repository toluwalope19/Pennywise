package com.example.ui.utils


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.launch
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable


fun Modifier.pressScale(
    scaleDown: Float = 0.96f
): Modifier = composed {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    this.pointerInput(Unit) {
        while (true) {
            awaitPointerEventScope {
                // Wait for finger down
                awaitFirstDown(requireUnconsumed = false)
                scope.launch {
                    scale.animateTo(
                        targetValue = scaleDown,
                        animationSpec = spring(
                            dampingRatio = 0.6f,
                            stiffness = 800f
                        )
                    )
                }

                // Wait for finger up or cancel
                waitForUpOrCancellation()
                scope.launch {
                    scale.animateTo(
                        targetValue = 1f,
                        animationSpec = spring(
                            dampingRatio = 0.4f, // ← bouncier on release
                            stiffness = 600f
                        )
                    )
                }
            }
        }
    }.scale(scale.value)
}



@Composable
fun StaggeredAnimatedItem(
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Cap delay at 300ms so long lists don't wait forever
    val delay = (index * 50).coerceAtMost(300)

    val visibleState = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }

    AnimatedVisibility(
        visibleState = visibleState,
        modifier = modifier,
        enter = slideInVertically(
            initialOffsetY = { it / 4 }, // ← slides up by 25% of its height
            animationSpec = tween(
                durationMillis = 300,
                delayMillis = delay
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = 300,
                delayMillis = delay
            )
        )
    ) {
        content()
    }
}