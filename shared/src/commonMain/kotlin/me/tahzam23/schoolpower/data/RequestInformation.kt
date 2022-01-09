package me.tahzam23.schoolpower.data

const val SCHOOLPOWER_GRADE_ROOT = "https://ps001.bergen.org/"
const val SCHOOLPOWER_GRADE_GUARDIAN_PATH = "guardian/"
const val SCHOOLPOWER_GRADE_ENDPOINT = SCHOOLPOWER_GRADE_GUARDIAN_PATH + "home.html"
const val SCHOOLPOWER_ASSIGNMENT_ENDPOINT = "ws/xte/assignment/lookup"
const val SCHOOLPOWER_KEEP_ALIVE_ENDPOINT = "ws/session/last-hit"

data class RequestInformation(
    val root: String = SCHOOLPOWER_GRADE_ROOT,
    val guardianPath: String = SCHOOLPOWER_GRADE_GUARDIAN_PATH,
    val summaryEndpoint: String = SCHOOLPOWER_GRADE_ENDPOINT,
    val assignmentEndpoint: String = SCHOOLPOWER_ASSIGNMENT_ENDPOINT,
    val keepAliveEndpoint: String = SCHOOLPOWER_KEEP_ALIVE_ENDPOINT
)