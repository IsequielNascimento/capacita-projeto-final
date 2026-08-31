package com.example.capacita_projeto_final.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.capacita_projeto_final.ui.theme.HigMetrics
import com.example.capacita_projeto_final.ui.theme.HigTheme

@Composable
fun HigEmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = HigMetrics.contentMargin, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(HigMetrics.elementSpacing),
    ) {
        Text(
            modifier = Modifier.semantics { heading() },
            text = title,
            style = HigTheme.typography.title3,
            color = HigTheme.colors.label,
            textAlign = TextAlign.Center,
        )
        Text(
            text = message,
            style = HigTheme.typography.subheadline,
            color = HigTheme.colors.secondaryLabel,
            textAlign = TextAlign.Center,
        )
        action?.invoke()
    }
}
