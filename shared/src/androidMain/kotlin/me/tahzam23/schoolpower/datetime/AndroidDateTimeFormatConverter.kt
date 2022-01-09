package me.tahzam23.schoolpower.datetime

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AndroidDateTimeFormatConverter: DateTimeFormatConverter {

    override fun convert(originalPattern: String, postPattern: String, date: String): String =
        LocalDate.parse(date, DateTimeFormatter.ofPattern(originalPattern))
            .format(DateTimeFormatter.ofPattern(postPattern))

}
