package me.tahzam23.schoolpower

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.cookies.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.datetime.*
import me.tahzam23.schoolpower.data.LoginInformation

private fun createRequestParameters(loginInformation: LoginInformation) = parametersOf(
    "dbpw" to listOf(loginInformation.password),
    "translator_username" to listOf(""),
    "translator_password" to listOf(""),
    "translator_ldappassword" to listOf(""),
    "returnUrl" to listOf(""),
    "serviceName" to listOf("PS Parent Portal"),
    "serviceTicket" to listOf(""),
    "pcasServerUrl" to listOf("/"),
    "credentialType" to listOf("User Id and Password Credential"),
    "ldappassword" to listOf(loginInformation.password),
    "account" to listOf(loginInformation.username),
    "pw" to listOf(loginInformation.password),
    "translatorpw" to listOf(""),
)

private fun isWeekend(dayOfWeek: DayOfWeek) = dayOfWeek.isoDayNumber == 6 || dayOfWeek.isoDayNumber == 7

fun createDefaultClientConfig(config: HttpClientConfig<*>) {
    config.install(HttpRedirect) {
        checkHttpMethod = false
    }

    config.install(HttpCookies) {
        storage = AcceptAllCookiesStorage()
    }

    config.BrowserUserAgent()
}

suspend fun login(client: HttpClient, loginInformation: LoginInformation) =
    client.submitForm<HttpResponse>(SCHOOLPOWER_GRADE_ENDPOINT, createRequestParameters(loginInformation))

fun isSchoolPowerOpen(): Boolean {
    val timeZone = TimeZone.of(BCA_TIMEZONE_ID)
    val dateTime = Clock.System.now().toLocalDateTime(timeZone)

    return isWeekend(dateTime.dayOfWeek) || (dateTime.hour < SCHOOLPOWER_CLOSE_BEGIN_HOUR ||
            dateTime.hour > SCHOOLPOWER_CLOSE_END_HOUR)
}