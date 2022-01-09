package me.tahzam23.schoolpower

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.cookies.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.datetime.*
import kotlinx.serialization.json.Json
import me.tahzam23.schoolpower.data.LoginInformation
import me.tahzam23.schoolpower.data.RequestInformation
import me.tahzam23.schoolpower.data.SchoolPowerInfo
import me.tahzam23.schoolpower.data.grade.markingPeriods
import me.tahzam23.schoolpower.datetime.DateTimeFormatConverter
import me.tahzam23.schoolpower.html.DocumentCreator

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

    /* Uncomment for logging
    config.install(Logging) {
        level = LogLevel.ALL
    }
     */

    config.BrowserUserAgent()
}

suspend fun login(
    requestInformation: RequestInformation = RequestInformation(),
    client: HttpClient,
    loginInformation: LoginInformation,
    documentCreator: DocumentCreator,
    dateTimeFormatConverter: DateTimeFormatConverter
): Boolean {
    try {
        val document = documentCreator.createDocument(
            client.submitForm(
                requestInformation.root + requestInformation.path + requestInformation.endpoint,
                createRequestParameters(loginInformation)
            )
        )

        val table = document
            .getElementById(GRADE_TABLE_ID)
            ?.getChild(1)
            ?.getChild(1) ?: throw IllegalArgumentException()

        for (rowIndex in 2 until table.getChildCount() - 1) {
            val row = table.getChild(rowIndex)

            val courseInfo = row.getChild(11)
            val courseName = courseInfo.getOwnText()
            val teacher = courseInfo
                .getChild(courseInfo.getChildCount() - 1)
                .getOwnText().substring(TEACHER_FIELD_PREFIX_LENGTH)

            for (markingPeriodIndex in markingPeriods.indices) {
                val cell = row.getChild(12 + markingPeriodIndex)
                if (cell.getAttribute(HTML_ATTRIBUTE_CLASS) != CLASS_UNAVAILABLE) {
                    val link = cell.getChild(0)

                    if (link.getOwnText() != "[ i ]") {
                        val href = link.getAttribute(HTML_ATTRIBUTE_HREF)
                        val gradeDocument = documentCreator.createDocument(
                            client.get(requestInformation.root + requestInformation.path +
                                    href)
                        )

                        val content = gradeDocument.getElementById("content-main")!!
                        val div = content.getChild(2)
                        val info = div.getChild(6)
                        val sectionId = info.getChild(0).getAttribute("data-sectionid")

                        val summary = info.getAttribute("data-ng-init")
                        val studentFRN = summary
                            .substringAfter("studentFRN")
                            .substringAfter("'")
                            .substringBefore("'")
                        val beginningDate = dateTimeFormatConverter.convert(
                            "MM/dd/yyyy", "yyyy-M-d",
                            summary
                                .substringAfter("beginningDate")
                                .substringAfter("'")
                                .substringBefore("'"))
                        val endingDate = dateTimeFormatConverter.convert(
                            "MM/dd/yyyy", "yyyy-M-d",
                            summary
                                .substringAfter("endingDate")
                                .substringAfter("'")
                                .substringBefore("'"))


                        val studentId = Regex("001([0-9]+)").find(studentFRN)!!
                            .groupValues[1]

                        val data = "{\"section_ids\":[$sectionId],\"student_ids\":[$studentId]," +
                                "\"start_date\":\"$beginningDate\",\"end_date\":\"$endingDate\"}"
                        val time = Clock.System.now().toEpochMilliseconds()

                        val json = client.post<String>(requestInformation.root +
                                "ws/xte/assignment/lookup") {
                            contentType(ContentType.Application.Json)
                            header("Referer", requestInformation.root + requestInformation.path +
                                    href)
                            parameter("_", time)
                            body = data
                        }

                        Json.parseToJsonElement(json)
                    }

                    val markingPeriod = markingPeriods[markingPeriodIndex]
                }
            }
        }

        return true
    }
    catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}

fun isSchoolPowerOpen(schoolPowerInfo: SchoolPowerInfo = SchoolPowerInfo()): Boolean {
    val dateTime = Clock.System.now().toLocalDateTime(schoolPowerInfo.timeZone)

    return dateTime.dayOfWeek in schoolPowerInfo.fullyOpenDays ||
            (dateTime.hour < schoolPowerInfo.beginHour || dateTime.hour > schoolPowerInfo.endHour)
}