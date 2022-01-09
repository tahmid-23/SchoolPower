package me.tahzam23.schoolpower.data

import me.tahzam23.schoolpower.SCHOOLPOWER_GRADE_ENDPOINT
import me.tahzam23.schoolpower.SCHOOLPOWER_GRADE_GUARDIAN_PATH
import me.tahzam23.schoolpower.SCHOOLPOWER_GRADE_ROOT

data class RequestInformation(
    val root: String = SCHOOLPOWER_GRADE_ROOT,
    val path: String = SCHOOLPOWER_GRADE_GUARDIAN_PATH,
    val endpoint: String = SCHOOLPOWER_GRADE_ENDPOINT
)