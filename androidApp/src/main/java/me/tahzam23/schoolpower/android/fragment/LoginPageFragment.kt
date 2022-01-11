package me.tahzam23.schoolpower.android.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import io.ktor.client.*
import kotlinx.coroutines.*
import me.tahzam23.schoolpower.android.R
import me.tahzam23.schoolpower.createDefaultClientConfig
import me.tahzam23.schoolpower.data.LoginInformation
import me.tahzam23.schoolpower.data.RequestInformation
import me.tahzam23.schoolpower.datetime.AndroidDateTimeFormatConverter
import me.tahzam23.schoolpower.html.AndroidDocumentCreator
import me.tahzam23.schoolpower.scraper.WebSchoolPowerScraper

class LoginPageFragment: Fragment() {

    private val schoolPowerScraper = WebSchoolPowerScraper(
        RequestInformation(),
        AndroidDocumentCreator(),
        AndroidDateTimeFormatConverter()
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_login_page, container, false)

        view.findViewById<Button>(R.id.settings).setOnClickListener {
            val action = LoginPageFragmentDirections.actionLoginPageFragmentToSettingsFragment()
            it.findNavController().navigate(action)
        }

        val submitButton = view.findViewById<Button>(R.id.schoolpower_submit_button)
        submitButton.isEnabled = false

        val username = view.findViewById<EditText>(R.id.schoolpower_username)
        val password = view.findViewById<EditText>(R.id.schoolpower_password)

        var flagUsername = false
        var flagPassword = false
        username.addTextChangedListener {
            flagUsername = !TextUtils.isEmpty(it?.toString())
            submitButton.isEnabled = flagUsername && flagPassword
        }
        password.addTextChangedListener {
            flagPassword = !TextUtils.isEmpty(it?.toString())
            submitButton.isEnabled = flagUsername && flagPassword
        }

        val successText = view.findViewById<TextView>(R.id.schoolpower_success)
        val defaultColors = successText.textColors
        submitButton.setOnClickListener {
            val animation = beginAnimation(successText, defaultColors)

            val client = HttpClient {
                createDefaultClientConfig()
            }

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
                val success = try {
                    schoolPowerScraper.scrape(client, LoginInformation(
                        username.text.toString(),
                        password.text.toString()
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
                animation.cancelAndJoin()

                if (success) {
                    withContext(Dispatchers.Main) {
                        successText.text = "Success"
                        successText.setTextColor(Color.GREEN)

                        val action = LoginPageFragmentDirections.actionLoginPageFragmentToLoggedInFragment()
                        it.findNavController().navigate(action)
                    }
                }
                else {
                    withContext(Dispatchers.Main) {
                        successText.text = "Incorrect Username or Password!"
                        successText.setTextColor(Color.RED)
                    }
                }
            }
        }

        return view
    }

    private fun beginAnimation(successText: TextView, defaultTextColor: ColorStateList): Job {
        return viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            successText.setTextColor(defaultTextColor)
            var i = 0

            while (isActive) {
                successText.text = "Loading Grades" + ".".repeat(i % 4)
                if (++i == 4) {
                    i = 0
                }

                delay(1000)
            }
        }
    }

}