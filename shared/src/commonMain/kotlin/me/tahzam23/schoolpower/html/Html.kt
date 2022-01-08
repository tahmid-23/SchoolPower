package me.tahzam23.schoolpower.html

expect fun createDocument(html: String): Element

interface Element {

    fun getElementById(id: String): Element?

    fun getChild(index: Int): Element

    fun getChildCount(): Int

    fun getAttribute(attributeName: String): String

}