package me.tahzam23.schoolpower.datetime

interface DateTimeFormatConverter {

    fun convert(originalPattern: String, postPattern: String, date: String): String

}