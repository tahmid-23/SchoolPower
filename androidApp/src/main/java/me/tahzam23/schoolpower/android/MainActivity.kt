package me.tahzam23.schoolpower.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import io.ktor.client.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.tahzam23.schoolpower.createDefaultClientConfig
import me.tahzam23.schoolpower.data.LoginInformation
import me.tahzam23.schoolpower.login

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val client = HttpClient {
            createDefaultClientConfig(this)
        }

        val username = findViewById<EditText>(R.id.schoolpower_username)
        val password = findViewById<EditText>(R.id.schoolpower_password)
        findViewById<Button>(R.id.schoolpower_submit_button).setOnClickListener {
            GlobalScope.launch {
                login(
                    client = client,
                    loginInformation = LoginInformation(
                        username.text.toString(),
                        password.text.toString()
                    )
                )
            }
        }
    }
}
