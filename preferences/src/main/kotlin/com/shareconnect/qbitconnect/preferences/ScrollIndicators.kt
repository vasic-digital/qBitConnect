/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.shareconnect.qbitconnect.preferences

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.material3.LocalContentColor
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

internal fun Modifier.verticalScrollIndicators(
    scrollableState: ScrollableState,
    reverseScrolling: Boolean = false,
    drawTopIndicator: Boolean = true,
    drawBottomIndicator: Boolean = true,
): Modifier =
    scrollIndicators(
        scrollableState,
        Orientation.Vertical,
        reverseScrolling,
        drawTopIndicator,
        drawBottomIndicator,
    )

private fun Modifier.scrollIndicators(
    scrollableState: ScrollableState,
    orientation: Orientation,
    reverseScrolling: Boolean = false,
    drawTopStartIndicator: Boolean = true,
    drawBottomEndIndicator: Boolean = true,
): Modifier =
    composed(
        debugInspectorInfo {
            name = "scrollIndicators"
            properties["orientation"] = orientation
            properties["reverseScrolling"] = reverseScrolling
            properties["scrollableState"] = scrollableState
            properties["drawTopStartIndicator"] = drawTopStartIndicator
            properties["drawBottomEndIndicator"] = drawBottomEndIndicator
        }
    ) {
        val layoutDirection = LocalLayoutDirection.current
        val reverseDirection =
            ScrollableDefaults.reverseDirection(layoutDirection, orientation, reverseScrolling)
        val drawTopLeftIndicator: Boolean
        val drawBottomRightIndicator: Boolean
        when (orientation) {
            Orientation.Vertical -> {
                drawTopLeftIndicator = drawTopStartIndicator
                drawBottomRightIndicator = drawBottomEndIndicator
            }

            Orientation.Horizontal -> {
                when (layoutDirection) {
                    LayoutDirection.Ltr -> {
                        drawTopLeftIndicator = drawTopStartIndicator
                        drawBottomRightIndicator = drawBottomEndIndicator
                    }

                    LayoutDirection.Rtl -> {
                        drawTopLeftIndicator = drawBottomEndIndicator
                        drawBottomRightIndicator = drawTopStartIndicator
                    }
                }
            }
        }
        val color = LocalContentColor.current.copy(alpha = ScrollIndicatorOpacity)
        val thickness = with(LocalDensity.current) { ScrollIndicatorThickness.toPx() }
        drawWithContent {
            drawContent()
            val indicatorSize =
                when (orientation) {
                    Orientation.Vertical -> Size(size.width, thickness)
                    Orientation.Horizontal -> Size(thickness, size.height)
                }
            if (drawTopLeftIndicator) {
                val canScrollUpLeft =
                    if (reverseDirection) {
                        scrollableState.canScrollBackward
                    } else {
                        scrollableState.canScrollForward
                    }
                if (canScrollUpLeft) {
                    drawRect(color = color, size = indicatorSize)
                }
            }
            if (drawBottomRightIndicator) {
                val canScrollDownRight =
                    if (reverseDirection) {
                        scrollableState.canScrollForward
                    } else {
                        scrollableState.canScrollBackward
                    }
                val topLeft =
                    when (orientation) {
                        Orientation.Vertical -> Offset(0f, size.height - thickness)
                        Orientation.Horizontal -> Offset(size.width - thickness, 0f)
                    }
                if (canScrollDownRight) {
                    drawRect(color = color, topLeft = topLeft, size = indicatorSize)
                }
            }
        }
    }

private const val ScrollIndicatorOpacity = 0.12f
private val ScrollIndicatorThickness = 1.dp
