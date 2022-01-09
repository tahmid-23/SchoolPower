package me.tahzam23.schoolpower.android.listener

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import io.ktor.client.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tahzam23.schoolpower.android.MainActivity
import me.tahzam23.schoolpower.android.R
import me.tahzam23.schoolpower.createDefaultClientConfig
import me.tahzam23.schoolpower.data.LoginInformation
import me.tahzam23.schoolpower.datetime.AndroidDateTimeFormatConverter
import me.tahzam23.schoolpower.html.AndroidDocumentCreator
import me.tahzam23.schoolpower.scraper.SchoolPowerScraper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SettingsButtonListener(private val app : Activity): View.OnClickListener {

    override fun onClick(p0: View?) {
        app.setContentView(R.layout.settings);
    }

}

class LoginButtonListener(
    private val schoolPowerScraper: SchoolPowerScraper,
    private val app : MainActivity
): View.OnClickListener {

    override fun onClick(p0: View?) {
        val client = HttpClient {
            createDefaultClientConfig(this)
        }

        app.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val success = try {
                    schoolPowerScraper.scrape(client, LoginInformation(
                        app.username.text.toString(),
                        app.password.text.toString()
                    )).forEach {
                        println(it)
                    }
                    schoolPowerScraper.keepAlive(client)

                    true
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
                client.close()

                app.runOnUiThread {
                    if (success) {
                        app.successText.text = "Success"
                        app.successText.setTextColor(Color.GREEN)
                        app.setContentView(R.layout.grades)
                        app.gradePageSetup()
                    }
                    else {
                        app.successText.text = "Incorrect Username or Password!"
                        app.successText.setTextColor(Color.RED)
                    }
                }
            }
        }
    }

}

class UpdateGradesButtonListener(private val app: MainActivity) : View.OnClickListener {

    override fun onClick(p0: View?) {
        val dtf1 = DateTimeFormatter.ofPattern("MM/dd")
        val dtf2 = DateTimeFormatter.ofPattern("HH:mm:ss")
        val now = LocalDateTime.now()
        val text = "Last Updated: " + dtf1.format(now) + " at " + dtf2.format(now)
        app.findViewById<TextView>(R.id.last_updated).text = text
    }

}