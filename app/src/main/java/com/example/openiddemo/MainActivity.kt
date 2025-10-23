package com.example.openiddemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.openiddemo.ui.theme.OpenIdDemoTheme
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration

class MainActivity : ComponentActivity() {

    private lateinit var authService: AuthorizationService

    private var authRequest: AuthorizationRequest? = null


    private val getAuthResponse =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val dataIntent = it.data ?: return@registerForActivityResult
            handleAuthResponseIntent(dataIntent)
        }


    private fun handleAuthResponseIntent(dataIntent: Intent) {
        val response = AuthorizationResponse.fromIntent(dataIntent)
        val ex = AuthorizationException.fromIntent(dataIntent)

        if (response != null) {
            val tokenRequest = response.createTokenExchangeRequest()

            authService.performTokenRequest(tokenRequest) { tokenResponse, tokenEx ->
                if (tokenResponse != null) {
                    val accessToken = tokenResponse.accessToken
                    val idToken = tokenResponse.idToken
                    val refreshToken = tokenResponse.refreshToken

                    Log.d("AuthDebug", "AccessToken: $accessToken")
                    Log.d("AuthDebug", "ID Token: $idToken")
                    Log.d("AuthDebug", "Refresh Token: $refreshToken")

                    saveTokens(accessToken, idToken, refreshToken)

                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Log.e("AuthDebug", "Token error", tokenEx)
                }
            }

        } else {
            Log.e("AuthDebug", "Authorization error", ex)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        authService.dispose()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authService = AuthorizationService(this)

        if (isLoggedIn()) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()
        setContent {
            OpenIdDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var isLoading by remember {
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
                            text = "Welcome",
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            modifier = Modifier.padding(top = 64.dp),
                            text = "Please tap the button to log in",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.tertiary
                        )

                        Button(
                            modifier = Modifier.padding(top = 32.dp),
                            enabled = !isLoading,
                            onClick = {
                                isLoading = true
                                startLogin()
                            }) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            } else {
                                Text(text = "Log In")
                            }
                        }
                    }
                }
            }
        }
    }


    private fun saveTokens(accessToken: String?, idToken: String?, refreshToken: String?) {
        val prefs = EncryptedSharedPreferences.create(
            this@MainActivity,
            "auth_prefs",
            MasterKey.Builder(this).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        prefs.edit().apply {
            putString("access_token", accessToken)
            putString("id_token", idToken)
            putString("refresh_token", refreshToken)
            apply()
        }
    }

    private fun isLoggedIn(): Boolean {
        val prefs = EncryptedSharedPreferences.create(
            this,
            "auth_prefs",
            MasterKey.Builder(this).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val accessToken = prefs.getString("access_token", null)
        return !accessToken.isNullOrEmpty()
    }

    fun getAuthRequest(): AuthorizationRequest {
        val serviceConfiguration = AuthorizationServiceConfiguration(
            AuthConfig.AuthorizeEndpoint.toUri(),
            AuthConfig.TokenEndpoint.toUri(),
            null, // registration endpoint
            AuthConfig.EndSessionEndpoint.toUri()
        )

        return AuthorizationRequest.Builder(
            serviceConfiguration,
            AuthConfig.ClientId,
            AuthConfig.ResponseType,
            AuthConfig.RedirectUri.toUri()
        )
            .setScope(AuthConfig.Scope)
            .build()
    }

    private fun startLogin() {
        if (authRequest == null) {
            authRequest = getAuthRequest()
        }

        Log.d("AuthDebug", "Authorization URL: ${authRequest!!.toUri()}")

        val authIntent = authService.getAuthorizationRequestIntent(authRequest!!)
        getAuthResponse.launch(authIntent)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OpenIdDemoTheme {
        Greeting("Android")
    }
}