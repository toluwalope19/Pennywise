package com.example.ui


import androidx.window.core.layout.WindowSizeClass
import androidx.window.layout.FoldingFeature

sealed class PennywiseWindowLayout {

    // Phone held upright — existing single column layout
    object PhonePortrait : PennywiseWindowLayout()

    // Phone rotated — two compact columns, bottom nav stays
    object PhoneLandscape : PennywiseWindowLayout()

    // Tablet held upright — nav rail + single wider column
    object TabletPortrait : PennywiseWindowLayout()

    // Tablet in landscape — nav rail + two equal columns
    object TabletLandscape : PennywiseWindowLayout()

    // Foldable device open flat in book mode
    // carries FoldingFeature so screens can get exact hinge bounds
    data class Foldable(
        val foldingFeature: FoldingFeature
    ) : PennywiseWindowLayout()
}


fun computeWindowLayout(
    windowSizeClass: WindowSizeClass,
    foldingFeature: FoldingFeature?
): PennywiseWindowLayout {

    val isCompactHeight = !windowSizeClass.isHeightAtLeastBreakpoint(
        WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND // 480dp
    )

    val isMediumWidth = windowSizeClass.isWidthAtLeastBreakpoint(
        WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND  // 600dp
    ) && !windowSizeClass.isWidthAtLeastBreakpoint(
        WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND // 840dp
    )

    val isExpandedWidth = windowSizeClass.isWidthAtLeastBreakpoint(
        WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND // 840dp
    )

    // Foldable — must be expanded width, flat, vertical hinge (book mode)
    if (
        foldingFeature != null &&
        foldingFeature.state == FoldingFeature.State.FLAT &&
        foldingFeature.orientation == FoldingFeature.Orientation.VERTICAL &&
        isExpandedWidth
    ) {
        return PennywiseWindowLayout.Foldable(foldingFeature)
    }

    return when {
        // Phone landscape — wide but short
        isExpandedWidth && isCompactHeight -> PennywiseWindowLayout.PhoneLandscape
        isMediumWidth && isCompactHeight   -> PennywiseWindowLayout.PhoneLandscape

        // Tablet landscape — wide and tall, no fold
        isExpandedWidth                    -> PennywiseWindowLayout.TabletLandscape

        // Tablet portrait — medium width and tall
        isMediumWidth                      -> PennywiseWindowLayout.TabletPortrait



        // Default — phone portrait
        else                               -> PennywiseWindowLayout.PhonePortrait
    }
}