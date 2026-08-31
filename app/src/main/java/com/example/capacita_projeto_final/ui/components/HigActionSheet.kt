package com.example.capacita_projeto_final.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.capacita_projeto_final.ui.theme.HigMetrics
import com.example.capacita_projeto_final.ui.theme.HigShapes
import com.example.capacita_projeto_final.ui.theme.HigTheme

// MARK: - Action

data class HigAction(
    val title: String,
    val destructive: Boolean = false,
    val onSelect: () -> Unit,
)

// MARK: - Action sheet

@Composable
fun HigActionSheet(
    title: String?,
    message: String?,
    actions: List<HigAction>,
    cancelTitle: String,
    onDismissRequest: () -> Unit,
) {
    val colors = HigTheme.colors
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismissRequest,
                ),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(HigMetrics.elementSpacing),
                verticalArrangement = Arrangement.spacedBy(HigMetrics.elementSpacing),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(HigShapes.alert)
                        .background(colors.secondaryGroupedBackground),
                ) {
                    if (title != null || message != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(HigMetrics.contentMargin),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            title?.let {
                                Text(
                                    text = it,
                                    style = HigTheme.typography.footnoteEmphasized,
                                    color = colors.secondaryLabel,
                                    textAlign = TextAlign.Center,
                                )
                            }
                            message?.let {
                                Text(
                                    text = it,
                                    style = HigTheme.typography.footnote,
                                    color = colors.secondaryLabel,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                        HigRowSeparator(startInset = 0.dp)
                    }
                    actions.forEachIndexed { index, action ->
                        if (index > 0) HigRowSeparator(startInset = 0.dp)
                        SheetAction(
                            title = action.title,
                            tint = if (action.destructive) colors.destructive else colors.accent,
                            onSelect = action.onSelect,
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(HigShapes.alert)
                        .background(colors.secondaryGroupedBackground),
                ) {
                    SheetAction(
                        title = cancelTitle,
                        tint = colors.accent,
                        emphasized = true,
                        onSelect = onDismissRequest,
                    )
                }
            }
        }
    }
}

@Composable
private fun SheetAction(
    title: String,
    tint: Color,
    emphasized: Boolean = false,
    onSelect: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onSelect)
            .defaultMinSize(minHeight = 57.dp)
            .padding(horizontal = HigMetrics.contentMargin),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            style = if (emphasized) {
                HigTheme.typography.bodyEmphasized
            } else {
                HigTheme.typography.body
            },
            color = tint,
        )
    }
}
