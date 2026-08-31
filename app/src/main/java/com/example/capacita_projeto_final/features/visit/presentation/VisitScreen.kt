package com.example.capacita_projeto_final.features.visit.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.capacita_projeto_final.features.visit.infrastructure.PhotoEvidenceStore
import com.example.capacita_projeto_final.ui.theme.Muted
import com.example.capacita_projeto_final.ui.theme.Success

@Composable
fun VisitScreen(
    state: VisitUiState,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onFinish: () -> Unit,
    onPhotoCaptured: (String?) -> Unit,
    onEvidenceMessage: (String) -> Unit,
    onCaptureLocation: () -> Unit,
) {
    val context = LocalContext.current
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
            .onFailure { onEvidenceMessage("Não foi possível preparar o arquivo da foto.") }
        Unit
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) launchCamera() else onEvidenceMessage("Permissão de câmera não concedida.")
    }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) onCaptureLocation() else onEvidenceMessage("Permissão de localização não concedida.")
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
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        if (fineGranted || coarseGranted) {
            onCaptureLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
            )
        }
        Unit
    }

    Scaffold { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedButton(onClick = onBack) { Text("‹ Ponto") }
            Text("Registro de visita", style = MaterialTheme.typography.labelLarge, color = Muted)
            when (state) {
                VisitUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                is VisitUiState.Error -> ErrorContent(state.message, onBack)
                is VisitUiState.Ready -> ReadyContent(
                    state = state,
                    onSave = onSave,
                    onTakePhoto = requestPhoto,
                    onGetLocation = requestLocation,
                )
                is VisitUiState.Saved -> SavedContent(state, onFinish)
            }
        }
    }
}

@Composable
private fun ReadyContent(
    state: VisitUiState.Ready,
    onSave: () -> Unit,
    onTakePhoto: () -> Unit,
    onGetLocation: () -> Unit,
) {
    Text(state.point.customer, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
    ) {
        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Resumo", fontWeight = FontWeight.Bold)
            Text("Instalação: ${state.point.installationCode}", color = Muted)
            Text("Medidor: ${state.point.meterNumber}", color = Muted)
            Text("Leitura anterior: ${state.point.previousReading}", color = Muted)
            Text("Leitura atual: ${state.reading}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
    ) {
        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Evidências do dispositivo", fontWeight = FontWeight.Bold)
            Text(
                state.photoUri?.let { "Foto do medidor anexada" } ?: "Foto ainda não capturada",
                color = if (state.photoUri == null) Muted else Success,
            )
            OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = onTakePhoto) {
                Text(if (state.photoUri == null) "Capturar foto" else "Capturar outra foto")
            }
            Text(
                state.location?.let { "${it.latitude.formatCoordinate()}, ${it.longitude.formatCoordinate()}" }
                    ?: "Localização ainda não capturada",
                color = if (state.location == null) Muted else Success,
            )
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.locationLoading,
                onClick = onGetLocation,
            ) {
                Text(if (state.locationLoading) "Obtendo localização…" else "Usar localização atual")
            }
            state.evidenceMessage?.let { Text(it, color = Muted, style = MaterialTheme.typography.bodySmall) }
        }
    }
    Text(
        "A visita será gravada no Room e ficará disponível mesmo sem conexão.",
        color = Muted,
    )
    Button(
        modifier = Modifier.fillMaxWidth(),
        enabled = !state.saving,
        onClick = onSave,
    ) {
        if (state.saving) {
            CircularProgressIndicator(
                modifier = Modifier.height(20.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp,
            )
        } else {
            Text("Salvar visita no dispositivo")
        }
    }
}

private fun Double.formatCoordinate(): String = "%.6f".format(this)

@Composable
private fun SavedContent(state: VisitUiState.Saved, onFinish: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Success.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(18.dp),
    ) {
        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Visita salva", style = MaterialTheme.typography.headlineSmall, color = Success, fontWeight = FontWeight.Bold)
            Text("${state.point.customer} · leitura ${state.reading}")
            Text("Status: aguardando sincronização", color = Muted)
        }
    }
    Button(modifier = Modifier.fillMaxWidth(), onClick = onFinish) { Text("Voltar para a rota") }
}

@Composable
private fun ErrorContent(message: String, onBack: () -> Unit) {
    Spacer(Modifier.height(24.dp))
    Text("Não foi possível concluir", style = MaterialTheme.typography.headlineSmall)
    Text(message, color = MaterialTheme.colorScheme.error)
    OutlinedButton(onClick = onBack) { Text("Voltar") }
}
