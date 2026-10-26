package camiloo.com.example.volleyvision

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import camiloo.com.example.volleyvision.ui.theme.*

@Composable
fun NewMatchScreen(
    clubName: String = "CV Olímpico",
    onBackToHome: () -> Unit = {}
) {
    var rivalName by remember { mutableStateOf("") }
    var dateTime by remember { mutableStateOf("Hoy, 18:30h") }
    var location by remember { mutableStateOf("Pabellón Polideportivo Norte") }
    var selectedType by remember { mutableStateOf("Liga Oficial") }
    var isRecordingMode by remember { mutableStateOf(true) }

    val context = LocalContext.current

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = NeonGreen,
        unfocusedBorderColor = CardBorder,
        focusedLabelColor = NeonGreen,
        unfocusedLabelColor = TextGray
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgMain)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 1. Cabecera Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CardBg)
                        .border(1.dp, CardBorder, CircleShape)
                        .clickable { onBackToHome() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("←", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Nuevo Partido",
                    color = TextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                color = ButtonDarkBg,
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(NeonGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "JORNADA 14",
                        color = NeonGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Tarjeta Enfrentamiento (VS)
        Surface(
            color = CardBg,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Local
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.BottomCenter) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(ButtonDarkBg)
                                .border(1.dp, NeonGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏐", fontSize = 24.sp)
                        }
                        Surface(
                            color = NeonGreen,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "LOCAL",
                                color = BgMain,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(clubName, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Senior Masc A", color = Color(0xFF64B5F6), fontSize = 11.sp)
                }

                // VS
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ButtonDarkBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("VS", color = NeonGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("CANCHA 1", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                // Visita
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.BottomCenter) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(ButtonDarkBg)
                                .border(1.dp, CardBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🛡️", fontSize = 22.sp)
                        }
                        Surface(
                            color = CardBorder,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "VISITA",
                                color = TextWhite,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (rivalName.isEmpty()) "Rival" else rivalName,
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Por definir", color = TextGray, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Campo 1: Rival
        Text("🛡️  Nombre del Equipo Rival", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = rivalName,
            onValueChange = { rivalName = it },
            placeholder = { Text("e.g., CV San José", color = TextGray) },
            leadingIcon = { Text("🏃", fontSize = 16.sp) },
            singleLine = true,
            colors = fieldColors,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Campo 2: Fecha y Hora
        Text("📅  Fecha y Hora", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = dateTime,
            onValueChange = { dateTime = it },
            leadingIcon = { Text("🕒", fontSize = 16.sp) },
            singleLine = true,
            colors = fieldColors,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Campo 3: Lugar / Pabellón
        Text("📍  Lugar / Pabellón", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            leadingIcon = { Text("🏟️", fontSize = 16.sp) },
            singleLine = true,
            colors = fieldColors,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Tipo de Encuentro
        Text("🏆  Tipo de Encuentro", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val types = listOf("Liga Oficial", "Amistoso", "Torneo Copa")
            types.forEach { type ->
                val isSelected = selectedType == type
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedType = type },
                    label = { Text(type) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CardBg,
                        selectedLabelColor = NeonGreen,
                        containerColor = CardBg,
                        labelColor = TextGray
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = CardBorder,
                        selectedBorderColor = NeonGreen
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Modo de Registro
        Text("🎛️  Modo de Registro", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        // Opción 1: Video + Anotar
        Surface(
            color = CardBg,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isRecordingMode) NeonGreen else CardBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isRecordingMode = true }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📹", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Grabar Video + Anotar", color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text("Scouting inteligente con IA, clips de remates", color = TextGray, fontSize = 11.sp)
                        }
                    }
                    RadioButton(
                        selected = isRecordingMode,
                        onClick = { isRecordingMode = true },
                        colors = RadioButtonDefaults.colors(selectedColor = NeonGreen)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Opción 2: Solo Marcador
        Surface(
            color = CardBg,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (!isRecordingMode) NeonGreen else CardBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isRecordingMode = false }
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📊", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Solo Marcador y Puntos", color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text("Anotación táctica rápida sin almacenamiento de video", color = TextGray, fontSize = 11.sp)
                    }
                }
                RadioButton(
                    selected = !isRecordingMode,
                    onClick = { isRecordingMode = false },
                    colors = RadioButtonDefaults.colors(selectedColor = NeonGreen)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Alineación / Titulares
        Surface(
            color = CardBg,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("👥", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("6 Titulares asignados • 6 Suplentes", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Alineación 5-1 Confirmada", color = Color(0xFF64B5F6), fontSize = 11.sp)
                    }
                }
                Text("VER ›", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón Comenzar
        Button(
            onClick = {
                if (rivalName.isNotEmpty()) {
                    Toast.makeText(context, "Iniciando partido contra $rivalName", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Ingresa el nombre del equipo rival", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📹 ", fontSize = 18.sp)
                Text("Comenzar  ➔", color = BgMain, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun NewMatchScreenPreview() {
    VolleyVisionTheme {
        NewMatchScreen()
    }
}
