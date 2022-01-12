package me.tahzam23.schoolpower.android.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import me.tahzam23.schoolpower.android.R
import me.tahzam23.schoolpower.data.Settings

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val savePasswordSwitch = view.findViewById<Switch>(R.id.save_password_switch)
        val autoRefreshSwitch = view.findViewById<Switch>(R.id.auto_refresh_switch)
        val hagjerSwitch = view.findViewById<Switch>(R.id.hagjer_switch)

        savePasswordSwitch.isChecked = Settings.rememberPassword
        autoRefreshSwitch.isChecked = Settings.automaticGradeUpdate
        hagjerSwitch.isChecked = Settings.hagjerRequests

        savePasswordSwitch.setOnClickListener {
            Settings.rememberPassword = savePasswordSwitch.isChecked
            println(Settings.rememberPassword)
        }
        autoRefreshSwitch.setOnClickListener {
            Settings.automaticGradeUpdate = autoRefreshSwitch.isChecked
            println(Settings.automaticGradeUpdate)
        }
        hagjerSwitch.setOnClickListener {
            Settings.hagjerRequests = hagjerSwitch.isChecked
            println(Settings.hagjerRequests)
        }

        return view
    }


}