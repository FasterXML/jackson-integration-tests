package com.fasterxml.jackson.failing

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlFactory
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.integtest.BaseTest
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import javax.xml.stream.XMLInputFactory

class Jackson212MissingConstructorTest : BaseTest()
{
    /**
     * Succeeds in Jackson 2.11.x, but fails in Jackson 2.12.x
     *
     * @see <a href="https://github.com/FasterXML/jackson-module-kotlin/issues/396">jackson-databind issue 396</a>
     */
    fun testMissingConstructor()
    {
        val factory = XmlFactory(XMLInputFactory.newInstance())
        val mapper = XmlMapper(factory, JacksonXmlModule()).registerKotlinModule()

        val xml = "<product><stuff></stuff></product>"
        val product: Product = mapper.readValue(xml, Product::class.java)

        assertEquals(Product(null), product)
    }

    private data class Stuff(val str: String?)
    private data class Product(val stuff: Stuff?)
}