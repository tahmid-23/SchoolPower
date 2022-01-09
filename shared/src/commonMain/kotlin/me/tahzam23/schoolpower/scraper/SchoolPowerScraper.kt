package me.tahzam23.schoolpower.scraper

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.datetime.Clock
import kotlinx.serialization.json.*
import me.tahzam23.schoolpower.data.LoginInformation
import me.tahzam23.schoolpower.data.RequestInformation
import me.tahzam23.schoolpower.data.grade.*
import me.tahzam23.schoolpower.datetime.DateTimeFormatConverter
import me.tahzam23.schoolpower.html.DocumentCreator
import me.tahzam23.schoolpower.html.Element

interface SchoolPowerScraper {

    suspend fun scrape(client: HttpClient, loginInformation: LoginInformation): Collection<Course>

    suspend fun keepAlive(client: HttpClient): Int

}

class WebSchoolPowerScraper(
    private val requestInformation: RequestInformation,
    private val documentCreator: DocumentCreator,
    private val dateTimeFormatConverter: DateTimeFormatConverter
): SchoolPowerScraper {

    companion object {

        private const val GRADE_TABLE_ID = "quickLookup"

        private const val TEACHER_FIELD_PREFIX = "Email "

        private const val NO_GRADES_TEXT = "[ i ]"

        private const val HTML_ATTRIBUTE_CLASS = "class"

        private const val HTML_ATTRIBUTE_HREF = "href"

        private const val CLASS_UNAVAILABLE = "notInSession"

        private const val ASSIGNMENT_TABLE_ID = "content-main"

        private const val COURSE_ASSIGNMENT_PARSE_FAIL =
            "Could not parse course assignment information!"

        private const val SECTION_ID_KEY = "data-sectionid"

        private const val ASSIGNMENT_INFO_KEY = "data-ng-init"

        private const val STUDENT_ID_REGEX = "001([0-9]+)"

        private const val BEGINNING_DATE = "beginningDate"

        private const val ENDING_DATE = "endingDate"

        private const val PARSE_DAY_FORMAT = "MM/dd/yyyy"

        private const val REQUEST_DAY_FORMAT = "yyyy-M-d"

        private const val REFERER_HEADER = "Referer"

        private const val TIMESTAMP_PARAMETER_NAME = "_"

        private const val KEEP_ALIVE_PARAMETER_NAME = "seconds"

        private const val KEEP_ALIVE_RESPONSE_OBJECT_NAME = "Seconds"

        private const val KEEP_ALIVE_RESPONSE_VALUE_NAME = "value"

    }

    override suspend fun scrape(
        client: HttpClient,
        loginInformation: LoginInformation
    ): Collection<Course> = buildList {
        try {
            val summaryTable = getSummaryTable(getSummaryDocument(client, loginInformation))

            for (rowIndex in 2 until summaryTable.getChildCount() - 1) {
                add(createCourse(client, summaryTable.getChild(rowIndex)))
            }
        } catch (e: SchoolPowerScrapeException) {
            throw e
        } catch (e: Exception) {
            throw SchoolPowerScrapeException(e)
        }
    }

    override suspend fun keepAlive(client: HttpClient) =
        Json.parseToJsonElement(client.request<String>(requestInformation.root +
                requestInformation.keepAliveEndpoint) {
            method = HttpMethod.Put
            body = TextContent("", ContentType.Any) // ktor won't let us explicitly set no content type
            header(REFERER_HEADER, requestInformation.root + requestInformation.summaryEndpoint)
            accept(ContentType.Application.Json)
            parameter(KEEP_ALIVE_PARAMETER_NAME, 0)
        }).jsonObject[KEEP_ALIVE_RESPONSE_OBJECT_NAME]!!
            .jsonObject[KEEP_ALIVE_RESPONSE_VALUE_NAME]!!.jsonPrimitive.int

    private suspend fun getSummaryDocument(client: HttpClient, loginInformation: LoginInformation) =
        documentCreator.createDocument(client.submitForm(
            requestInformation.root + requestInformation.summaryEndpoint,
            createRequestParameters(loginInformation)
        ))

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

    private fun getSummaryTable(document: Element) =
        document.getElementById(GRADE_TABLE_ID)
            ?.getChild(1)
            ?.getChild(1)
            ?: throw SchoolPowerScrapeException("Could not find a grade summary table!")

    private suspend fun createCourse(client: HttpClient, row: Element): Course {
        if (row.getChildCount() < 12 + markingPeriods.size) {
            throw SchoolPowerScrapeException("Row $row did not have enough information " +
                    "to create a course!")
        }

        val courseInfo = row.getChild(11)
        val courseName = courseInfo.getOwnText()
        val teacherName = parseTeacherName(courseInfo)

        return Course(courseName, teacherName, buildMap {
            for (markingPeriodIndex in markingPeriods.indices) {
                val markingPeriod = markingPeriods[markingPeriodIndex]

                val cell = row.getChild(12 + markingPeriodIndex)
                if (cell.getAttribute(HTML_ATTRIBUTE_CLASS) != CLASS_UNAVAILABLE) {
                    if (cell.getChildCount() == 0) {
                        throw SchoolPowerScrapeException("Failed to get course info link from " +
                                "cell $cell!")
                    }

                    val link = cell.getChild(0)
                    val gradeSummary = link.getOwnText()
                    if (gradeSummary != NO_GRADES_TEXT) {
                        put(markingPeriod, createMarkingPeriodGrades(client, gradeSummary, link))
                    }
                }

                putIfAbsent(markingPeriod, null)
            }
        })
    }

    private fun parseTeacherName(courseInfo: Element): String {
        if (courseInfo.getChildCount() == 0) {
            throw SchoolPowerScrapeException("Failed to parse teacher name!")
        }

        val text = courseInfo.getChild(courseInfo.getChildCount() - 1).getOwnText()
        if (!text.startsWith(TEACHER_FIELD_PREFIX)) {
            throw SchoolPowerScrapeException("Teacher name did not have expected prefix: " +
                    TEACHER_FIELD_PREFIX
            )
        }

        return text.substring(TEACHER_FIELD_PREFIX.length)
    }

    private suspend fun createMarkingPeriodGrades(
        client: HttpClient,
        gradeSummary: String,
        link: Element
    ): MarkingPeriodGrades {
        val gradeSummaryList = gradeSummary.split(" ")
        val letterGrade = gradeSummaryList[0]
        val grade = gradeSummaryList[1].toDouble()

        val href = link.getAttribute(HTML_ATTRIBUTE_HREF)

        val json = getAssignmentJson(
            client,
            href,
            parseAssignmentRequestInfo(getGradeDocument(client, href)).toString()
        )

        val jsonElement = Json.parseToJsonElement(json)
        val grades = buildList {
            for (assignment in jsonElement.jsonArray) {
                add(parseGrade(assignment))
            }
        }

        return MarkingPeriodGrades(letterGrade, grade, grades)
    }

    private fun parseGrade(jsonElement: JsonElement): Grade {
        val assignment = jsonElement.jsonObject["_assignmentsections"]!!.jsonArray[0].jsonObject
        val name = assignment["name"]!!.jsonPrimitive.content
        val dueDate = assignment["duedate"]!!.jsonPrimitive.content
        val totalPoints = assignment["scoreentrypoints"]!!.jsonPrimitive.double

        val scores = assignment["_assignmentscores"]!!.jsonArray
        return if (scores.size == 0) {
            Grade(name, dueDate, totalPoints, null, null, null)
        }
        else {
            val score = scores[0].jsonObject
            val points = score["scorepoints"]?.jsonPrimitive?.double
            val percentage = score["scorepercent"]?.jsonPrimitive?.double
            val letterGrade = score["scorelettergrade"]?.jsonPrimitive?.content

            Grade(name, dueDate, totalPoints, points, percentage, letterGrade)
        }
    }

    private suspend fun getGradeDocument(client: HttpClient, href: String) =
        documentCreator.createDocument(
            client.get(requestInformation.root + requestInformation.guardianPath + href)
        )

    private fun parseAssignmentRequestInfo(gradeDocument: Element): AssignmentRequestInfo {
        val content = gradeDocument.getElementById(ASSIGNMENT_TABLE_ID)
            ?: throw SchoolPowerScrapeException(COURSE_ASSIGNMENT_PARSE_FAIL)

        if (content.getChildCount() < 3) {
            throw SchoolPowerScrapeException(COURSE_ASSIGNMENT_PARSE_FAIL)
        }
        val div = content.getChild(2)

        if (div.getChildCount() < 7) {
            throw SchoolPowerScrapeException(COURSE_ASSIGNMENT_PARSE_FAIL)
        }
        val info = div.getChild(6)

        if (info.getChildCount() == 0) {
            throw SchoolPowerScrapeException(COURSE_ASSIGNMENT_PARSE_FAIL)
        }

        val sectionId = info.getChild(0).getAttribute(SECTION_ID_KEY)

        val summary = info.getAttribute(ASSIGNMENT_INFO_KEY)
        val studentId = Regex(STUDENT_ID_REGEX).find(parseStudentFRN(summary))!!.groupValues[1]
        val beginningDate = parseDate(summary, BEGINNING_DATE)
        val endingDate = parseDate(summary, ENDING_DATE)

        return AssignmentRequestInfo(sectionId, studentId, beginningDate, endingDate)
    }

    private fun parseStudentFRN(summary: String) = summary
        .substringAfter("studentFRN")
        .substringAfter("'")
        .substringBefore("'")

    private fun parseDate(summary: String, dateKey: String) = dateTimeFormatConverter.convert(
        PARSE_DAY_FORMAT, REQUEST_DAY_FORMAT,
        summary
            .substringAfter(dateKey)
            .substringAfter("'")
            .substringBefore("'")
    )

    private suspend fun getAssignmentJson(client: HttpClient, href: String, data: String) =
        client.post<String>(requestInformation.root +
                requestInformation.assignmentEndpoint) {
            contentType(ContentType.Application.Json)
            header(REFERER_HEADER, requestInformation.root + requestInformation.guardianPath + href)
            parameter(TIMESTAMP_PARAMETER_NAME, Clock.System.now().toEpochMilliseconds())
            body = data
        }

    private data class AssignmentRequestInfo(
        val sectionId: String,
        val studentId: String,
        val beginningDate: String,
        val endingDate: String
    ) {

        override fun toString() = "{\"section_ids\":[$sectionId],\"student_ids\":[$studentId]," +
                "\"start_date\":\"$beginningDate\",\"end_date\":\"$endingDate\"}"

    }

}