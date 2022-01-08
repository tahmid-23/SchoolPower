package me.tahzam23.schoolpower

import android.content.SharedPreferences
import me.tahzam23.schoolpower.android.LOGIN_PASSWORD_KEY
import me.tahzam23.schoolpower.android.LOGIN_USERNAME_KEY
import me.tahzam23.schoolpower.data.LoginInformation

class AndroidApplicationEnvironment(
    private val preferences: SharedPreferences
): ApplicationEnvironment {

    override fun getLoginDetails(): LoginInformation? {
        val username = preferences.getString(LOGIN_USERNAME_KEY, null)
            ?: return null

        val password = preferences.getString(LOGIN_PASSWORD_KEY, null)
            ?: return null

        return LoginInformation(username, password)
    }

    override fun setLoginDetails(loginInformation: LoginInformation) {
        preferences
            .edit()
            .putString(LOGIN_USERNAME_KEY, loginInformation.username)
            .putString(LOGIN_PASSWORD_KEY, loginInformation.password)
            .apply()
    }

}