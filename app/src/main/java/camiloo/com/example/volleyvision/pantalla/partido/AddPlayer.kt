package camiloo.com.example.volleyvision

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

// Catálogo de posiciones oficiales en voleibol disponibles para asignar al jugador
enum class PlayerPosition(val label: String) {
    PUNTA("Punta"),
    OPUESTO("Opuesto"),
    CENTRAL("Central"),
    COLOCADOR("Colocador"),
    LIBERO("Líbero")
}

// Modelo temporal que agrupa los datos ingresados en el formulario de registro de jugador
data class NewPlayerForm(
    val photoUri: Uri?,
    val name: String,
    val number: Int,
    val position: PlayerPosition,
    val heightM: Double?,
    val weightKg: Double?
)

// Panel inferior desplegable (ModalBottomSheet) para inscribir un nuevo deportista en el club
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlayerSheet(
    onDismiss: () -> Unit,
    onSave: (NewPlayerForm) -> Unit
) {
    // Estados reactivos de cada campo del formulario
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var name by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var position by remember { mutableStateOf(PlayerPosition.PUNTA) }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    // Selector nativo de galería de Android para cargar la foto de perfil del jugador
    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> photoUri = uri }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Añadir jugador", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            // Selector circular de fotografía de perfil
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            photoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (photoUri != null) {
                        AsyncImage(
                            model = photoUri,
                            contentDescription = "Foto del jugador",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text("📷\nSubir foto", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            // Campo de nombre completo del deportista
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre y apellido") },
                singleLine = true,
                isError = showError && name.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            // Fila con dorsal numérico (máximo 2 dígitos) y menú desplegable de posición táctica
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it.filter(Char::isDigit).take(2) },
                    label = { Text("Dorsal (#)") },
                    singleLine = true,
                    isError = showError && number.isBlank(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = position.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Posición") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        PlayerPosition.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.label) },
                                onClick = {
                                    position = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Datos biométricos opcionales (altura y peso)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it.filter { c -> c.isDigit() || c == '.' }.take(4) },
                    label = { Text("Altura (m)") },
                    placeholder = { Text("1.92") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it.filter { c -> c.isDigit() || c == '.' }.take(5) },
                    label = { Text("Peso (kg)") },
                    placeholder = { Text("82") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }

            // Validación de campos obligatorios y guardado en la plantilla del club
            Button(
                onClick = {
                    if (name.isBlank() || number.isBlank()) {
                        showError = true
                    } else {
                        onSave(
                            NewPlayerForm(
                                photoUri = photoUri,
                                name = name.trim(),
                                number = number.toInt(),
                                position = position,
                                heightM = height.toDoubleOrNull(),
                                weightKg = weight.toDoubleOrNull()
                            )
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Añadir jugador a la lista")
            }
        }
    }
}