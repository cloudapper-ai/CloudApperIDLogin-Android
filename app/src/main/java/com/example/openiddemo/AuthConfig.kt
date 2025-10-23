package com.example.openiddemo

object AuthConfig {
    const val RedirectUri = "com.cloudapper.auth:/oauth2redirect"
    const val SignOutRedirectUri = "com.cloudapper.auth2:/logout"

    const val AuthorizeEndpoint = "https://dev-account.cloudapper.com/connect/authorize"
    const val TokenEndpoint = "https://dev-account.cloudapper.com/connect/token"
    const val EndSessionEndpoint = "https://dev-account.cloudapper.com/connect/endsession"
    const val ClientId = "ko-android-app-v8"
    const val Scope = "openid email profile roles ko_webapi_v2 offline_access marketplace_api"
    const val ResponseType = "code id_token"
}