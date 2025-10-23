package com.example.openiddemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.openiddemo.databinding.ActivityRedirectUriReceiver2Binding
import net.openid.appauth.AuthorizationManagementActivity

class RedirectUriReceiverActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRedirectUriReceiver2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*val intent = Intent(this, MainActivity::class.java)
        intent.data = getIntent().data
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()*/
        // while this does not appear to be achieving much, handling the redirect in this way
        // ensures that we can remove the browser tab from the back stack. See the documentation
        // on AuthorizationManagementActivity for more details.
        startActivity(
            AuthorizationManagementActivity.createResponseHandlingIntent(
                this,
                getIntent().getData().toString().replace(
                    "com.cloudapper.auth:/oauth2redirect#",
                    "com.cloudapper.auth:/oauth2redirect?"
                ).toUri()
            )
        )
        finish()
    }
}