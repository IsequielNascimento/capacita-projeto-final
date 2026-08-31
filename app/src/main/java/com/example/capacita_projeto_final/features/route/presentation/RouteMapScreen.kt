package com.example.capacita_projeto_final.features.route.presentation

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.capacita_projeto_final.features.route.domain.ProjectedRoutePoint
import com.example.capacita_projeto_final.features.route.domain.RoutePoint
import com.example.capacita_projeto_final.features.route.domain.projectRoutePoints
import com.example.capacita_projeto_final.ui.components.NavigationTopBar
import com.example.capacita_projeto_final.ui.theme.Blue
import com.example.capacita_projeto_final.ui.theme.Muted
import com.example.capacita_projeto_final.ui.theme.Navy
import com.example.capacita_projeto_final.ui.theme.Success

@Composable
fun RouteMapScreen(
    state: RouteUiState,
    onBack: () -> Unit,
    onPointClick: (Int) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            NavigationTopBar(
                title = "Mapa da rota",
                subtitle = "Aldeota · Fortaleza",
                onBack = onBack,
            )
        },
    ) { contentPadding ->
        when (state) {
            RouteUiState.Loading -> Column(
                modifier = Modifier.fillMaxSize().padding(contentPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(16.dp))
                Text("Carregando mapa da rota")
            }

            is RouteUiState.Error -> Column(
                modifier = Modifier.fillMaxSize().padding(contentPadding).padding(24.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text("Mapa indisponível", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
                Text(state.message, color = Muted)
            }

            is RouteUiState.Ready -> RouteMapContent(
                modifier = Modifier.padding(contentPadding),
                state = state,
                onPointClick = onPointClick,
            )
        }
    }
}

@Composable
private fun RouteMapContent(
    modifier: Modifier,
    state: RouteUiState.Ready,
    onPointClick: (Int) -> Unit,
) {
    val visitedPointIds = remember(state.latestVisits) { state.latestVisits.keys }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Sequência geográfica",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${visitedPointIds.size} de ${state.points.size} pontos visitados",
                    color = Muted,
                )
            }
        }
        item {
            RouteMap(
                points = state.points,
                visitedPointIds = visitedPointIds,
            )
        }
        item {
            Text(
                text = "O traçado usa as coordenadas locais e permanece disponível sem conexão.",
                color = Muted,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        item {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = "ORDEM DE ATENDIMENTO",
                color = Muted,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
        }
        items(state.points.sortedBy(RoutePoint::order), key = RoutePoint::id) { point ->
            RouteMapPointCard(
                point = point,
                visited = point.id in visitedPointIds,
                onClick = { onPointClick(point.id) },
            )
        }
    }
}

@Composable
private fun RouteMap(
    points: List<RoutePoint>,
    visitedPointIds: Set<Int>,
) {
    val projectedPoints = remember(points) { projectRoutePoints(points) }
    val backgroundColor = Color(0xFFEAF0F4)
    val streetColor = Color(0xFFD3DDE5)
    val secondaryStreetColor = Color.White.copy(alpha = 0.86f)
    val visitedColor = Success
    val labelPaint = remember {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            textAlign = Paint.Align.CENTER
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.92f)
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor),
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .semantics {
                    contentDescription = "Mapa da rota com ${points.size} pontos em ordem de atendimento"
                },
        ) {
            labelPaint.textSize = 13.dp.toPx()
            val roadWidth = 5.dp.toPx()
            val secondaryRoadWidth = 2.dp.toPx()
            listOf(0.18f, 0.38f, 0.62f, 0.82f).forEach { fraction ->
                drawLine(
                    color = secondaryStreetColor,
                    start = Offset(0f, size.height * fraction),
                    end = Offset(size.width, size.height * (fraction - 0.08f)),
                    strokeWidth = roadWidth,
                    cap = StrokeCap.Round,
                )
            }
            listOf(0.22f, 0.48f, 0.74f).forEach { fraction ->
                drawLine(
                    color = streetColor,
                    start = Offset(size.width * fraction, 0f),
                    end = Offset(size.width * (fraction + 0.08f), size.height),
                    strokeWidth = secondaryRoadWidth,
                    cap = StrokeCap.Round,
                )
            }
            projectedPoints.zipWithNext().forEach { (start, end) ->
                drawLine(
                    color = Blue.copy(alpha = 0.24f),
                    start = start.asOffset(size.width, size.height),
                    end = end.asOffset(size.width, size.height),
                    strokeWidth = 10.dp.toPx(),
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = Blue,
                    start = start.asOffset(size.width, size.height),
                    end = end.asOffset(size.width, size.height),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }
            projectedPoints.forEach { projected ->
                val center = projected.asOffset(size.width, size.height)
                val markerColor = if (projected.point.id in visitedPointIds) visitedColor else Blue
                drawCircle(Color.White, radius = 17.dp.toPx(), center = center)
                drawCircle(markerColor, radius = 13.dp.toPx(), center = center)
                drawIntoCanvas { canvas ->
                    val baseline = center.y - (labelPaint.ascent() + labelPaint.descent()) / 2f
                    canvas.nativeCanvas.drawText(projected.point.order.toString(), center.x, baseline, labelPaint)
                }
            }
        }
        Surface(
            modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
            color = Color.White.copy(alpha = 0.9f),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                text = "N ↑",
                color = Navy,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

private fun ProjectedRoutePoint.asOffset(width: Float, height: Float) = Offset(
    x = xFraction * width,
    y = yFraction * height,
)

@Composable
private fun RouteMapPointCard(
    point: RoutePoint,
    visited: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                color = if (visited) Success else MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp),
                    text = point.order.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(point.referencePoint, fontWeight = FontWeight.Bold)
                Text(point.address, color = Muted, style = MaterialTheme.typography.bodySmall)
            }
            Icon(
                modifier = Modifier.height(24.dp),
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Muted,
            )
        }
    }
}
