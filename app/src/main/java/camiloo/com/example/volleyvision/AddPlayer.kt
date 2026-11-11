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

enum class PlayerPosition(val label: String) {
    PUNTA("Punta"),
    OPUESTO("Opuesto"),
    CENTRAL("Central"),
    COLOCADOR("Colocador"),
    LIBERO("Líbero")
}

data class NewPlayerForm( //Una caja que agrupa los datos del jugador
    val photoUri: Uri?, // ? Significa que el dato puede ser null (no tiene foto, altura, etc)
    val name: String,
    val number: Int,
    val position: PlayerPosition,
    val heightM: Double?,
    val weightKg: Double?
)

@OptIn(ExperimentalMaterial3Api::class) //Se guarda como experimental se puede decir si,
@Composable  //puede cambiar en el futuro pero quiero usarlo
fun AddPlayerSheet(
    onDismiss: () -> Unit,
    onSave: (NewPlayerForm) -> Unit
) { //Estados: Guarda lo que el usuario va escribiendo
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var name by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var position by remember { mutableStateOf(PlayerPosition.PUNTA) }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) } //Si no estuviera la pantalla no se actualiza
    var showError by remember { mutableStateOf(false) } //La pantalla se actualiza

    val photoPicker = rememberLauncherForActivityResult( //Le dice a Android que abra la galería para la foto
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> photoUri = uri } //Lambda que recibe el resultado //Cambia de subir foto a la foto seleccionada
// uri: dirección de la foto elegida
    ModalBottomSheet( //El panel que sube desde abajo
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true) //Se abre completo de una vez
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth() //Ocupa_todo el ancho
                .verticalScroll(rememberScrollState()) //hace scroll
                .padding(horizontal = 20.dp) //Espacio a izquierda y derecha
                .padding(bottom = 32.dp), //Espacio abajo
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Añadir jugador", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) //Negrita

            // Foto
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape) //Le da la forma de circulo
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { //Hace que se pueda tocar (al tocar se abre la galeria)
                            photoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (photoUri != null) {
                        AsyncImage( //Lib Coil - Carga imagen
                            model = photoUri, // de dónde sacar la imagen
                            contentDescription = "Foto del jugador",
                            contentScale = ContentScale.Crop, // la recorta para llenar el círculo sin deformarla
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text("📷\nSubir foto", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it }, // cada vez que el usuario escribe, guarda el texto nuevo
                label = { Text("Nombre y apellido") },
                singleLine = true,  // una sola línea (no deja dar Enter)
                isError = showError && name.isBlank(), // Da error si intentó guardar Y está vacío
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {  // Row = acomoda hijos de forma horizontal.
                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it.filter(Char::isDigit).take(2) },
                    label = { Text("Dorsal (#)") },
                    singleLine = true,
                    isError = showError && number.isBlank(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                // Dropdown de posición
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