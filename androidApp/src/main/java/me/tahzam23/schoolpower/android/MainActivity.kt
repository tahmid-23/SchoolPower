package me.tahzam23.schoolpower.android

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import io.ktor.client.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.tahzam23.schoolpower.android.listener.LoginButtonListener
import me.tahzam23.schoolpower.android.listener.SettingsButtonListener
import me.tahzam23.schoolpower.createDefaultClientConfig
import me.tahzam23.schoolpower.data.LoginInformation
import me.tahzam23.schoolpower.login

class MainActivity : AppCompatActivity() {
    lateinit var username : EditText
        private set
    lateinit var password : EditText
        private set
    lateinit var successText : TextView
        private set
    lateinit var settingsButton : Button
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        username = findViewById<EditText>(R.id.schoolpower_username)
        password = findViewById<EditText>(R.id.schoolpower_password)
        successText = findViewById<TextView>(R.id.schoolpower_success)
        settingsButton = findViewById<Button>(R.id.settings)
        findViewById<Button>(R.id.schoolpower_submit_button).setOnClickListener(LoginButtonListener(this))
        findViewById<Button>(R.id.settings).setOnClickListener(SettingsButtonListener(this))
    }
}
