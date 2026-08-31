package com.example.capacita_projeto_final.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.capacita_projeto_final.ui.theme.HigMetrics
import com.example.capacita_projeto_final.ui.theme.HigShapes
import com.example.capacita_projeto_final.ui.theme.HigTheme

// MARK: - Section

@Composable
fun HigListSection(
    modifier: Modifier = Modifier,
    header: String? = null,
    footer: String? = null,
    footerColor: Color? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = HigTheme.colors
    Column(modifier.fillMaxWidth()) {
        header?.let {
            Text(
                modifier = Modifier.padding(
                    start = HigMetrics.contentMargin,
                    end = HigMetrics.contentMargin,
                    bottom = 7.dp,
                ),
                text = it,
                style = HigTheme.typography.footnote,
                color = colors.secondaryLabel,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(HigShapes.group)
                .background(colors.secondaryGroupedBackground),
            content = content,
        )
        footer?.let {
            Text(
                modifier = Modifier.padding(
                    start = HigMetrics.contentMargin,
                    end = HigMetrics.contentMargin,
                    top = 7.dp,
                ),
                text = it,
                style = HigTheme.typography.footnote,
                color = footerColor ?: colors.secondaryLabel,
            )
        }
    }
}

// MARK: - Separator

@Composable
fun HigRowSeparator(startInset: Dp = HigMetrics.contentMargin) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(start = startInset)
            .height(HigMetrics.separatorThickness)
            .background(HigTheme.colors.separator),
    )
}

// MARK: - Rows

@Composable
fun HigRow(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onClickLabel: String? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val base = modifier
        .fillMaxWidth()
        .then(
            if (onClick == null) {
                Modifier
            } else {
                Modifier.clickable(onClickLabel = onClickLabel, role = Role.Button, onClick = onClick)
            },
        )
        .defaultMinSize(minHeight = HigMetrics.minimumTouchTarget)
        .padding(
            horizontal = HigMetrics.contentMargin,
            vertical = HigMetrics.rowVerticalPadding,
        )
    Row(
        modifier = base,
        horizontalArrangement = Arrangement.spacedBy(HigMetrics.rowSpacing),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

@Composable
fun HigValueRow(label: String, value: String, modifier: Modifier = Modifier) {
    HigRow(modifier) {
        Text(
            text = label,
            style = HigTheme.typography.body,
            color = HigTheme.colors.label,
        )
        Box(Modifier.weight(1f))
        Text(
            text = value,
            style = HigTheme.typography.body,
            color = HigTheme.colors.secondaryLabel,
        )
    }
}

@Composable
fun HigDisclosureIndicator() {
    HigChevron(
        direction = HigChevronDirection.Forward,
        tint = HigTheme.colors.tertiaryLabel,
        height = 13.dp,
        strokeWidth = 2.dp,
    )
}
