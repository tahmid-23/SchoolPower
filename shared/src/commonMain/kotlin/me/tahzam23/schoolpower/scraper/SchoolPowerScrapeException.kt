package me.tahzam23.schoolpower.scraper

import java.lang.Exception

class SchoolPowerScrapeException: Exception {

    constructor(throwable: Throwable): super(throwable)

    constructor(message: String): super(message)

}