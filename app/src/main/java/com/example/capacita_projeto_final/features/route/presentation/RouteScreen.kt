package com.example.capacita_projeto_final.features.route.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import com.example.capacita_projeto_final.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.capacita_projeto_final.features.route.domain.RoutePoint
import com.example.capacita_projeto_final.features.visit.domain.Visit
import com.example.capacita_projeto_final.ui.components.HigDisclosureIndicator
import com.example.capacita_projeto_final.ui.components.HigLargeTitle
import com.example.capacita_projeto_final.ui.components.HigListSection
import com.example.capacita_projeto_final.ui.components.HigNavigationBar
import com.example.capacita_projeto_final.ui.components.HigRow
import com.example.capacita_projeto_final.ui.components.HigRowSeparator
import com.example.capacita_projeto_final.ui.components.rememberLargeTitleCollapsed
import com.example.capacita_projeto_final.ui.theme.HigMetrics
import com.example.capacita_projeto_final.ui.theme.HigShapes
import com.example.capacita_projeto_final.ui.theme.HigTheme

@Composable
fun RouteScreen(
    state: RouteUiState,
    onOpenMap: () -> Unit,
    onPointClick: (Int) -> Unit,
) {
    val colors = HigTheme.colors
    val listState = rememberLazyListState()
    val collapsed = rememberLargeTitleCollapsed(listState)

    Column(
        Modifier
            .fillMaxSize()
            .background(colors.groupedBackground),
    ) {
        HigNavigationBar(
            title = stringResource(R.string.route_title),
            showsInlineTitle = collapsed,
            showsSeparator = collapsed,
        )
        when (state) {
            RouteUiState.Loading -> LoadingContent(stringResource(R.string.route_loading))
            is RouteUiState.Error -> MessageContent(
                title = stringResource(R.string.route_unavailable_title),
                message = stringResource(R.string.route_unavailable_message),
            )
            is RouteUiState.Ready -> RouteContent(
                listState = listState,
                points = state.points,
                latestVisits = state.latestVisits,
                onOpenMap = onOpenMap,
                onPointClick = onPointClick,
            )
        }
    }
}

@Composable
private fun RouteContent(
    listState: androidx.compose.foundation.lazy.LazyListState,
    points: List<RoutePoint>,
    latestVisits: Map<Int, Visit>,
    onOpenMap: () -> Unit,
    onPointClick: (Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(
            start = HigMetrics.contentMargin,
            end = HigMetrics.contentMargin,
            bottom = HigMetrics.groupSpacing,
        ),
        verticalArrangement = Arrangement.spacedBy(HigMetrics.groupSpacing),
    ) {
        item {
            HigLargeTitle(
                text = stringResource(R.string.route_title),
                subtitle = stringResource(R.string.route_subtitle),
            )
        }
        item {
            HigListSection {
                HigRow {
                    Text(
                        text = stringResource(R.string.route_summary_points),
                        style = HigTheme.typography.body,
                        color = HigTheme.colors.label,
                    )
                    Box(Modifier.weight(1f))
                    Text(
                        text = points.size.toString(),
                        style = HigTheme.typography.body,
                        color = HigTheme.colors.secondaryLabel,
                    )
                }
                HigRowSeparator()
                HigRow {
                    Text(
                        text = stringResource(R.string.route_summary_visited),
                        style = HigTheme.typography.body,
                        color = HigTheme.colors.label,
                    )
                    Box(Modifier.weight(1f))
                    Text(
                        text = latestVisits.size.toString(),
                        style = HigTheme.typography.body,
                        color = HigTheme.colors.secondaryLabel,
                    )
                }
                HigRowSeparator()
                HigRow(onClick = onOpenMap, onClickLabel = stringResource(R.string.route_map_row_action)) {
                    Text(
                        text = stringResource(R.string.route_map_row),
                        style = HigTheme.typography.body,
                        color = HigTheme.colors.label,
                    )
                    Box(Modifier.weight(1f))
                    HigDisclosureIndicator()
                }
            }
        }
        item {
            HigListSection(header = stringResource(R.string.route_points_header)) {
                points.forEachIndexed { index, point ->
                    if (index > 0) HigRowSeparator(startInset = 60.dp)
                    RoutePointRow(
                        point = point,
                        visit = latestVisits[point.id],
                        onClick = { onPointClick(point.id) },
                    )
                }
            }
        }
        item { Spacer(Modifier.height(HigMetrics.elementSpacing)) }
    }
}

@Composable
private fun RoutePointRow(point: RoutePoint, visit: Visit?, onClick: () -> Unit) {
    val colors = HigTheme.colors
    HigRow(onClick = onClick, onClickLabel = stringResource(R.string.route_point_row_action)) {
        OrderBadge(order = point.order, visited = visit != null)
        Column(Modifier.weight(1f)) {
            Text(point.customer, style = HigTheme.typography.body, color = colors.label)
            Text(point.referencePoint, style = HigTheme.typography.footnote, color = colors.secondaryLabel)
            Text(point.address, style = HigTheme.typography.caption1, color = colors.secondaryLabel)
        }
        HigDisclosureIndicator()
    }
}

@Composable
private fun OrderBadge(order: Int, visited: Boolean) {
    val colors = HigTheme.colors
    val background = if (visited) colors.successFill else colors.accentFill
    val foreground = if (visited) colors.onSuccessFill else colors.onAccentFill
    Box(
        modifier = Modifier
            .defaultMinSize(minWidth = 28.dp, minHeight = 28.dp)
            .clip(HigShapes.badge)
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            text = order.toString(),
            style = HigTheme.typography.footnoteEmphasized,
            color = foreground,
        )
    }
}

@Composable
internal fun LoadingContent(label: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(color = HigTheme.colors.accent)
        Spacer(Modifier.height(HigMetrics.contentMargin))
        Text(label, style = HigTheme.typography.subheadline, color = HigTheme.colors.secondaryLabel)
    }
}

@Composable
internal fun MessageContent(title: String, message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = Modifier.semantics { heading() },
            text = title,
            style = HigTheme.typography.title3,
            color = HigTheme.colors.label,
        )
        Spacer(Modifier.height(HigMetrics.elementSpacing))
        Text(
            text = message,
            style = HigTheme.typography.subheadline,
            color = HigTheme.colors.secondaryLabel,
        )
    }
}
