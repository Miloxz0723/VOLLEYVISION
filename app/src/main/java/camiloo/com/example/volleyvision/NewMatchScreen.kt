package camiloo.com.example.volleyvision

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    onBackToHome: () -> Unit = {},
    onMatchCreated: (String, String, String, String) -> Unit = { _, _, _, _ -> }
) {
    var rivalName by remember { mutableStateOf("") }
    var matchDate by remember { mutableStateOf("Hoy, 18:30h") }
    var location by remember { mutableStateOf("Pabellón Polideportivo Norte") }
    var matchType by remember { mutableStateOf("Liga Oficial") } // "Liga Oficial", "Amistoso", "Torneo Copa"
    var recordingMode by remember { mutableStateOf(2) } // 1: Grabar, 2: Solo Marcador, 3: Solo Agendar
    val context = LocalContext.current

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = NeonGreen,
        unfocusedBorderColor = CardBorder,
        focusedLabelColor = NeonGreen,
        unfocusedLabelColor = TextGray,
        focusedTextColor = TextWhite,
        unfocusedTextColor = TextWhite
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgMain)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Botón Volver Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBackToHome) {
                Text("← Volver", color = TextGray, fontSize = 14.sp)
            }
            Text("NUEVO ENCUENTRO", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(48.dp)) // Equilibrio visual
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tarjeta Banner VS
        Surface(
            color = CardBg,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("LOCAL", color = Color(0xFF64B5F6), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(clubName, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)

                }

                Text("VS", color = NeonGreen, fontSize = 20.sp, fontWeight = FontWeight.Black)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("VISITA", color = Color(0xFF64B5F6), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(if (rivalName.isEmpty()) "Rival" else rivalName, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)

                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 1. Nombre Rival
        Text("🛡️ Nombre del Equipo Rival", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = rivalName,
            onValueChange = { rivalName = it },
            placeholder = { Text("e.g., CV San José", color = TextGray) },
            singleLine = true,
            colors = textFieldColors,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Fecha y Hora
        Text("📅 Fecha y Hora", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = matchDate,
            onValueChange = { matchDate = it },
            singleLine = true,
            colors = textFieldColors,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Lugar / Pabellón
        Text("📍 Lugar / Pabellón", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            singleLine = true,
            colors = textFieldColors,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 4. Tipo de Encuentro
        Text("🏆 Tipo de Encuentro", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MatchTypeChip("Liga Oficial", isSelected = matchType == "Liga Oficial", modifier = Modifier.weight(1f)) { matchType = "Liga Oficial" }
            MatchTypeChip("Amistoso", isSelected = matchType == "Amistoso", modifier = Modifier.weight(1f)) { matchType = "Amistoso" }
            MatchTypeChip("Torneo Copa", isSelected = matchType == "Torneo Copa", modifier = Modifier.weight(1f)) { matchType = "Torneo Copa" }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 5. Modo de Registro / Programación
        Text("🎛️ Modo de Registro", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        // Opción 1: Grabar Video + Anotar
        ModeOptionCard(
            title = "Grabar Video + Anotar",
            subtitle = "Scouting inteligente con IA, clips de remates",
            icon = "📹",
            isSelected = recordingMode == 1
        ) { recordingMode = 1 }

        Spacer(modifier = Modifier.height(10.dp))

        // Opción 2: Solo Marcador y Puntos
        ModeOptionCard(
            title = "Solo Marcador y Puntos",
            subtitle = "Anotación táctica rápida sin almacenamiento de video",
            icon = "📊",
            isSelected = recordingMode == 2
        ) { recordingMode = 2 }

        Spacer(modifier = Modifier.height(10.dp))

        // Opción 3: Solo Agendar Partido (NUEVO)
        ModeOptionCard(
            title = "Solo Agendar Partido",
            subtitle = "Publicar evento en la agenda de los jugadores para asistencia",
            icon = "📅",
            isSelected = recordingMode == 3
        ) { recordingMode = 3 }

        Spacer(modifier = Modifier.height(24.dp))

        // Alineación / Jugadores Asignados
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
                Text("VER ›", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón Dinámico de Acción
        Button(
            onClick = {
                val rival = if (rivalName.isEmpty()) "Rival por Definir" else rivalName

                // Llama al callback para guardar los datos ingresados en el ViewModel
                onMatchCreated(rival, matchDate, location, matchType)

                if (recordingMode == 3) {
                    Toast.makeText(context, "Partido contra $rival agendado 📅", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Iniciando partido contra $rival 🏐", Toast.LENGTH_SHORT).show()
                }
            },

            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text(
                text = if (recordingMode == 3) "Guardar en Agenda 📅" else "Comenzar ➔",
                color = BgMain,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

// Componentes Auxiliares
@Composable
fun MatchTypeChip(label: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        color = if (isSelected) ButtonDarkBg else CardBg,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) NeonGreen else CardBorder),
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (isSelected) NeonGreen else TextWhite,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ModeOptionCard(title: String, subtitle: String, icon: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        color = CardBg,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) NeonGreen else CardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(icon, fontSize = 22.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(title, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(subtitle, color = TextGray, fontSize = 11.sp)
                }
            }

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = NeonGreen,
                    unselectedColor = TextGray
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewMatchScreenPreview() {
    VolleyVisionTheme {
        NewMatchScreen()
    }
}