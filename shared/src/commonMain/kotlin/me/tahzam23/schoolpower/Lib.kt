package me.tahzam23.schoolpower

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.cookies.*
import kotlinx.datetime.*
import me.tahzam23.schoolpower.data.SchoolPowerInfo

fun HttpClientConfig<*>.createDefaultClientConfig() {
    install(HttpRedirect) {
        checkHttpMethod = false
    }

    install(HttpCookies) {
        storage = AcceptAllCookiesStorage()
    }

    /* Uncomment for logging
    config.install(Logging) {
        level = LogLevel.ALL
    }
     */

    BrowserUserAgent()
}

fun isSchoolPowerOpen(schoolPowerInfo: SchoolPowerInfo = SchoolPowerInfo()): Boolean {
    val dateTime = Clock.System.now().toLocalDateTime(schoolPowerInfo.timeZone)

    return dateTime.dayOfWeek in schoolPowerInfo.fullyOpenDays ||
            (dateTime.hour < schoolPowerInfo.beginHour || dateTime.hour > schoolPowerInfo.endHour)
}