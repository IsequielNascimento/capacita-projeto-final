package com.example.capacita_projeto_final.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// MARK: - Chevron

enum class HigChevronDirection { Backward, Forward }

@Composable
fun HigChevron(
    direction: HigChevronDirection,
    tint: Color,
    modifier: Modifier = Modifier,
    height: Dp = 13.dp,
    strokeWidth: Dp = 2.dp,
) {
    Canvas(modifier.size(width = height * 0.6f, height = height)) {
        val inset = strokeWidth.toPx() / 2f
        val top = Offset(if (direction == HigChevronDirection.Forward) inset else size.width - inset, inset)
        val middle = Offset(
            if (direction == HigChevronDirection.Forward) size.width - inset else inset,
            size.height / 2f,
        )
        val bottom = Offset(top.x, size.height - inset)
        drawPath(
            path = Path().apply {
                moveTo(top.x, top.y)
                lineTo(middle.x, middle.y)
                lineTo(bottom.x, bottom.y)
            },
            color = tint,
            style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

// MARK: - Checkmark

@Composable
fun HigCheckmark(
    tint: Color,
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    strokeWidth: Dp = 2.dp,
) {
    Canvas(modifier.size(size)) {
        drawPath(
            path = Path().apply {
                moveTo(this@Canvas.size.width * 0.14f, this@Canvas.size.height * 0.53f)
                lineTo(this@Canvas.size.width * 0.39f, this@Canvas.size.height * 0.78f)
                lineTo(this@Canvas.size.width * 0.87f, this@Canvas.size.height * 0.24f)
            },
            color = tint,
            style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

// MARK: - Tab bar symbols

@Composable
fun HigListBulletSymbol(tint: Color, modifier: Modifier = Modifier, size: Dp = 25.dp) {
    Canvas(modifier.size(size)) {
        val stroke = this.size.width * 0.085f
        val dotRadius = this.size.width * 0.062f
        listOf(0.24f, 0.5f, 0.76f).forEach { fraction ->
            drawCircle(tint, dotRadius, Offset(this.size.width * 0.14f, this.size.height * fraction))
            drawLine(
                color = tint,
                start = Offset(this.size.width * 0.34f, this.size.height * fraction),
                end = Offset(this.size.width * 0.9f, this.size.height * fraction),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
fun HigMapSymbol(tint: Color, modifier: Modifier = Modifier, size: Dp = 25.dp) {
    Canvas(modifier.size(size)) {
        val stroke = this.size.width * 0.085f
        val width = this.size.width
        val height = this.size.height
        val left = stroke / 2f
        val right = width - stroke / 2f
        val third = width / 3f
        val high = height * 0.2f
        val low = height * 0.3f
        val bottomHigh = height * 0.7f
        val bottomLow = height * 0.8f

        drawPath(
            path = Path().apply {
                moveTo(left, low)
                lineTo(third, high)
                lineTo(third * 2f, low)
                lineTo(right, high)
                lineTo(right, bottomHigh)
                lineTo(third * 2f, bottomLow)
                lineTo(third, bottomHigh)
                lineTo(left, bottomLow)
                close()
            },
            color = tint,
            style = Stroke(stroke, join = StrokeJoin.Round),
        )
        drawLine(
            color = tint,
            start = Offset(third, high),
            end = Offset(third, bottomHigh),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(third * 2f, low),
            end = Offset(third * 2f, bottomLow),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
fun HigSyncSymbol(tint: Color, modifier: Modifier = Modifier, size: Dp = 25.dp) {
    Canvas(modifier.size(size)) {
        val stroke = this.size.width * 0.085f
        val inset = this.size.width * 0.18f
        drawArc(
            color = tint,
            startAngle = 35f,
            sweepAngle = 285f,
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = Size(this.size.width - inset * 2f, this.size.height - inset * 2f),
            style = Stroke(stroke, cap = StrokeCap.Round),
        )
        val head = this.size.width * 0.13f
        val anchor = Offset(this.size.width * 0.82f, this.size.height * 0.28f)
        drawPath(
            path = Path().apply {
                moveTo(anchor.x - head, anchor.y - head * 0.2f)
                lineTo(anchor.x + head * 0.4f, anchor.y - head)
                lineTo(anchor.x + head * 0.2f, anchor.y + head)
                close()
            },
            color = tint,
        )
    }
}

// MARK: - Evidence symbols

@Composable
fun HigCameraSymbol(tint: Color, modifier: Modifier = Modifier, size: Dp = 22.dp) {
    Canvas(modifier.size(size)) {
        val stroke = this.size.width * 0.085f
        val bodyTop = this.size.height * 0.3f
        val bodyBottom = this.size.height * 0.8f
        drawRoundedBody(tint, stroke, bodyTop, bodyBottom)
        drawLine(
            color = tint,
            start = Offset(this.size.width * 0.36f, bodyTop),
            end = Offset(this.size.width * 0.44f, this.size.height * 0.21f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(this.size.width * 0.64f, bodyTop),
            end = Offset(this.size.width * 0.56f, this.size.height * 0.21f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawCircle(
            color = tint,
            radius = this.size.width * 0.15f,
            center = Offset(this.size.width / 2f, (bodyTop + bodyBottom) / 2f),
            style = Stroke(stroke),
        )
    }
}

private fun DrawScope.drawRoundedBody(tint: Color, stroke: Float, top: Float, bottom: Float) {
    val rect = Rect(stroke / 2f, top, size.width - stroke / 2f, bottom)
    drawPath(
        path = Path().apply {
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    rect,
                    androidx.compose.ui.geometry.CornerRadius(size.width * 0.14f),
                ),
            )
        },
        color = tint,
        style = Stroke(stroke),
    )
}

@Composable
fun HigLocationSymbol(tint: Color, modifier: Modifier = Modifier, size: Dp = 22.dp) {
    Canvas(modifier.size(size)) {
        drawPath(
            path = Path().apply {
                moveTo(this@Canvas.size.width * 0.9f, this@Canvas.size.height * 0.12f)
                lineTo(this@Canvas.size.width * 0.1f, this@Canvas.size.height * 0.46f)
                lineTo(this@Canvas.size.width * 0.47f, this@Canvas.size.height * 0.55f)
                lineTo(this@Canvas.size.width * 0.56f, this@Canvas.size.height * 0.9f)
                close()
            },
            color = tint,
        )
    }
}

// MARK: - Warning

@Composable
fun HigExclamationSymbol(tint: Color, modifier: Modifier = Modifier, size: Dp = 22.dp) {
    Canvas(modifier.size(size)) {
        val stroke = this.size.width * 0.1f
        drawCircle(tint, this.size.width / 2f - stroke / 2f, style = Stroke(stroke))
        drawLine(
            color = tint,
            start = Offset(this.size.width / 2f, this.size.height * 0.26f),
            end = Offset(this.size.width / 2f, this.size.height * 0.58f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawCircle(tint, stroke * 0.62f, Offset(this.size.width / 2f, this.size.height * 0.74f))
    }
}
