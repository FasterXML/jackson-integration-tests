package tools.jackson.integtest.kotlin

import tools.jackson.dataformat.xml.JacksonXmlModule
import tools.jackson.dataformat.xml.XmlFactory
import tools.jackson.dataformat.xml.XmlMapper
import tools.jackson.integtest.BaseTest
import tools.jackson.module.kotlin.registerKotlinModule
import javax.xml.stream.XMLInputFactory

class Jackson212MissingConstructorTest : BaseTest()
{
    /**
     * Succeeds in Jackson 2.11.x, but fails in Jackson 2.12.0
     * See https://github.com/FasterXML/jackson-module-kotlin/issues/396
     */
    fun testMissingConstructor()
    {
        val factory = XmlFactory(XMLInputFactory.newInstance())
        val mapper = XmlMapper(factory, JacksonXmlModule()).registerKotlinModule()

        val xml = "<product><stuff></stuff></product>"
        val product: Product = mapper.readValue(xml, Product::class.java)

        // 20-Jan-2023, tatu: With Properties-based Creator, this was wrong:
        //assertEquals(Product(null), product)
        assertEquals(Product(Stuff(null)), product)
    }

    private data class Stuff(val str: String?)
    private data class Product(val stuff: Stuff?)
}