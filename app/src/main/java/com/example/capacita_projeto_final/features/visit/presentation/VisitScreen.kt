package com.example.capacita_projeto_final.features.visit.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.capacita_projeto_final.features.route.presentation.LoadingContent
import com.example.capacita_projeto_final.features.route.presentation.MessageContent
import com.example.capacita_projeto_final.features.visit.infrastructure.PhotoEvidenceStore
import com.example.capacita_projeto_final.ui.components.HigBorderedButton
import com.example.capacita_projeto_final.ui.components.HigCameraSymbol
import com.example.capacita_projeto_final.ui.components.HigCheckmark
import com.example.capacita_projeto_final.ui.components.HigLargeTitle
import com.example.capacita_projeto_final.ui.components.HigListSection
import com.example.capacita_projeto_final.ui.components.HigLocationSymbol
import com.example.capacita_projeto_final.ui.components.HigNavigationBar
import com.example.capacita_projeto_final.ui.components.HigProminentButton
import com.example.capacita_projeto_final.ui.components.HigRow
import com.example.capacita_projeto_final.ui.components.HigRowSeparator
import com.example.capacita_projeto_final.ui.components.HigValueRow
import com.example.capacita_projeto_final.ui.components.rememberLargeTitleCollapsed
import com.example.capacita_projeto_final.ui.theme.HigMetrics
import com.example.capacita_projeto_final.ui.theme.HigTheme

@Composable
fun VisitScreen(
    state: VisitUiState,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onFinish: () -> Unit,
    onPhotoCaptured: (String?) -> Unit,
    onEvidenceMessage: (EvidenceFeedback) -> Unit,
    onCaptureLocation: () -> Unit,
) {
    val context = LocalContext.current
    val colors = HigTheme.colors
    val listState = rememberLazyListState()
    val collapsed = rememberLargeTitleCollapsed(listState)

    var pendingPhotoUri by remember { mutableStateOf<Uri?>(null) }
    val takePhotoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { captured ->
        onPhotoCaptured(pendingPhotoUri?.toString()?.takeIf { captured })
    }
    val launchCamera = {
        runCatching { PhotoEvidenceStore.createDestination(context) }
            .onSuccess { uri ->
                pendingPhotoUri = uri
                takePhotoLauncher.launch(uri)
            }
            .onFailure { onEvidenceMessage(EvidenceFeedback.PhotoStorageUnavailable) }
        Unit
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) launchCamera() else onEvidenceMessage(EvidenceFeedback.CameraPermissionDenied)
    }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) onCaptureLocation() else onEvidenceMessage(EvidenceFeedback.LocationPermissionDenied)
    }
    val requestPhoto = {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        Unit
    }
    val requestLocation = {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED
        if (granted) {
            onCaptureLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ),
            )
        }
        Unit
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(colors.groupedBackground),
    ) {
        HigNavigationBar(
            title = "Registro de visita",
            backTitle = "Ponto",
            backAccessibilityLabel = "Voltar para o ponto",
            onBack = onBack,
            showsInlineTitle = collapsed,
            showsSeparator = collapsed,
        )
        when (state) {
            VisitUiState.Loading -> LoadingContent("Carregando a visita")
            is VisitUiState.Error -> MessageContent(
                title = "Não foi possível continuar",
                message = state.message,
            )

            is VisitUiState.Ready -> ReadyContent(
                listState = listState,
                state = state,
                onSave = onSave,
                onTakePhoto = requestPhoto,
                onGetLocation = requestLocation,
            )

            is VisitUiState.Saved -> SavedContent(state = state, onFinish = onFinish)
        }
    }
}

