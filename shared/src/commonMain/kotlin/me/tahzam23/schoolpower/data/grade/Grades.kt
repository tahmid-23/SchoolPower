package me.tahzam23.schoolpower.data.grade

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