package me.tahzam23.schoolpower.data

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import me.tahzam23.schoolpower.BCA_TIMEZONE_ID
import me.tahzam23.schoolpower.SCHOOLPOWER_CLOSE_BEGIN_HOUR
import me.tahzam23.schoolpower.SCHOOLPOWER_CLOSE_END_HOUR

data class SchoolPowerInfo(
    val timeZone: TimeZone = TimeZone.of(BCA_TIMEZONE_ID),
    val beginHour: Int = SCHOOLPOWER_CLOSE_BEGIN_HOUR,
    val endHour: Int = SCHOOLPOWER_CLOSE_END_HOUR,
    val fullyOpenDays: Set<DayOfWeek> = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
)