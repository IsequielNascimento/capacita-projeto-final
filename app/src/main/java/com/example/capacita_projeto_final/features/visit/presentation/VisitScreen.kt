package com.example.capacita_projeto_final.features.visit.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.res.stringResource
import com.example.capacita_projeto_final.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.capacita_projeto_final.features.route.presentation.LoadingContent
import com.example.capacita_projeto_final.features.route.presentation.MessageContent
import com.example.capacita_projeto_final.features.visit.infrastructure.AppSettings
import com.example.capacita_projeto_final.features.visit.infrastructure.PhotoEvidenceStore
import com.example.capacita_projeto_final.ui.components.HigAction
import com.example.capacita_projeto_final.ui.components.HigAlert
import com.example.capacita_projeto_final.ui.components.HigActionSheet
import com.example.capacita_projeto_final.ui.components.HigBorderedButton
import com.example.capacita_projeto_final.ui.components.HigCameraSymbol
import com.example.capacita_projeto_final.ui.components.HigCheckmark
import com.example.capacita_projeto_final.ui.components.HigHapticFeedback
import com.example.capacita_projeto_final.ui.components.HigHapticOutcome
import com.example.capacita_projeto_final.ui.components.HigLargeTitle
import com.example.capacita_projeto_final.ui.components.HigListSection
import com.example.capacita_projeto_final.ui.components.HigLocationSymbol
import com.example.capacita_projeto_final.ui.components.HigNavigationBar
import com.example.capacita_projeto_final.ui.components.HigPlainButton
import com.example.capacita_projeto_final.ui.components.HigProminentButton
import com.example.capacita_projeto_final.ui.components.HigRow
import com.example.capacita_projeto_final.ui.components.HigRowSeparator
import com.example.capacita_projeto_final.ui.components.HigSheetGrabber
import com.example.capacita_projeto_final.ui.components.HigValueRow
import com.example.capacita_projeto_final.ui.components.rememberLargeTitleCollapsed
import com.example.capacita_projeto_final.ui.theme.HigMetrics
import com.example.capacita_projeto_final.ui.theme.HigShapes
import com.example.capacita_projeto_final.ui.theme.HigTheme

