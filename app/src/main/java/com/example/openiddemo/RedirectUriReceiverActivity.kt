package com.example.openiddemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import net.openid.appauth.AuthorizationManagementActivity

class RedirectUriReceiverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // while this does not appear to be achieving much, handling the redirect in this way
        // ensures that we can remove the browser tab from the back stack. See the documentation
        // on AuthorizationManagementActivity for more details.
        startActivity(
            AuthorizationManagementActivity.createResponseHandlingIntent(
                this,
                intent.data.toString().replace(
                    "${AuthConfig.REDIRECT_URI}#",
                    "${AuthConfig.REDIRECT_URI}?"
                ).toUri()
            )
        )
        finish()
    }
}