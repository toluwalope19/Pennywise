package com.example.ui.components


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ── Public API ─────────────────────────────────────────────────────────────

/**
 * A single segment in the donut chart.
 *
 * @param value     The raw value — used to calculate percentage internally.
 *                  e.g. 214.84 for Food spending
 * @param color     The color of this segment
 * @param label     Optional label for accessibility
 */
data class DonutSegment(
    val value: Float,
    val color: Color,
    val label: String = ""
)

// ── Donut chart ────────────────────────────────────────────────────────────

/**
 * A fully generic, reusable segmented ring chart.
 * No dependencies on Pennywise domain models.
 * Safe to extract to a standalone library.
 *
 * @param segments          List of segments to draw
 * @param modifier          Modifier for the chart size and layout
 * @param chartSize         Overall diameter of the chart
 * @param strokeWidth       Thickness of the ring
 * @param gapAngle          Gap between segments in degrees
 * @param animated          Whether to animate segments sweeping in on first composition
 * @param animDurationMs    Duration of the sweep animation in milliseconds
 * @param centerContent     Composable slot rendered in the center of the ring
 */
@Composable
fun DonutChart(
    segments: List<DonutSegment>,
    modifier: Modifier = Modifier,
    chartSize: Dp = 240.dp,
    strokeWidth: Dp = 24.dp,
    gapAngle: Float = 4f,
    animated: Boolean = true,
    animDurationMs: Int = 800,
    centerContent: @Composable () -> Unit = {}
) {
    // Guard — nothing to draw
    if (segments.isEmpty()) return

    val total = segments.sumOf { it.value.toDouble() }.toFloat()
    if (total <= 0f) return

    // Calculate percentage for each segment
    val percentages = segments.map { it.value / total }

    // One animatable per segment
    val animatedSweeps = percentages.map { pct ->
        val targetSweep = pct * (360f - gapAngle * segments.size)
        val animatable = remember(pct) { Animatable(if (animated) 0f else targetSweep) }
        LaunchedEffect(pct) {
            animatable.animateTo(
                targetValue = targetSweep,
                animationSpec = tween(
                    durationMillis = animDurationMs,
                    easing = FastOutSlowInEasing
                )
            )
        }
        animatable.value
    }

    Box(
        modifier = modifier.size(chartSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(chartSize)) {
            val strokePx = strokeWidth.toPx()
            val diameter = size.minDimension - strokePx
            val radius = diameter / 2f
            val topLeft = Offset(
                x = (size.width - diameter) / 2f,
                y = (size.height - diameter) / 2f
            )
            val arcSize = Size(diameter, diameter)

            // Background guide ring — barely visible
            drawArc(
                color = Color.White.copy(alpha = 0.05f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(
                    width = strokePx,
                    cap = StrokeCap.Butt
                )
            )

            // Draw segments starting from top (-90°)
            var currentAngle = -90f

            segments.forEachIndexed { index, segment ->
                val sweep = animatedSweeps.getOrElse(index) { 0f }
                if (sweep > 0.5f) { // skip invisible segments
                    drawArc(
                        color = segment.color,
                        startAngle = currentAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(
                            width = strokePx,
                            cap = StrokeCap.Butt
                        )
                    )
                    // Advance angle — segment sweep + gap
                    currentAngle += sweep + gapAngle
                }
            }
        }

        // Center slot — caller decides what goes here
        centerContent()
    }
}