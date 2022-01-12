package me.tahzam23.schoolpower.storage

import me.tahzam23.schoolpower.data.grade.CourseSummary

interface GradeSaver {

    fun saveGrades(summary: CourseSummary)

    fun loadGrades(): CourseSummary

}