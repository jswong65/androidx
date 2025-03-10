/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.wear.compose.foundation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import kotlin.math.min

/**
 * Used to cache computations and objects with expensive construction (Android's Paint & Path)
 */
internal actual class CurvedTextDelegate {
    private var text: String = ""
    private var clockwise: Boolean = true
    private var fontSizePx: Float = 0f
    private var arcPaddingPx: ArcPaddingPx = ArcPaddingPx(0f, 0f, 0f, 0f)

    actual var textWidth by mutableStateOf(0f)
    actual var textHeight by mutableStateOf(0f)
    actual var baseLinePosition = 0f

    private val paint = android.graphics.Paint().apply { isAntiAlias = true }
    private val backgroundPath = android.graphics.Path()
    private val textPath = android.graphics.Path()

    var lastSize: Size? = null

    actual fun updateIfNeeded(
        text: String,
        clockwise: Boolean,
        fontSizePx: Float,
        arcPaddingPx: ArcPaddingPx
    ) {
        if (
            text != this.text ||
            clockwise != this.clockwise ||
            fontSizePx != this.fontSizePx ||
            arcPaddingPx != this.arcPaddingPx
        ) {
            this.text = text
            this.clockwise = clockwise
            this.fontSizePx = fontSizePx
            this.arcPaddingPx = arcPaddingPx
            doUpdate()
            lastSize = null // Ensure paths are recomputed
        }
    }

    private fun doUpdate() {
        paint.textSize = fontSizePx

        val rect = android.graphics.Rect()
        paint.getTextBounds(text, 0, text.length, rect)

        textWidth = rect.width() + arcPaddingPx.before + arcPaddingPx.after
        textHeight = -paint.fontMetrics.top + paint.fontMetrics.bottom +
            arcPaddingPx.inner + arcPaddingPx.outer
        baseLinePosition = arcPaddingPx.outer +
            if (clockwise) -paint.fontMetrics.top else paint.fontMetrics.bottom
    }

    private fun updatePathsIfNeeded(size: Size) {
        if (size != lastSize) {
            lastSize = size

            val clockwiseFactor = if (clockwise) 1f else -1f

            val outerRadius = min(size.width, size.height) / 2f
            val innerRadius = outerRadius - textHeight
            val baselineRadius = outerRadius - baseLinePosition

            val sweepDegree = (textWidth / baselineRadius)
                .toDegrees()
                .coerceAtMost(360f)
            val paddingBeforeAsAngle = (arcPaddingPx.before / baselineRadius)
                .toDegrees()
                .coerceAtMost(360f)

            val centerX = size.width / 2f
            val centerY = size.height / 2f

            backgroundPath.reset()
            backgroundPath.arcTo(
                centerX - outerRadius,
                centerY - outerRadius,
                centerX + outerRadius,
                centerY + outerRadius,
                anchor - clockwiseFactor * sweepDegree / 2,
                clockwiseFactor * sweepDegree, false
            )
            backgroundPath.arcTo(
                centerX - innerRadius,
                centerY - innerRadius,
                centerX + innerRadius,
                centerY + innerRadius,
                anchor + clockwiseFactor * sweepDegree / 2,
                -clockwiseFactor * sweepDegree, false
            )
            backgroundPath.close()

            textPath.reset()
            textPath.addArc(
                centerX - baselineRadius,
                centerY - baselineRadius,
                centerX + baselineRadius,
                centerY + baselineRadius,
                anchor - clockwiseFactor * (sweepDegree / 2 - paddingBeforeAsAngle),
                clockwiseFactor * sweepDegree
            )
        }
    }

    actual fun doDraw(canvas: Canvas, size: Size, color: Color, background: Color) {
        updatePathsIfNeeded(size)

        if (background.isSpecified && background != Color.Transparent) {
            paint.color = background.toArgb()
            canvas.nativeCanvas.drawPath(backgroundPath, paint)
        }

        paint.color = color.toArgb()
        canvas.nativeCanvas.drawTextOnPath(text, textPath, 0f, 0f, paint)
    }
}

// We always draw curved text centered at the top, the CurvedRow will rotate us to the
// desired angle.
// Note that this is in the Angle system used by the arc drawing functions: 0 is 3 o clock,
// increasing clockwise.
private const val anchor = 270f
