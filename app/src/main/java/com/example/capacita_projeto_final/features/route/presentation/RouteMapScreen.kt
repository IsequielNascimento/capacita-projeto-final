package com.example.capacita_projeto_final.features.route.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.capacita_projeto_final.features.route.domain.ProjectedRoutePoint
import com.example.capacita_projeto_final.features.route.domain.RoutePoint
import com.example.capacita_projeto_final.features.route.domain.projectRoutePoints
import com.example.capacita_projeto_final.ui.components.HigCheckmark
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
fun RouteMapScreen(
    state: RouteUiState,
    onBack: () -> Unit,
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
            title = "Mapa da rota",
            backTitle = "Rota",
            backAccessibilityLabel = "Voltar para Rota",
            onBack = onBack,
            showsInlineTitle = collapsed,
            showsSeparator = collapsed,
        )
        when (state) {
            RouteUiState.Loading -> LoadingContent("Carregando o mapa")
            is RouteUiState.Error -> MessageContent(title = "Mapa indisponível", message = state.message)
            is RouteUiState.Ready -> RouteMapContent(
                listState = listState,
                state = state,
                onPointClick = onPointClick,
            )
        }
    }
}

@Composable
private fun RouteMapContent(
    listState: androidx.compose.foundation.lazy.LazyListState,
    state: RouteUiState.Ready,
    onPointClick: (Int) -> Unit,
) {
    val visitedPointIds = remember(state.latestVisits) { state.latestVisits.keys }
    val orderedPoints = remember(state.points) { state.points.sortedBy(RoutePoint::order) }

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
                text = "Mapa da rota",
                subtitle = "${visitedPointIds.size} de ${state.points.size} pontos visitados",
            )
        }
        item {
            RouteMap(
                points = orderedPoints,
                visitedPointIds = visitedPointIds,
                onPointClick = onPointClick,
            )
        }
        item {
            HigListSection(header = "Ordem de atendimento") {
                orderedPoints.forEachIndexed { index, point ->
                    if (index > 0) HigRowSeparator(startInset = 60.dp)
                    RouteMapPointRow(
                        point = point,
                        visited = point.id in visitedPointIds,
                        onClick = { onPointClick(point.id) },
                    )
                }
            }
        }
        item { Spacer(Modifier.height(HigMetrics.elementSpacing)) }
    }
}

// MARK: - Map

@Composable
private fun RouteMap(
    points: List<RoutePoint>,
    visitedPointIds: Set<Int>,
    onPointClick: (Int) -> Unit,
) {
    val colors = HigTheme.colors
    val projectedPoints = remember(points) { projectRoutePoints(points) }
    val density = LocalDensity.current
    val markerSize = HigMetrics.minimumTouchTarget

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(HigShapes.group)
            .background(colors.mapBackground),
    ) {
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clearAndSetSemantics { },
        ) {
            listOf(0.18f, 0.38f, 0.62f, 0.82f).forEach { fraction ->
                drawLine(
                    color = colors.mapStreet,
                    start = Offset(0f, size.height * fraction),
                    end = Offset(size.width, size.height * (fraction - 0.08f)),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }
            listOf(0.22f, 0.48f, 0.74f).forEach { fraction ->
                drawLine(
                    color = colors.mapStreet,
                    start = Offset(size.width * fraction, 0f),
                    end = Offset(size.width * (fraction + 0.08f), size.height),
                    strokeWidth = 6.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }
            projectedPoints.zipWithNext().forEach { (start, end) ->
                drawLine(
                    color = colors.accentFill,
                    start = start.asOffset(size.width, size.height),
                    end = end.asOffset(size.width, size.height),
                    strokeWidth = 5.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }
        }

        projectedPoints.forEach { projected ->
            val visited = projected.point.id in visitedPointIds
            val centerX = with(density) { (projected.xFraction * widthPx).toDp() }
            val centerY = with(density) { (projected.yFraction * heightPx).toDp() }
            MapMarker(
                order = projected.point.order,
                customer = projected.point.customer,
                visited = visited,
                markerSize = markerSize,
                modifier = Modifier.offset(x = centerX - markerSize / 2, y = centerY - markerSize / 2),
                onClick = { onPointClick(projected.point.id) },
            )
        }
    }
}

@Composable
private fun MapMarker(
    order: Int,
    customer: String,
    visited: Boolean,
    markerSize: Dp,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val colors = HigTheme.colors
    val fill = if (visited) colors.successFill else colors.accentFill
    val onFill = if (visited) colors.onSuccessFill else colors.onAccentFill
    val stateLabel = if (visited) "visitado" else "pendente"

    Box(
        modifier = modifier
            .size(markerSize)
            .then(
                Modifier.clip(CircleShape),
            ),
        contentAlignment = Alignment.Center,
    ) {
        HigRow(
            modifier = Modifier.size(markerSize),
            onClick = onClick,
            onClickLabel = "Abrir ponto $order, $customer, $stateLabel",
        ) {}
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(colors.systemBackground),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(fill),
                contentAlignment = Alignment.Center,
            ) {
                if (visited) {
                    HigCheckmark(tint = onFill, size = 14.dp, strokeWidth = 2.dp)
                } else {
                    Text(
                        text = order.toString(),
                        style = HigTheme.typography.caption1,
                        color = onFill,
                    )
                }
            }
        }
    }
}

private fun ProjectedRoutePoint.asOffset(width: Float, height: Float) = Offset(
    x = xFraction * width,
    y = yFraction * height,
)

// MARK: - Rows

@Composable
private fun RouteMapPointRow(point: RoutePoint, visited: Boolean, onClick: () -> Unit) {
    val colors = HigTheme.colors
    HigRow(onClick = onClick, onClickLabel = "Abrir os detalhes do ponto") {
        Box(
            modifier = Modifier
                .defaultMinSize(minWidth = 28.dp, minHeight = 28.dp)
                .clip(HigShapes.badge)
                .background(if (visited) colors.successFill else colors.accentFill),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                text = point.order.toString(),
                style = HigTheme.typography.footnoteEmphasized,
                color = if (visited) colors.onSuccessFill else colors.onAccentFill,
            )
        }
        Column(Modifier.weight(1f)) {
            Text(point.referencePoint, style = HigTheme.typography.body, color = colors.label)
            Text(
                text = if (visited) "Visitado" else "Pendente",
                style = HigTheme.typography.footnote,
                color = if (visited) colors.success else colors.secondaryLabel,
            )
        }
        if (visited) HigCheckmark(tint = colors.success, size = 15.dp)
        HigDisclosureIndicator()
    }
}
