package com.example.capacita_projeto_final.features.point.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.capacita_projeto_final.features.route.domain.RoutePoint
import com.example.capacita_projeto_final.features.route.presentation.LoadingContent
import com.example.capacita_projeto_final.features.route.presentation.MessageContent
import com.example.capacita_projeto_final.features.visit.domain.Visit
import com.example.capacita_projeto_final.features.visit.presentation.readableLabel
import com.example.capacita_projeto_final.ui.components.HigLargeTitle
import com.example.capacita_projeto_final.ui.components.HigListSection
import com.example.capacita_projeto_final.ui.components.HigNavigationBar
import com.example.capacita_projeto_final.ui.components.HigProminentButton
import com.example.capacita_projeto_final.ui.components.HigRow
import com.example.capacita_projeto_final.ui.components.HigRowSeparator
import com.example.capacita_projeto_final.ui.components.HigValueRow
import com.example.capacita_projeto_final.ui.components.rememberLargeTitleCollapsed
import com.example.capacita_projeto_final.ui.theme.HigMetrics
import com.example.capacita_projeto_final.ui.theme.HigShapes
import com.example.capacita_projeto_final.ui.theme.HigTheme
import java.text.DateFormat
import java.util.Date

@Composable
fun PointDetailScreen(
    state: PointDetailUiState,
    onBack: () -> Unit,
    onReadingChange: (String) -> Unit,
    onStartVisit: () -> Unit,
) {
    val colors = HigTheme.colors
    val listState = rememberLazyListState()
    val collapsed = rememberLargeTitleCollapsed(listState)
    val title = (state as? PointDetailUiState.Ready)?.point?.customer ?: "Ponto"

    Column(
        Modifier
            .fillMaxSize()
            .background(colors.groupedBackground),
    ) {
        HigNavigationBar(
            title = title,
            backTitle = "Rota",
            backAccessibilityLabel = "Voltar para Rota",
            onBack = onBack,
            showsInlineTitle = collapsed,
            showsSeparator = collapsed,
        )
        when (state) {
            PointDetailUiState.Loading -> LoadingContent("Carregando o ponto")
            PointDetailUiState.NotFound -> MessageContent(
                title = "Ponto não encontrado",
                message = "Este ponto não faz mais parte da rota.",
            )

            is PointDetailUiState.Ready -> PointDetailContent(
                listState = listState,
                state = state,
                onReadingChange = onReadingChange,
                onStartVisit = onStartVisit,
            )
        }
    }
}

@Composable
private fun PointDetailContent(
    listState: androidx.compose.foundation.lazy.LazyListState,
    state: PointDetailUiState.Ready,
    onReadingChange: (String) -> Unit,
    onStartVisit: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
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
                text = state.point.customer,
                subtitle = "Ponto ${state.point.order} da rota",
            )
        }
        item { PointDataSection(state.point) }
        state.latestVisit?.let { visit ->
            item { LatestVisitSection(visit) }
        }
        item {
            ReadingSection(
                readingInput = state.readingInput,
                previousReading = state.point.previousReading,
                validationMessage = state.validationMessage,
                onReadingChange = onReadingChange,
                onStartVisit = onStartVisit,
            )
        }
        item { Spacer(Modifier.height(HigMetrics.elementSpacing)) }
    }
}

@Composable
private fun PointDataSection(point: RoutePoint) {
    HigListSection(header = "Dados do ponto") {
        HigValueRow("Instalação", point.installationCode)
        HigRowSeparator()
        HigValueRow("Medidor", point.meterNumber)
        HigRowSeparator()
        HigValueRow("Leitura anterior", point.previousReading.toString())
        HigRowSeparator()
        HigRow {
            Text("Referência", style = HigTheme.typography.body, color = HigTheme.colors.label)
            Box(Modifier.weight(1f))
            Text(
                text = point.referencePoint,
                style = HigTheme.typography.body,
                color = HigTheme.colors.secondaryLabel,
            )
        }
        HigRowSeparator()
        Column(
            Modifier
                .fillMaxWidth()
                .padding(HigMetrics.contentMargin),
        ) {
            Text("Endereço", style = HigTheme.typography.body, color = HigTheme.colors.label)
            Text(
                modifier = Modifier.padding(top = 2.dp),
                text = point.address,
                style = HigTheme.typography.subheadline,
                color = HigTheme.colors.secondaryLabel,
            )
        }
    }
}

@Composable
private fun LatestVisitSection(visit: Visit) {
    HigListSection(header = "Última visita") {
        HigValueRow("Leitura registrada", visit.currentReading.toString())
        HigRowSeparator()
        HigValueRow("Data", DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(visit.capturedAt)))
        HigRowSeparator()
        HigRow {
            Text("Situação", style = HigTheme.typography.body, color = HigTheme.colors.label)
            Box(Modifier.weight(1f))
            Text(
                text = visit.syncStatus.readableLabel(),
                style = HigTheme.typography.body,
                color = HigTheme.colors.secondaryLabel,
            )
        }
    }
}

@Composable
private fun ReadingSection(
    readingInput: String,
    previousReading: Int,
    validationMessage: String?,
    onReadingChange: (String) -> Unit,
    onStartVisit: () -> Unit,
) {
    val colors = HigTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(HigMetrics.contentMargin)) {
        HigListSection(
            header = "Nova leitura",
            footer = validationMessage ?: "A leitura precisa ser igual ou maior que $previousReading.",
            footerColor = if (validationMessage != null) colors.destructive else null,
        ) {
            Column(Modifier.padding(HigMetrics.contentMargin)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = readingInput,
                    onValueChange = onReadingChange,
                    placeholder = {
                        Text(
                            "Leitura atual",
                            style = HigTheme.typography.body,
                            color = colors.tertiaryLabel,
                        )
                    },
                    isError = validationMessage != null,
                    textStyle = HigTheme.typography.body,
                    shape = HigShapes.control,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colors.fill,
                        unfocusedContainerColor = colors.fill,
                        errorContainerColor = colors.fill,
                        focusedTextColor = colors.label,
                        unfocusedTextColor = colors.label,
                        focusedIndicatorColor = colors.accent,
                        unfocusedIndicatorColor = colors.separator,
                        errorIndicatorColor = colors.destructive,
                    ),
                )
            }
        }
        HigProminentButton(title = "Continuar", onClick = onStartVisit)
    }
}
