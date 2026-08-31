package com.example.capacita_projeto_final.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.capacita_projeto_final.ui.theme.HigMetrics
import com.example.capacita_projeto_final.ui.theme.HigShapes
import com.example.capacita_projeto_final.ui.theme.HigTheme

@Composable
fun HigAlert(
    title: String,
    message: String,
    confirmTitle: String,
    onConfirm: () -> Unit,
    dismissTitle: String,
    onDismissRequest: () -> Unit,
    confirmIsDestructive: Boolean = false,
) {
    val colors = HigTheme.colors
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .width(270.dp)
                .clip(HigShapes.alert)
                .background(colors.secondaryGroupedBackground),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HigMetrics.contentMargin, vertical = 19.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = title,
                    style = HigTheme.typography.headline,
                    color = colors.label,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = message,
                    style = HigTheme.typography.footnote,
                    color = colors.label,
                    textAlign = TextAlign.Center,
                )
            }
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(HigMetrics.separatorThickness)
                    .background(colors.separator),
            )
            Row(Modifier.height(IntrinsicSize.Min)) {
                AlertAction(
                    modifier = Modifier.weight(1f),
                    title = dismissTitle,
                    tint = colors.accent,
                    onSelect = onDismissRequest,
                )
                Box(
                    Modifier
                        .width(HigMetrics.separatorThickness)
                        .fillMaxHeight()
                        .background(colors.separator),
                )
                AlertAction(
                    modifier = Modifier.weight(1f),
                    title = confirmTitle,
                    tint = if (confirmIsDestructive) colors.destructive else colors.accent,
                    emphasized = true,
                    onSelect = onConfirm,
                )
            }
        }
    }
}

@Composable
private fun AlertAction(
    modifier: Modifier,
    title: String,
    tint: Color,
    emphasized: Boolean = false,
    onSelect: () -> Unit,
) {
    Box(
        modifier = modifier
            .clickable(role = Role.Button, onClick = onSelect)
            .defaultMinSize(minHeight = HigMetrics.minimumTouchTarget)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            style = if (emphasized) HigTheme.typography.bodyEmphasized else HigTheme.typography.body,
            color = tint,
            textAlign = TextAlign.Center,
        )
    }
}
