package com.example.openiddemo

object AuthConfig {
    const val BASE_URL =
        "{YOUR_BASE_URL}"  // The base URL of the identity provider (authorization server).

    const val REDIRECT_URI =
        "{YOUR_LOGIN_REDIRECT_URI}" // The redirect URI the provider sends the authorization code to (must be registered).

    const val SIGN_OUT_REDIRECT_URI =
        "{YOUR_LOGOUT_REDIRECT_URI}" // The redirect URI the provider sends the user to after logging out.

    const val AUTHORIZE_ENDPOINT =
        "$BASE_URL/connect/authorize" // The path to the authorization endpoint, appended to baseUrl.

    const val TOKEN_ENDPOINT =
        "$BASE_URL/connect/token" // The path to the token endpoint, used for exchanging codes for tokens.

    const val END_SESSION_ENDPOINT =
        "$BASE_URL/connect/endsession" //The path to the end session (logout) endpoint.

    const val CLIENT_ID = "{YOUR_CLIENT_ID}" // The unique identifier for this client application.

    const val SCOPE =
        "openid profile email offline_access" // The list of permissions (scopes) the app is requesting. Please give correct scopes according to your authorization server

    const val RESPONSE_TYPE =
        "code" // Please put correct response type according to your authorization server
}