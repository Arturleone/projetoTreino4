package com.example.arturmodulo4

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.arturmodulo4.HomeActivity
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {

    private var tentativas = 0
    private var isAuthenticationCancelled = false
    private var biometricPrompt: BiometricPrompt? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)

        if (isBiometricAvailable()) {
            val executor: Executor = ContextCompat.getMainExecutor(this)
            biometricPrompt = BiometricPrompt(
                this,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        tentativas = 0
                        startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        tentativas++
                        if (tentativas >= 3) {
                            isAuthenticationCancelled = true
                            biometricPrompt?.cancelAuthentication()
                        } else {
                        }
                    }
                })

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Autenticação Biométrica")
                .setSubtitle("Use sua biometria para continuar")
                .setNegativeButtonText("Cancelar")
                .build()

            if (!isAuthenticationCancelled) {
                biometricPrompt?.authenticate(promptInfo)
            }
        }
        else {
            Toast.makeText(this, "Biometria não disponível neste dispositivo.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(this)
        return biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS
    }

}
