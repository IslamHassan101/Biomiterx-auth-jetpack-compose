package com.islam.biomiterx_auth_jetpack_compose

import android.Manifest
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.islam.biomiterx_auth_jetpack_compose.ui.theme.BiomiterxauthjetpackcomposeTheme


class MainActivity : ComponentActivity() {
    private var cancellationSignal: CancellationSignal? = null

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BiomiterxauthjetpackcomposeTheme {
                BiometricScreen(onButtonClicked = { lunchBiometric() })
            }
        }
    }

    private val authenticationCallBack: BiometricPrompt.AuthenticationCallback
        get() = @RequiresApi(Build.VERSION_CODES.P) object :
            BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                notifyUser("Authentication Error $errorCode")
                Toast.makeText(applicationContext, "Auth Error", Toast.LENGTH_SHORT).show()
                super.onAuthenticationError(errorCode, errString)
            }

            override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
                super.onAuthenticationHelp(helpCode, helpString)
            }

            override fun onAuthenticationFailed() {
                Toast.makeText(applicationContext, "Auth failed", Toast.LENGTH_SHORT).show()
                super.onAuthenticationFailed()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                notifyUser(" authentication Succeeded")
                Toast.makeText(applicationContext, "Auth Succeeded", Toast.LENGTH_SHORT).show()
                super.onAuthenticationSucceeded(result)
            }
        }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun checkedBiometricSupport(): Boolean {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (!keyguardManager.isDeviceSecure) {
            notifyUser("lock screen security not enable in setting")
            return false
        }
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.USE_BIOMETRIC
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notifyUser("Finger print authentication permission not enable")
            return false
        }
        return packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun lunchBiometric() {
        if (checkedBiometricSupport()) {
            val biometricPrompt = BiometricPrompt
                .Builder(this)
                .setTitle("Allow Biometric Authentication")
                .setSubtitle("You will no longer required username and password during login")
                .setDescription("we use biometric authentication to protect your data")
                .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                .build()
            biometricPrompt.authenticate(
                getCancellationSignal(), mainExecutor, authenticationCallBack
            )
        }
    }

    private fun getCancellationSignal(): CancellationSignal {
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            notifyUser("Ath canceled via signal")
        }
        return cancellationSignal as CancellationSignal
    }

    private fun notifyUser(message: String) {
        Log.d("BIOMETRIC", message)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @Composable
    fun BiometricScreen(onButtonClicked: () -> Unit) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = onButtonClicked) {
                Text(text = "FingerPrint")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @Preview(showBackground = true)
    @Composable
    fun BiometricScreenPreview() {
        BiometricScreen {

        }
    }

}