@Composable
fun VisitScreen(
    state: VisitUiState,
    onDismiss: () -> Unit,
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
    var confirmingDiscard by remember { mutableStateOf(false) }
    var primingPermission by remember { mutableStateOf<EvidencePermission?>(null) }
    var deniedPermission by remember { mutableStateOf<EvidencePermission?>(null) }

    val readyState = state as? VisitUiState.Ready
    val requestDismiss = {
        if (readyState?.hasUnsavedEvidence == true) confirmingDiscard = true else onDismiss()
    }
    BackHandler(enabled = state !is VisitUiState.Saved, onBack = requestDismiss)
    HigHapticFeedback(
        outcome = when (state) {
            is VisitUiState.Saved -> HigHapticOutcome.Success
            is VisitUiState.Error -> HigHapticOutcome.Failure
            else -> null
        },
        key = state::class,
    )
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
        if (granted) {
            launchCamera()
        } else {
            onEvidenceMessage(EvidenceFeedback.CameraPermissionDenied)
            deniedPermission = EvidencePermission.Camera
        }
    }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            onCaptureLocation()
        } else {
            onEvidenceMessage(EvidenceFeedback.LocationPermissionDenied)
            deniedPermission = EvidencePermission.Location
        }
    }
    val requestPhoto = {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera()
        } else {
            primingPermission = EvidencePermission.Camera
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
            primingPermission = EvidencePermission.Location
        }
        Unit
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(colors.scrim)
            .padding(top = 10.dp)
            .clip(HigShapes.sheet)
            .background(colors.groupedBackground),
    ) {
        HigSheetGrabber()
        HigNavigationBar(
            title = stringResource(R.string.visit_title),
            showsInlineTitle = collapsed,
            showsSeparator = collapsed,
            leading = if (state is VisitUiState.Saved) {
                null
            } else {
                {
                    HigPlainButton(
                        title = stringResource(R.string.action_cancel),
                        onClick = requestDismiss,
                    )
                }
            },
            trailing = readyState?.let { ready ->
                {
                    HigPlainButton(
                        title = stringResource(R.string.action_save),
                        onClick = onSave,
                        emphasized = true,
                        enabled = !ready.saving,
                    )
                }
            },
        )
        when (state) {
            VisitUiState.Loading -> LoadingContent(stringResource(R.string.visit_loading))
            is VisitUiState.Error -> MessageContent(
                title = stringResource(R.string.visit_error_title),
                message = stringResource(state.reason.messageRes()),
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

    primingPermission?.let { permission ->
        HigAlert(
            title = stringResource(permission.primingTitleRes),
            message = stringResource(permission.primingMessageRes),
            confirmTitle = stringResource(R.string.action_allow),
            onConfirm = {
                primingPermission = null
                when (permission) {
                    EvidencePermission.Camera ->
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)

                    EvidencePermission.Location -> locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                        ),
                    )
                }
            },
            dismissTitle = stringResource(R.string.action_not_now),
            onDismissRequest = { primingPermission = null },
        )
    }

    deniedPermission?.let { permission ->
        HigAlert(
            title = stringResource(permission.deniedTitleRes),
            message = stringResource(permission.deniedMessageRes),
            confirmTitle = stringResource(R.string.action_open_settings),
            onConfirm = {
                deniedPermission = null
                AppSettings.open(context)
            },
            dismissTitle = stringResource(R.string.action_not_now),
            onDismissRequest = { deniedPermission = null },
        )
    }

    if (confirmingDiscard) {
        HigActionSheet(
            title = stringResource(R.string.visit_discard_title),
            message = stringResource(R.string.visit_discard_message),
            actions = listOf(
                HigAction(
                    title = stringResource(R.string.action_discard),
                    destructive = true,
                    onSelect = {
                        confirmingDiscard = false
                        onDismiss()
                    },
                ),
            ),
            cancelTitle = stringResource(R.string.visit_keep_editing),
            onDismissRequest = { confirmingDiscard = false },
        )
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
            HigLargeTitle(text = stringResource(R.string.visit_title), subtitle = state.point.customer)
        }
        item {
            HigListSection(header = stringResource(R.string.visit_reading_header)) {
                HigValueRow(stringResource(R.string.point_installation), state.point.installationCode)
                HigRowSeparator()
                HigValueRow(stringResource(R.string.point_meter), state.point.meterNumber)
                HigRowSeparator()
                HigValueRow(stringResource(R.string.point_previous_reading), state.point.previousReading.toString())
                HigRowSeparator()
                HigRow {
                    Text(stringResource(R.string.visit_current_reading), style = HigTheme.typography.body, color = colors.label)
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
        header = stringResource(R.string.visit_evidence_header),
        footer = state.feedback?.let { stringResource(it.messageRes) },
        footerColor = if (state.feedback?.isFailure == true) colors.destructive else null,
    ) {
        EvidenceRow(
            label = stringResource(R.string.visit_photo_label),
            value = stringResource(
                if (state.photoUri == null) R.string.visit_photo_missing else R.string.visit_photo_attached,
            ),
            captured = state.photoUri != null,
        )
        HigRowSeparator()
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = HigMetrics.contentMargin, vertical = 12.dp),
        ) {
            HigBorderedButton(
                title = stringResource(
                    if (state.photoUri == null) R.string.visit_take_photo else R.string.visit_retake_photo,
                ),
                onClick = onTakePhoto,
                leading = { HigCameraSymbol(tint = colors.accent, size = 20.dp) },
            )
        }
        HigRowSeparator()
        EvidenceRow(
            label = stringResource(R.string.visit_location_label),
            value = state.location?.let {
                stringResource(
                    R.string.visit_coordinate_format,
                    it.latitude.formatCoordinate(),
                    it.longitude.formatCoordinate(),
                )
            } ?: stringResource(R.string.visit_location_missing),
            captured = state.location != null,
        )
        HigRowSeparator()
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = HigMetrics.contentMargin, vertical = 12.dp),
        ) {
            HigBorderedButton(
                title = stringResource(R.string.visit_use_location),
                onClick = onGetLocation,
                inProgress = state.locationLoading,
                progressLabel = stringResource(R.string.visit_location_in_progress),
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
        HigListSection(header = stringResource(R.string.visit_saved_header)) {
            HigValueRow(stringResource(R.string.visit_customer), state.point.customer)
            HigRowSeparator()
            HigValueRow(stringResource(R.string.visit_reading), state.reading.toString())
            HigRowSeparator()
            HigRow {
                Text(stringResource(R.string.point_status), style = HigTheme.typography.body, color = colors.label)
                Box(Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.sync_status_pending),
                    style = HigTheme.typography.body,
                    color = colors.secondaryLabel,
                )
            }
        }
        HigProminentButton(title = stringResource(R.string.action_done), onClick = onFinish)
    }
}

private fun Double.formatCoordinate(): String = "%.5f".format(this)
