package camiloo.com.example.volleyvision

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import camiloo.com.example.volleyvision.ui.theme.*

@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgMain)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Spacer(modifier = Modifier.height(16.dp))

            // Insignia Superior
            Surface(
                color = ButtonDarkBg,
                shape = RoundedCornerShape(50.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Text(
                    text = "● VOLEIBOL TÁCTICO & LIVE",
                    color = NeonGreen,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Contenedor del Logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(ButtonDarkBg, shape = RoundedCornerShape(20.dp))
                    .border(1.dp, CardBorder, shape = RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🏐",
                    fontSize = 38.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Título
            Row {
                Text("Volley ", color = TextWhite, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text("App", color = NeonGreen, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Graba tus partidos, mueve tus rotaciones\ny analiza tu equipo de forma fácil.",
                color = TextGray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Tarjetas de funciones
            FeatureCard(
                iconSymbol = "📹",
                title = "Grabación Inteligente",
                subtitle = "Auto-tags de saques y remates"
            )

            Spacer(modifier = Modifier.height(10.dp))

            FeatureCard(
                iconSymbol = "📐",
                title = "Pizarra 2D Táctica",
                subtitle = "Rotaciones P1 a P6 en tiempo real"
            )

            Spacer(modifier = Modifier.height(10.dp))

            FeatureCard(
                iconSymbol = "📊",
                title = "Estadísticas Claras",
                subtitle = "% Kills, eficacia y mapa de calor"
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Botón Principal: Crear Mi Cuenta
            Button(
                onClick = onNavigateToRegister,
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = "Crear Mi Cuenta  ➔",
                    color = BgMain,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón Secundario: Iniciar Sesión
            OutlinedButton(
                onClick = onNavigateToLogin,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = ButtonDarkBg,
                    contentColor = TextWhite
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = "Ya tengo cuenta / Iniciar Sesión",
                    fontSize = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "🏆 Para entrenadores y clubes de voleibol amateur",
            color = TextGray,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun FeatureCard(iconSymbol: String, title: String, subtitle: String) {
    Surface(
        color = CardBg,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = iconSymbol,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = TextGray, fontSize = 12.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    VolleyVisionTheme {
        WelcomeScreen(
            onNavigateToLogin = {},
            onNavigateToRegister = {}
        )
    }
}

