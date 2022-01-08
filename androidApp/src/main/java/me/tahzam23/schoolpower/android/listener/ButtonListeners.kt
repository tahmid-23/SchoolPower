package me.tahzam23.schoolpower.android.listener

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.ktor.client.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.tahzam23.schoolpower.android.MainActivity
import me.tahzam23.schoolpower.android.R
import me.tahzam23.schoolpower.createDefaultClientConfig
import me.tahzam23.schoolpower.data.LoginInformation
import me.tahzam23.schoolpower.html.AndroidDocumentCreator
import me.tahzam23.schoolpower.login
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SettingsButtonListener(private val app : AppCompatActivity) : View.OnClickListener {
    override fun onClick(p0: View?) {
        app.setContentView(R.layout.settings);
    }

}

class LoginButtonListener(private val app : MainActivity) : View.OnClickListener {
    override fun onClick(p0: View?) {
        val client = HttpClient {
            createDefaultClientConfig(this)
        }

        GlobalScope.launch {
            val success = login(
                client = client,
                loginInformation = LoginInformation(
                    app.username.text.toString(),
                    app.password.text.toString()
                ),
                documentCreator = AndroidDocumentCreator()
            )

            app.runOnUiThread {
                if (success) {
                    app.successText.text = "Success"
                    app.successText.setTextColor(Color.GREEN)
                    app.setContentView(R.layout.grades)
                }
                else {
                    app.successText.text = "Incorrect Username or Password!"
                    app.successText.setTextColor(Color.RED)
                }
            }
        }
    }
}

class UpdateGradesButtonListener(private val app : MainActivity) : View.OnClickListener {
    override fun onClick(p0: View?) {
        val dtf = DateTimeFormatter.ofPattern("MM/dd at HH:mm:ss")
        val now = LocalDateTime.now()
        val text = "Last Updated: " + dtf.format(now)
        app.findViewById<TextView>(R.id.last_updated).text = text
    }

}