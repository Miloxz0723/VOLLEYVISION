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
fun PlayerHomeScreen(
    playerName: String = "Mateo Silva",
    clubName: String = "CV OLÍMPICO"
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Inicio, 1: Agenda, 2: Mis Stats, 3: Feedback
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            Surface(
                color = CardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PlayerNavItem(icon = "🏠", label = "Inicio", isSelected = selectedTab == 0) { selectedTab = 0 }
                    PlayerNavItem(icon = "📅", label = "Agenda", isSelected = selectedTab == 1) { selectedTab = 1 }
                    PlayerNavItem(icon = "📈", label = "Mis Stats", isSelected = selectedTab == 2) { selectedTab = 2 }
                    PlayerNavItem(icon = "💬", label = "Feedback", isSelected = selectedTab == 3) { selectedTab = 3 }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BgMain)
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Cabecera Top Bar del Jugador
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(ButtonDarkBg)
                            .border(1.5.dp, NeonGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("#10", color = NeonGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Hola, $playerName 👋", color = TextWhite, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        Text("$clubName • Punta Receptor", color = Color(0xFF64B5F6), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(ButtonDarkBg)
                        .border(1.dp, CardBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🔔", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Renderizado de Pestañas
            when (selectedTab) {
                0 -> PlayerHomeTab(clubName = clubName, context = context)
                1 -> PlayerAgendaTab(clubName = clubName, context = context)
                2 -> PlayerStatsTab()
                3 -> PlayerFeedbackTab()
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ----------------------------------------------------
// PESTAÑA 1: INICIO JUGADOR
// ----------------------------------------------------
@Composable
fun PlayerHomeTab(clubName: String, context: android.content.Context) {
    var isConfirmed by remember { mutableStateOf(false) }

    Column {
        Text("PRÓXIMO ENCUENTRO", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            color = CardBg,
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = ButtonDarkBg,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("SÁBADO 18:30H", color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                    Text("Liga Senior • Fecha 8", color = TextGray, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("$clubName vs CV San José", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("📍 Pabellón Polideportivo Norte", color = TextGray, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        isConfirmed = !isConfirmed
                        val status = if (isConfirmed) "Asistencia Confirmada ✅" else "Asistencia Cancelada ❌"
                        Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isConfirmed) Color(0xFF00381B) else NeonGreen
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isConfirmed) "✓ Asistencia Confirmada" else "Confirmar Mi Asistencia 🙋‍♂️",
                        color = if (isConfirmed) NeonGreen else BgMain,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Tarjeta resumen rápido de rendimiento
        Text("RESUMEN DE TEMPORADA", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PlayerStatBox(label = "Ataques Efectivos", value = "62%", icon = "💥", modifier = Modifier.weight(1f))
            PlayerStatBox(label = "Puntos Totales", value = "142", icon = "🏐", modifier = Modifier.weight(1f))
        }
    }
}

// ----------------------------------------------------
// PESTAÑA 2: AGENDA Y ENTRENAMIENTOS
// ----------------------------------------------------
@Composable
fun PlayerAgendaTab(clubName: String, context: android.content.Context) {
    Column {
        Text("📅  AGENDA DEL EQUIPO", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("Entrenamientos y partidos programados para $clubName", color = TextGray, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(16.dp))

        EventItem(
            tag = "HOY 19:30",
            title = "Entrenamiento Táctico (Sistema 5-1)",
            subtitle = "Pabellón Municipal • Cancha Central",
            isMatch = false
        )

        Spacer(modifier = Modifier.height(10.dp))

        EventItem(
            tag = "SÁB 18:30",
            title = "$clubName vs CV San José",
            subtitle = "Pabellón Polideportivo Norte • Liga Oficial",
            isMatch = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        EventItem(
            tag = "LUN 18:00",
            title = "Acondicionamiento Físico + Saque",
            subtitle = "Gimnasio del Club • Pista A",
            isMatch = false
        )
    }
}

// ----------------------------------------------------
// PESTAÑA 3: MIS ESTADÍSTICAS
// ----------------------------------------------------
@Composable
fun PlayerStatsTab() {
    Column {
        Text("📈  MIS ESTADÍSTICAS", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("Rendimiento personal acumulado en la liga", color = TextGray, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PlayerStatBox(label = "Puntos por Partido", value = "14.5", icon = "🔥", modifier = Modifier.weight(1f))
            PlayerStatBox(label = "Eficacia Recepción", value = "74%", icon = "🛡️", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PlayerStatBox(label = "Aces Directos", value = "18", icon = "⚡", modifier = Modifier.weight(1f))
            PlayerStatBox(label = "Bloqueos Puntuales", value = "12", icon = "🚫", modifier = Modifier.weight(1f))
        }
    }
}

// ----------------------------------------------------
// PESTAÑA 4: FEEDBACK DEL ENTRENADOR
// ----------------------------------------------------
@Composable
fun PlayerFeedbackTab() {
    Column {
        Text("💬  FEEDBACK DEL ENTRENADOR", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("Comentarios tácticos y recomendaciones del cuerpo técnico", color = TextGray, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            color = CardBg,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📋 Coach Sanabria", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("• Hace 2 días", color = TextGray, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    "\"Excelente efectividad en el remate por posición 4 en el último set. Debemos ajustar la diagonal corta en recepción de saque flotante para el próximo partido.\"",
                    color = TextWhite.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

// ----------------------------------------------------
// COMPONENTES AUXILIARES
// ----------------------------------------------------
@Composable
fun PlayerStatBox(label: String, value: String, icon: String, modifier: Modifier = Modifier) {
    Surface(
        color = CardBg,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(label, color = TextGray, fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, color = NeonGreen, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun EventItem(tag: String, title: String, subtitle: String, isMatch: Boolean) {
    Surface(
        color = CardBg,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = if (isMatch) Color(0xFF00381B) else ButtonDarkBg,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = tag,
                    color = if (isMatch) NeonGreen else TextWhite,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(title, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = TextGray, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun PlayerNavItem(icon: String, label: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(icon, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isSelected) NeonGreen else TextGray,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlayerHomeScreenPreview() {
    VolleyVisionTheme {
        PlayerHomeScreen()
    }
}

