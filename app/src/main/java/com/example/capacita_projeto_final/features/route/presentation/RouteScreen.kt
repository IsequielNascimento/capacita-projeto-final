package com.example.capacita_projeto_final.features.route.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.capacita_projeto_final.features.route.domain.RoutePoint
import com.example.capacita_projeto_final.features.visit.domain.Visit
import com.example.capacita_projeto_final.ui.theme.Muted
import com.example.capacita_projeto_final.ui.theme.Navy
import com.example.capacita_projeto_final.ui.theme.Success

@Composable
fun RouteScreen(
    state: RouteUiState,
    onPointClick: (Int) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { RouteHeader() },
    ) { contentPadding ->
        when (state) {
            RouteUiState.Loading -> Column(
                modifier = Modifier.fillMaxSize().padding(contentPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(16.dp))
                Text("Carregando rota local")
            }

            is RouteUiState.Error -> Column(
                modifier = Modifier.fillMaxSize().padding(contentPadding).padding(24.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text("Rota indisponível", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
                Text(state.message, color = Muted)
            }

            is RouteUiState.Ready -> RouteContent(
                modifier = Modifier.padding(contentPadding),
                points = state.points,
                latestVisits = state.latestVisits,
                onPointClick = onPointClick,
            )
        }
    }
}

@Composable
private fun RouteHeader() {
    Surface(color = Navy) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "ROTA DE CAMPO",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Capacita Aldeota",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "CAPACITA-ALDEOTA-001",
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.72f),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun RouteContent(
    modifier: Modifier,
    points: List<RoutePoint>,
    latestVisits: Map<Int, Visit>,
    onPointClick: (Int) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { RouteSummary(points.size) }
        item {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = "PONTOS DE ATENDIMENTO",
                color = Muted,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
        }
        items(points, key = RoutePoint::id) { point ->
            RoutePointCard(
                point = point,
                visit = latestVisits[point.id],
                onClick = { onPointClick(point.id) },
            )
        }
    }
}

@Composable
private fun RouteSummary(pointCount: Int) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text("Cobertura da rota", color = Muted)
                Text(
                    text = "$pointCount pontos",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Aldeota", fontWeight = FontWeight.SemiBold)
                Text("Fortaleza · CE", color = Muted)
            }
        }
    }
}

@Composable
private fun RoutePointCard(point: RoutePoint, visit: Visit?, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Surface(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(12.dp)) {
                Text(
                    modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp),
                    text = point.order.toString(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(point.customer, fontWeight = FontWeight.Bold)
                Text(point.installationCode, color = MaterialTheme.colorScheme.primary)
                Text(point.referencePoint, color = Muted)
                Text(point.address, color = Muted, style = MaterialTheme.typography.bodySmall)
                if (visit != null) {
                    Text(
                        text = "Visitado · ${visit.syncStatus}",
                        color = Success,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Text("›", style = MaterialTheme.typography.headlineSmall, color = Muted)
        }
    }
}
