package me.tahzam23.schoolpower.storage

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.tahzam23.schoolpower.data.grade.CourseSummary
import java.io.*

class AndroidGradeSaver(private val file: File): GradeSaver {

    override fun saveGrades(summary: CourseSummary) {
        val json = Json.encodeToString(summary)
        BufferedWriter(FileWriter(file)).use {
            it.write(json)
        }
    }

    override fun loadGrades(): CourseSummary {
        BufferedReader(FileReader(file)).use {
            return Json.decodeFromString(it.readText())
        }
    }

}