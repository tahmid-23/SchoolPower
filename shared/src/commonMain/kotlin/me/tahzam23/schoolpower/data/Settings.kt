package me.tahzam23.schoolpower.data

private const val REMEMBER_PASSWORD_DEFAULT = false
private const val AUTOMATIC_GRADE_UPDATE_DEFAULT = true
private const val HAGJER_REQUESTS_DEFAULT = false

class Settings() {

    companion object {

        var rememberPassword = REMEMBER_PASSWORD_DEFAULT
        var automaticGradeUpdate = AUTOMATIC_GRADE_UPDATE_DEFAULT
        var hagjerRequests = HAGJER_REQUESTS_DEFAULT

        fun resetToDefault() {
            rememberPassword = REMEMBER_PASSWORD_DEFAULT
            automaticGradeUpdate = AUTOMATIC_GRADE_UPDATE_DEFAULT
            hagjerRequests = HAGJER_REQUESTS_DEFAULT
        }
    }
}