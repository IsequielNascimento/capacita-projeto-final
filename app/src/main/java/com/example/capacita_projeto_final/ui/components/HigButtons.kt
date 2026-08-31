package com.example.capacita_projeto_final.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.example.capacita_projeto_final.ui.theme.HigMetrics
import com.example.capacita_projeto_final.ui.theme.HigShapes
import com.example.capacita_projeto_final.ui.theme.HigTheme

// MARK: - Prominent button

@Composable
fun HigProminentButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    inProgress: Boolean = false,
    progressLabel: String? = null,
    tint: Color? = null,
    onTint: Color? = null,
) {
    val colors = HigTheme.colors
    val background = tint ?: colors.accentFill
    val foreground = onTint ?: colors.onAccentFill
    val active = enabled && !inProgress

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(HigShapes.control)
            .background(if (active) background else background.copy(alpha = 0.4f))
            .clickable(enabled = active, role = Role.Button, onClick = onClick)
            .defaultMinSize(minHeight = HigMetrics.controlHeight)
            .semantics {
                if (inProgress && progressLabel != null) stateDescription = progressLabel
            },
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = HigMetrics.contentMargin, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(HigMetrics.elementSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (inProgress) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = foreground,
                    strokeWidth = 2.dp,
                )
            }
            Text(
                text = title,
                style = HigTheme.typography.bodyEmphasized,
                color = foreground,
            )
        }
    }
}

// MARK: - Bordered button

@Composable
fun HigBorderedButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    inProgress: Boolean = false,
    progressLabel: String? = null,
    leading: (@Composable () -> Unit)? = null,
) {
    val colors = HigTheme.colors
    val active = enabled && !inProgress

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(HigShapes.control)
            .background(colors.fill)
            .border(HigMetrics.separatorThickness, colors.separator, HigShapes.control)
            .clickable(enabled = active, role = Role.Button, onClick = onClick)
            .defaultMinSize(minHeight = HigMetrics.controlHeight)
            .semantics {
                if (inProgress && progressLabel != null) stateDescription = progressLabel
            }
            .alpha(if (active) 1f else 0.5f),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = HigMetrics.contentMargin, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(HigMetrics.elementSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (inProgress) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = colors.accent,
                    strokeWidth = 2.dp,
                )
            } else {
                leading?.invoke()
            }
            Text(
                text = title,
                style = HigTheme.typography.body,
                color = colors.accent,
            )
        }
    }
}

// MARK: - Plain button

@Composable
fun HigPlainButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color? = null,
    emphasized: Boolean = false,
    enabled: Boolean = true,
) {
    Text(
        modifier = modifier
            .clip(HigShapes.badge)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .alpha(if (enabled) 1f else 0.4f)
            .defaultMinSize(minHeight = HigMetrics.minimumTouchTarget)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        text = title,
        style = if (emphasized) HigTheme.typography.bodyEmphasized else HigTheme.typography.body,
        color = tint ?: HigTheme.colors.accent,
    )
}
