package me.tahzam23.schoolpower.data.grade

import kotlinx.serialization.json.*

data class Course(
    val name: String,
    val teacher: String,
    val markingPeriods: Map<MarkingPeriod, MarkingPeriodGrades?>
)

enum class MarkingPeriod {
    T1,
    T2,
    T3,
    S1,
    S2,
    Y1
}

val markingPeriods = listOf(
    MarkingPeriod.T1,
    MarkingPeriod.S1,
    MarkingPeriod.T2,
    MarkingPeriod.T3,
    MarkingPeriod.S2,
    MarkingPeriod.Y1
)

data class MarkingPeriodGrades(
    val letterGrade: String,
    val grade: Double,
    val grades: Collection<Grade>
)

data class Grade(
    val name: String,
    val dueDate: String,
    val totalPoints: Double,
    val points: Double?,
    val percentage: Double?,
    val letterGrade: String?
)

fun parseGrade(jsonElement: JsonElement): Grade {
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