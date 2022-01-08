package me.tahzam23.schoolpower

import me.tahzam23.schoolpower.data.LoginInformation

interface PasswordManager {

    fun getLoginDetails(): LoginInformation?

    fun setLoginDetails(loginInformation: LoginInformation)

}