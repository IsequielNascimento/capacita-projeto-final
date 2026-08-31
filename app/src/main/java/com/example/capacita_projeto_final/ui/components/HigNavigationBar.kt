package com.example.capacita_projeto_final.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.capacita_projeto_final.ui.theme.HigMetrics
import com.example.capacita_projeto_final.ui.theme.HigTheme

// MARK: - Collapse state

@Composable
fun rememberLargeTitleCollapsed(listState: LazyListState): Boolean {
    val collapsed by remember(listState) {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 24
        }
    }
    return collapsed
}

// MARK: - Navigation bar

@Composable
fun HigNavigationBar(
    title: String,
    modifier: Modifier = Modifier,
    backTitle: String? = null,
    backAccessibilityLabel: String? = null,
    onBack: (() -> Unit)? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    showsInlineTitle: Boolean = true,
    showsSeparator: Boolean = true,
) {
    val colors = HigTheme.colors
    val titleAlpha by animateFloatAsState(
        targetValue = if (showsInlineTitle) 1f else 0f,
        label = "inlineTitleAlpha",
    )
    val separatorAlpha by animateFloatAsState(
        targetValue = if (showsSeparator) 1f else 0f,
        label = "barSeparatorAlpha",
    )

    Column(
        modifier
            .fillMaxWidth()
            .background(colors.barBackground)
            .statusBarsPadding(),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(HigMetrics.barHeight),
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .widthIn(max = 200.dp)
                    .alpha(titleAlpha)
                    .semantics { heading() },
                text = title,
                style = HigTheme.typography.headline,
                color = colors.label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = HigMetrics.elementSpacing),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (onBack != null) {
                    BackControl(
                        title = backTitle,
                        accessibilityLabel = backAccessibilityLabel ?: backTitle ?: title,
                        onBack = onBack,
                    )
                }
                leading?.invoke()
            }
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = HigMetrics.elementSpacing),
                horizontalArrangement = Arrangement.spacedBy(HigMetrics.elementSpacing),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                trailing?.invoke()
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(HigMetrics.separatorThickness)
                .alpha(separatorAlpha)
                .background(colors.separator),
        )
    }
}

@Composable
private fun BackControl(title: String?, accessibilityLabel: String, onBack: () -> Unit) {
    val colors = HigTheme.colors
    Row(
        modifier = Modifier
            .clickable(role = Role.Button, onClick = onBack)
            .defaultMinSize(
                minWidth = HigMetrics.minimumTouchTarget,
                minHeight = HigMetrics.minimumTouchTarget,
            )
            .padding(horizontal = HigMetrics.elementSpacing)
            .semantics(mergeDescendants = true) { contentDescription = accessibilityLabel },
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HigChevron(
            direction = HigChevronDirection.Backward,
            tint = colors.accent,
            height = 17.dp,
            strokeWidth = 2.5.dp,
        )
        if (title != null) {
            Text(
                modifier = Modifier.widthIn(max = 110.dp),
                text = title,
                style = HigTheme.typography.body,
                color = colors.accent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

// MARK: - Large title

@Composable
fun HigLargeTitle(
    text: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(
                start = HigMetrics.contentMargin,
                end = HigMetrics.contentMargin,
                top = 4.dp,
                bottom = HigMetrics.elementSpacing,
            ),
    ) {
        Text(
            modifier = Modifier.semantics { heading() },
            text = text,
            style = HigTheme.typography.largeTitle,
            color = HigTheme.colors.label,
        )
        subtitle?.let {
            Text(
                modifier = Modifier.clearAndSetSemantics { contentDescription = it },
                text = it,
                style = HigTheme.typography.subheadline,
                color = HigTheme.colors.secondaryLabel,
            )
        }
    }
}
