package me.tahzam23.schoolpower.data

import me.tahzam23.schoolpower.SCHOOLPOWER_ASSIGNMENT_ENDPOINT
import me.tahzam23.schoolpower.SCHOOLPOWER_GRADE_ENDPOINT
import me.tahzam23.schoolpower.SCHOOLPOWER_GRADE_GUARDIAN_PATH
import me.tahzam23.schoolpower.SCHOOLPOWER_GRADE_ROOT

data class RequestInformation(
    val root: String = SCHOOLPOWER_GRADE_ROOT,
    val guardianPath: String = SCHOOLPOWER_GRADE_GUARDIAN_PATH,
    val summaryEndpoint: String = SCHOOLPOWER_GRADE_ENDPOINT,
    val assignmentEndpoint: String = SCHOOLPOWER_ASSIGNMENT_ENDPOINT
)