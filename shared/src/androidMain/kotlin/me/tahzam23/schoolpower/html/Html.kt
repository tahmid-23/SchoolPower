package me.tahzam23.schoolpower.html

import org.jsoup.Jsoup

class AndroidDocumentCreator: DocumentCreator {

    override fun createDocument(html: String): Element {
        return ElementImpl(Jsoup.parse(html))
    }

}



private class ElementImpl(private val element: org.jsoup.nodes.Element): Element {

    override fun getElementById(id: String): Element? {
        val element = element.getElementById(id)

        return if (element != null) {
            ElementImpl(element)
        } else {
            null
        }
    }

    override fun getChild(index: Int) = ElementImpl(element.child(index))

    override fun getChildCount() = element.childrenSize()

    override fun getAttribute(attributeName: String): String = element.attr(attributeName)

    override fun getOwnText(): String = element.ownText()

}