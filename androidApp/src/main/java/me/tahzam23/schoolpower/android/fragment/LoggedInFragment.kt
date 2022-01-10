package me.tahzam23.schoolpower.android.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import me.tahzam23.schoolpower.android.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LoggedInFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_logged_in, container, false)

        val lastUpdated = view.findViewById<TextView>(R.id.last_updated)
        view.findViewById<Button>(R.id.update_button).setOnClickListener {
            val dtf = DateTimeFormatter.ofPattern("MM/dd 'at' HH:mm:ss")
            lastUpdated.text = "Last Updated: " + dtf.format(LocalDateTime.now())
        }

        return view
    }

}