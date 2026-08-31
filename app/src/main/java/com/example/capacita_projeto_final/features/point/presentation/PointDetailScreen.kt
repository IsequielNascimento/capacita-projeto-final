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
import androidx.compose.ui.res.stringResource
import com.example.capacita_projeto_final.R
import com.example.capacita_projeto_final.features.visit.domain.ReadingError
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
    val title = (state as? PointDetailUiState.Ready)?.point?.customer ?: stringResource(R.string.point_title_fallback)

    Column(
        Modifier
            .fillMaxSize()
            .background(colors.groupedBackground),
    ) {
        HigNavigationBar(
            title = title,
            backTitle = stringResource(R.string.route_title),
            backAccessibilityLabel = stringResource(R.string.point_back_label),
            onBack = onBack,
            showsInlineTitle = collapsed,
            showsSeparator = collapsed,
        )
        when (state) {
            PointDetailUiState.Loading -> LoadingContent(stringResource(R.string.point_loading))
            PointDetailUiState.NotFound -> MessageContent(
                title = stringResource(R.string.point_not_found_title),
                message = stringResource(R.string.point_not_found_message),
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
                subtitle = stringResource(R.string.point_subtitle, state.point.order),
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
                validationError = state.validationError,
                onReadingChange = onReadingChange,
                onStartVisit = onStartVisit,
            )
        }
        item { Spacer(Modifier.height(HigMetrics.elementSpacing)) }
    }
}

@Composable
private fun PointDataSection(point: RoutePoint) {
    HigListSection(header = stringResource(R.string.point_data_header)) {
        HigValueRow(stringResource(R.string.point_installation), point.installationCode)
        HigRowSeparator()
        HigValueRow(stringResource(R.string.point_meter), point.meterNumber)
        HigRowSeparator()
        HigValueRow(stringResource(R.string.point_previous_reading), point.previousReading.toString())
        HigRowSeparator()
        HigRow {
            Text(stringResource(R.string.point_reference), style = HigTheme.typography.body, color = HigTheme.colors.label)
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
            Text(stringResource(R.string.point_address), style = HigTheme.typography.body, color = HigTheme.colors.label)
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
    HigListSection(header = stringResource(R.string.point_latest_visit_header)) {
        HigValueRow(stringResource(R.string.point_recorded_reading), visit.currentReading.toString())
        HigRowSeparator()
        HigValueRow(stringResource(R.string.point_date), DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(visit.capturedAt)))
        HigRowSeparator()
        HigRow {
            Text(stringResource(R.string.point_status), style = HigTheme.typography.body, color = HigTheme.colors.label)
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
    validationError: ReadingError?,
    onReadingChange: (String) -> Unit,
    onStartVisit: () -> Unit,
) {
    val colors = HigTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(HigMetrics.contentMargin)) {
        HigListSection(
            header = stringResource(R.string.reading_header),
            footer = validationError?.let { stringResource(it.messageRes()) }
                ?: stringResource(R.string.reading_hint, previousReading),
            footerColor = if (validationError != null) colors.destructive else null,
        ) {
            Column(Modifier.padding(HigMetrics.contentMargin)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = readingInput,
                    onValueChange = onReadingChange,
                    placeholder = {
                        Text(
                            stringResource(R.string.reading_placeholder),
                            style = HigTheme.typography.body,
                            color = colors.tertiaryLabel,
                        )
                    },
                    isError = validationError != null,
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
        HigProminentButton(title = stringResource(R.string.action_continue), onClick = onStartVisit)
    }
}
