package me.tahzam23.schoolpower

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.cookies.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.datetime.*
import me.tahzam23.schoolpower.data.LoginInformation
import me.tahzam23.schoolpower.data.SchoolPowerInfo
import me.tahzam23.schoolpower.html.createDocument

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

fun createDefaultClientConfig(config: HttpClientConfig<*>) {
    config.install(HttpRedirect) {
        checkHttpMethod = false
    }

    config.install(HttpCookies) {
        storage = AcceptAllCookiesStorage()
    }

    config.BrowserUserAgent()
}

suspend fun login(
    root: String = SCHOOLPOWER_GRADE_ROOT,
    endpoint: String = SCHOOLPOWER_GRADE_ENDPOINT,
    client: HttpClient,
    loginInformation: LoginInformation
) {
    val document = createDocument(client.submitForm(
        root + endpoint,
        createRequestParameters(loginInformation)
    ))

    val table = document
        .getElementById(GRADE_TABLE_ID)
        ?.getChild(1)
        ?.getChild(1) ?: throw IllegalArgumentException()

    for (rowIndex in 2 until table.getChildCount() - 1) {
        val row = table.getChild(rowIndex)
        row.getChild(17).getChild(0).getAttribute("href")
    }
}

fun isSchoolPowerOpen(schoolPowerInfo: SchoolPowerInfo = SchoolPowerInfo()): Boolean {
    val dateTime = Clock.System.now().toLocalDateTime(schoolPowerInfo.timeZone)

    return dateTime.dayOfWeek in schoolPowerInfo.fullyOpenDays ||
            (dateTime.hour < schoolPowerInfo.beginHour || dateTime.hour > schoolPowerInfo.endHour)
}