package camiloo.com.example.volleyvision

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun CameraScreen(onClose: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = context as LifecycleOwner

    fun isGranted(permission: String) =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    // Permisos de cámara y micrófono
    var hasPermission by remember {
        mutableStateOf(isGranted(Manifest.permission.CAMERA) && isGranted(Manifest.permission.RECORD_AUDIO))
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result -> hasPermission = result.values.all { it } }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
            )
        }
    }

    var videoCapture by remember { mutableStateOf<VideoCapture<Recorder>?>(null) }
    var recording by remember { mutableStateOf<Recording?>(null) }
    var isRecording by remember { mutableStateOf(false) }
    var seconds by remember { mutableIntStateOf(0) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var closeRequested by remember { mutableStateOf(false) }

    // Cronómetro
    LaunchedEffect(isRecording) {
        seconds = 0
        while (isRecording) {
            delay(1000) //Espera 1 segundo pa subir 1
            seconds++ //Sube 1
        }
    }

    // Si se sale de la pantalla grabando, se detiene y se guarda
    DisposableEffect(Unit) {
        onDispose {
            recording?.stop()
            cameraProvider?.unbindAll()
        }
    }

    fun startRecording() {
        val vc = videoCapture ?: return
        val name = "Partido_" + SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US)
            .format(System.currentTimeMillis())

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Queda en la galería, carpeta Movies/VolleyVision
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/VolleyVision")
            }
        }

        val output = MediaStoreOutputOptions.Builder(
            context.contentResolver,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        ).setContentValues(values).build()

        recording = vc.output
            .prepareRecording(context, output)
            .withAudioEnabled()
            .start(ContextCompat.getMainExecutor(context)) { event ->
                if (event is VideoRecordEvent.Finalize) {
                    isRecording = false
                    recording = null
                    val msg = if (event.hasError()) "Error al guardar el video"
                    else "Video guardado en la galería"
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    if (closeRequested) onClose()
                }
            }
        isRecording = true
    }

    fun stopRecording() {
        recording?.stop()
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {

        if (hasPermission) {
            AndroidView(
                modifier = Modifier.fillMaxSize(), //Ocupa toda la pantalla
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val providerFuture = ProcessCameraProvider.getInstance(ctx)
                    providerFuture.addListener({
                        val provider = providerFuture.get() //Permiso de la cámara
                        cameraProvider = provider //Se guarda para apagarla después

                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val recorder = Recorder.Builder()
                            .setQualitySelector( //Elige Calidad
                                QualitySelector.from(
                                    Quality.HD, //Estándar de 720p
                                    FallbackStrategy.lowerQualityOrHigherThan(Quality.SD)
                                )
                            )
                            .build()
                        val vc = VideoCapture.withOutput(recorder)

                        provider.unbindAll()
                        provider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA, //Cámara trasera
                            preview, //Vista previa
                            vc //Grabar
                        )
                        videoCapture = vc
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                }
            )
        } else { //Si no dan permisos
            Text(
                "Necesito permiso de cámara y micrófono para grabar",
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp)
            )
        }

        // Botón cerrar
        Button(
            onClick = {
                if (isRecording) {
                    closeRequested = true
                    stopRecording()
                } else {
                    onClose()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.6f)),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) { Text("Cerrar", color = Color.White) }

        // Cronómetro
        if (isRecording) {
            Text(
                text = "● REC  %02d:%02d".format(seconds / 60, seconds % 60),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp) //Espacio a arriba
                    .background(Color(0xCCD32F2F), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        // Botón grabar
        if (hasPermission) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp) //Espacio libre al borde abajo
                    .size(76.dp) //Tamaño Botón
                    .clip(CircleShape)
                    .border(4.dp, Color.White, CircleShape)
                    .clickable { if (isRecording) stopRecording() else startRecording() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(if (isRecording) 28.dp else 56.dp) //Botón Rojo
                        .clip(if (isRecording) RoundedCornerShape(6.dp) else CircleShape)  //Circulo - Cuadrado
                        .background(Color(0xFFE53935))
                )
            }
        }
    }
}