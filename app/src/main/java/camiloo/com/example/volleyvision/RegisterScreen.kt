package camiloo.com.example.volleyvision

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import camiloo.com.example.volleyvision.ui.theme.*

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    onBackToWelcome: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var clubName by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Entrenador") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Volver atrás
        TextButton(
            onClick = onBackToWelcome,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text("← Volver", color = TextGray, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("🏐", fontSize = 48.sp)

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Crear Cuenta",
            color = TextWhite,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Únete a VolleyApp y gestiona tu equipo",
            color = TextGray,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Campo Nombre Completo
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre Completo", color = TextGray) },
            singleLine = true,
            colors = textFieldColors,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Nombre del Club / Equipo (NUEVO)
        OutlinedTextField(
            value = clubName,
            onValueChange = { clubName = it },
            label = { Text("Nombre del Club / Equipo", color = TextGray) },
            placeholder = { Text("Ej. CV Olímpico", color = TextGray) },
            singleLine = true,
            colors = textFieldColors,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Selección de Rol
        Text(
            text = "Selecciona tu Rol:",
            color = TextGray,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FilterChip(
                selected = role == "Entrenador",
                onClick = { role = "Entrenador" },
                label = { Text("📋 Entrenador") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NeonGreen,
                    selectedLabelColor = BgMain,
                    containerColor = CardBg,
                    labelColor = TextWhite
                ),
                modifier = Modifier.weight(1f)
            )

            FilterChip(
                selected = role == "Jugador",
                onClick = { role = "Jugador" },
                label = { Text("🏃 Jugador") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NeonGreen,
                    selectedLabelColor = BgMain,
                    containerColor = CardBg,
                    labelColor = TextWhite
                ),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Correo
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico", color = TextGray) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = textFieldColors,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña", color = TextGray) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = textFieldColors,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Botón Registrarse
        Button(
            onClick = {
                if (name.isNotEmpty() && clubName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    Toast.makeText(context, "Cuenta creada para $clubName ($role)", Toast.LENGTH_SHORT).show()
                    onNavigateToHome()
                } else {
                    Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text(
                text = "Registrarme",
                color = BgMain,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToLogin) {
            Text(
                text = "¿Ya tienes cuenta? Inicia sesión",
                color = NeonGreen,
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    VolleyVisionTheme {
        RegisterScreen(
            onNavigateToLogin = {},
            onNavigateToHome = {},
            onBackToWelcome = {}
        )
    }
}

