package me.tahzam23.schoolpower.android

import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import me.tahzam23.schoolpower.android.listener.LoginButtonListener
import me.tahzam23.schoolpower.android.listener.SettingsButtonListener
import me.tahzam23.schoolpower.android.listener.UpdateGradesButtonListener
import me.tahzam23.schoolpower.data.RequestInformation
import me.tahzam23.schoolpower.datetime.AndroidDateTimeFormatConverter
import me.tahzam23.schoolpower.html.AndroidDocumentCreator
import me.tahzam23.schoolpower.scraper.WebSchoolPowerScraper

class MainActivity: AppCompatActivity() {
    lateinit var username: EditText
        private set
    lateinit var password: EditText
        private set
    lateinit var successText: TextView
        private set
    lateinit var settingsButton: Button
        private set
    lateinit var defaultTextColor : ColorStateList
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        username = findViewById(R.id.schoolpower_username)
        password = findViewById(R.id.schoolpower_password)
        successText = findViewById(R.id.schoolpower_success)
        settingsButton = findViewById(R.id.settings)
        findViewById<Button>(R.id.schoolpower_submit_button).setOnClickListener(LoginButtonListener(
            WebSchoolPowerScraper(
                RequestInformation(),
                AndroidDocumentCreator(),
                AndroidDateTimeFormatConverter()
            ),
            this
        ))
        findViewById<Button>(R.id.settings).setOnClickListener(SettingsButtonListener(this))
        defaultTextColor = successText.textColors
    }

    fun gradePageSetup() {
        findViewById<Button>(R.id.update_button).setOnClickListener(UpdateGradesButtonListener(this))
    }
}
