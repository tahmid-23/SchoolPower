package me.tahzam23.schoolpower.android.listener

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import io.ktor.client.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import me.tahzam23.schoolpower.android.MainActivity
import me.tahzam23.schoolpower.android.R
import me.tahzam23.schoolpower.createDefaultClientConfig
import me.tahzam23.schoolpower.data.LoginInformation
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
        app.successText.text = "Logging in..."
        val animation = LoginAnimationThread(app)
        animation.start()
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
                        animation.stopAnimation()
                        app.successText.text = "Success"
                        app.successText.setTextColor(Color.GREEN)
                        app.setContentView(R.layout.grades)
                        app.gradePageSetup()
                    }
                    else {
                        animation.stopAnimation()
                        app.successText.text = "Incorrect Username or Password!"
                        app.successText.setTextColor(Color.RED)
                    }
                }
            }
        }
    }

    class LoginAnimationThread(private val app: MainActivity) : Thread() {

        private var condition = true

        @Override
        override fun run() {
            app.runOnUiThread {
                app.successText.setTextColor(app.defaultTextColor)
            }
            var i = 0
            while (condition) {
                app.runOnUiThread {
                    app.successText.text = getText(i) + ".".repeat(i%4)
                }
                try {
                    sleep(1000)
                } catch(e : InterruptedException) {

                }
                i++
            }
        }

        fun stopAnimation() {
            condition = false
        }

        private fun getText(i : Int) : String {
            if (i >= 8) {
                return "Loading Grades"
            }
            return "Logging in"
        }
    }

}

class UpdateGradesButtonListener(private val app: MainActivity) : View.OnClickListener {

    override fun onClick(p0: View?) {
        val dtf = DateTimeFormatter.ofPattern("MM/dd 'at' HH:mm:ss")
        val now = LocalDateTime.now()
        val text = "Last Updated: " + dtf.format(now)
        app.findViewById<TextView>(R.id.last_updated).text = text
    }

}