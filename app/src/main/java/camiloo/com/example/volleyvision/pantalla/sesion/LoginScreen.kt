package camiloo.com.example.volleyvision

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

// Pantalla de inicio de sesión para usuarios ya registrados (Entrenador o Jugador)[cite: 12]
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginAttempt: (email: String, pass: String, onComplete: (Boolean, String?) -> Unit) -> Unit = { _, _, cb -> cb(true, null) },
    onNavigateToHome: () -> Unit,
    onBackToWelcome: () -> Unit
) {
    // Estados reactivos del formulario y control de carga mientras responde Firebase[cite: 12]
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val fieldColors = OutlinedTextFieldDefaults.colors(
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

        // Botón para regresar a la pantalla de bienvenida[cite: 12]
        TextButton(
            onClick = onBackToWelcome,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text("← Volver", color = TextGray, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("🏐", fontSize = 48.sp)

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Iniciar Sesión",
            color = TextWhite,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Ingresa tus credenciales para acceder",
            color = TextGray,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campos de entrada para correo electrónico y contraseña oculta[cite: 12]
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = fieldColors,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = fieldColors,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Botón de acceso: valida campos vacíos y autentica mediante Firebase Auth[cite: 12]
        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                } else {
                    isLoading = true
                    onLoginAttempt(email, password) { success, error ->
                        isLoading = false
                        if (success) {
                            Toast.makeText(context, "Bienvenido de nuevo", Toast.LENGTH_SHORT).show()
                            onNavigateToHome()
                        } else {
                            Toast.makeText(
                                context,
                                error ?: "Correo o contraseña incorrectos",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            },
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text(
                text = if (isLoading) "Verificando..." else "Ingresar",
                color = BgMain,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Enlace directo hacia el formulario de registro de cuenta nueva[cite: 12]
        TextButton(onClick = onNavigateToRegister) {
            Text(
                text = "¿No tienes cuenta? Regístrate aquí",
                color = NeonGreen,
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    VolleyVisionTheme {
        LoginScreen(
            onNavigateToRegister = {},
            onNavigateToHome = {},
            onBackToWelcome = {}
        )
    }
}