@Composable
private fun ReadyContent(
    listState: androidx.compose.foundation.lazy.LazyListState,
    state: VisitUiState.Ready,
    onSave: () -> Unit,
    onTakePhoto: () -> Unit,
    onGetLocation: () -> Unit,
) {
    val colors = HigTheme.colors
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
            HigLargeTitle(text = "Registro de visita", subtitle = state.point.customer)
        }
        item {
            HigListSection(header = "Leitura") {
                HigValueRow("Instalação", state.point.installationCode)
                HigRowSeparator()
                HigValueRow("Medidor", state.point.meterNumber)
                HigRowSeparator()
                HigValueRow("Leitura anterior", state.point.previousReading.toString())
                HigRowSeparator()
                HigRow {
                    Text("Leitura atual", style = HigTheme.typography.body, color = colors.label)
                    Box(Modifier.weight(1f))
                    Text(
                        text = state.reading.toString(),
                        style = HigTheme.typography.bodyEmphasized,
                        color = colors.label,
                    )
                }
            }
        }
        item {
            EvidenceSection(
                state = state,
                onTakePhoto = onTakePhoto,
                onGetLocation = onGetLocation,
            )
        }
        item {
            HigProminentButton(
                title = "Salvar visita",
                onClick = onSave,
                inProgress = state.saving,
                progressLabel = "Salvando",
            )
        }
        item { Spacer(Modifier.height(HigMetrics.elementSpacing)) }
    }
}

@Composable
private fun EvidenceSection(
    state: VisitUiState.Ready,
    onTakePhoto: () -> Unit,
    onGetLocation: () -> Unit,
) {
    val colors = HigTheme.colors
    HigListSection(
        header = "Evidências",
        footer = state.feedback?.let(EvidenceFeedback::readableMessage),
        footerColor = if (state.feedback?.isFailure == true) colors.destructive else null,
    ) {
        EvidenceRow(
            label = "Foto do medidor",
            value = if (state.photoUri == null) "Não anexada" else "Anexada",
            captured = state.photoUri != null,
        )
        HigRowSeparator()
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = HigMetrics.contentMargin, vertical = 12.dp),
        ) {
            HigBorderedButton(
                title = if (state.photoUri == null) "Tirar foto" else "Tirar outra foto",
                onClick = onTakePhoto,
                leading = { HigCameraSymbol(tint = colors.accent, size = 20.dp) },
            )
        }
        HigRowSeparator()
        EvidenceRow(
            label = "Localização",
            value = state.location?.let { "${it.latitude.formatCoordinate()}, ${it.longitude.formatCoordinate()}" }
                ?: "Não capturada",
            captured = state.location != null,
        )
        HigRowSeparator()
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = HigMetrics.contentMargin, vertical = 12.dp),
        ) {
            HigBorderedButton(
                title = "Usar localização atual",
                onClick = onGetLocation,
                inProgress = state.locationLoading,
                progressLabel = "Obtendo a localização",
                leading = { HigLocationSymbol(tint = colors.accent, size = 20.dp) },
            )
        }
    }
}

@Composable
private fun EvidenceRow(label: String, value: String, captured: Boolean) {
    val colors = HigTheme.colors
    HigRow {
        Text(label, style = HigTheme.typography.body, color = colors.label)
        Box(Modifier.weight(1f))
        Text(
            text = value,
            style = HigTheme.typography.subheadline,
            color = if (captured) colors.success else colors.secondaryLabel,
        )
        if (captured) HigCheckmark(tint = colors.success, size = 15.dp)
    }
}

@Composable
private fun SavedContent(state: VisitUiState.Saved, onFinish: () -> Unit) {
    val colors = HigTheme.colors
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = HigMetrics.contentMargin),
        verticalArrangement = Arrangement.spacedBy(HigMetrics.groupSpacing),
    ) {
        Spacer(Modifier.height(HigMetrics.groupSpacing))
        HigListSection(header = "Visita salva") {
            HigValueRow("Cliente", state.point.customer)
            HigRowSeparator()
            HigValueRow("Leitura", state.reading.toString())
            HigRowSeparator()
            HigRow {
                Text("Situação", style = HigTheme.typography.body, color = colors.label)
                Box(Modifier.weight(1f))
                Text(
                    text = "Aguardando envio",
                    style = HigTheme.typography.body,
                    color = colors.secondaryLabel,
                )
            }
        }
        HigProminentButton(title = "Concluir", onClick = onFinish)
    }
}

private fun Double.formatCoordinate(): String = "%.5f".format(this)
