package me.tahzam23.schoolpower.scraper

class SchoolPowerScrapeException: Exception {

    constructor(throwable: Throwable): super(throwable)

    constructor(message: String): super(message)

}