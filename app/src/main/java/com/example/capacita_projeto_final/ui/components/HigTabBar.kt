package com.example.capacita_projeto_final.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.capacita_projeto_final.ui.theme.HigMetrics
import com.example.capacita_projeto_final.ui.theme.HigTheme

// MARK: - Item

data class HigTabItem(
    val id: String,
    val title: String,
    val symbol: @Composable (Color) -> Unit,
)

// MARK: - Tab bar

@Composable
fun HigTabBar(
    items: List<HigTabItem>,
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = HigTheme.colors
    Column(
        modifier
            .fillMaxWidth()
            .background(colors.barBackground),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(HigMetrics.separatorThickness)
                .background(colors.separator),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(HigMetrics.tabBarHeight),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { item ->
                val selected = item.id == selectedId
                val tint = if (selected) colors.accent else colors.secondaryLabel
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .selectable(
                            selected = selected,
                            role = Role.Tab,
                            onClick = { onSelect(item.id) },
                        )
                        .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    item.symbol(tint)
                    Text(
                        text = item.title,
                        style = HigTheme.typography.caption2,
                        color = tint,
                    )
                }
            }
        }
    }
}
