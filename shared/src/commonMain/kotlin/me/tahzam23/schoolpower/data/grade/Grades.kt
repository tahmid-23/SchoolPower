package me.tahzam23.schoolpower.data.grade

import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import me.tahzam23.schoolpower.BCA_TIMEZONE_ID

@Serializable
data class CourseSummary(
    val courses: Collection<Course> = emptyList(),
    val syncTime: LocalDateTime = Instant.DISTANT_PAST.toLocalDateTime(TimeZone.of(BCA_TIMEZONE_ID))
)

@Serializable
data class Course(
    val name: String = "Unknown Course Name",
    val teacher: String = "Unknown Teacher",
    val markingPeriods: Map<MarkingPeriod, MarkingPeriodGrades?> = emptyMap()
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

@Serializable
data class MarkingPeriodGrades(
    val letterGrade: String = "Unknown",
    val grade: Double = 0.0,
    val grades: Collection<Grade> = emptyList()
)

@Serializable
data class Grade(
    val name: String = "Unknown Grade",
    val dueDate: String = "Unknown Due Date",
    val totalPoints: Double = 0.0,
    val points: Double? = null,
    val percentage: Double? = null,
    val letterGrade: String? = null
)