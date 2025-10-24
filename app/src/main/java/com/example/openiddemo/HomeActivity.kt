package com.example.openiddemo

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.openiddemo.ui.theme.OpenIdDemoTheme
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.EndSessionRequest

class HomeActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private var accessToken = ""
    private var refreshToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = EncryptedSharedPreferences.create(
            this,
            PreferencesConstants.PREF_NAME,
            MasterKey.Builder(this).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        accessToken = prefs.getString(PreferencesConstants.KEY_ACCESS_TOKEN, "") ?: ""
        refreshToken = prefs.getString(PreferencesConstants.KEY_REFRESH_TOKEN, "") ?: ""

        enableEdgeToEdge()
        setContent {
            OpenIdDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    var isLoading by remember {
                        mutableStateOf(false)
                    }

                    var showAccessToken by remember {
                        mutableStateOf(false)
                    }

                    var showRefreshToken by remember {
                        mutableStateOf(false)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 32.dp),
                            text = "You successfully logged in",
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            modifier = Modifier.padding(top = 64.dp),
                            text = "Please tap the button to logout",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.tertiary
                        )

                        Button(
                            modifier = Modifier.padding(top = 32.dp),
                            enabled = !isLoading,
                            onClick = {
                                isLoading = true
                                logout()
                            }) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            } else {
                                Text(text = "Logout")
                            }
                        }

                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                            modifier = Modifier.padding(top = 64.dp),
                            onClick = {
                                showAccessToken = true
                            }) {
                            Text(text = "Show ACCESS Token")
                        }

                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                            modifier = Modifier.padding(top = 16.dp),
                            onClick = {
                                showRefreshToken = true
                            }) {
                            Text(text = "Show REFRESH Token")
                        }

                        if (showAccessToken || showRefreshToken) {
                            BasicAlertDialog(
                                title = if (showAccessToken) "Access Token" else "Refresh Token",
                                message = if (showAccessToken) accessToken else refreshToken,
                                onDismiss = {
                                    showAccessToken = false
                                    showRefreshToken = false
                                }
                            )
                        }
                    }

                }
            }
        }
    }

    private val logoutResponse = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            prefs.edit().clear().apply()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    fun logout() {
        val serviceConfiguration = AuthorizationServiceConfiguration(
            AuthConfig.AUTHORIZE_ENDPOINT.toUri(),
            AuthConfig.TOKEN_ENDPOINT.toUri(),
            null, // registration endpoint
            AuthConfig.END_SESSION_ENDPOINT.toUri()
        )

        val idToken = prefs.getString(PreferencesConstants.KEY_ID_TOKEN, null)

        val endSessionRequest = EndSessionRequest.Builder(
            serviceConfiguration,
        )
            .setIdTokenHint(idToken)
            .setPostLogoutRedirectUri(AuthConfig.SIGN_OUT_REDIRECT_URI.toUri())
            .build()

        val authService = AuthorizationService(this)

        val logoutIntent = authService.getEndSessionRequestIntent(endSessionRequest)
        logoutResponse.launch(logoutIntent)
    }

}