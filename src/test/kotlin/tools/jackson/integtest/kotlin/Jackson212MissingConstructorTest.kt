package tools.jackson.integtest.kotlin

import tools.jackson.dataformat.xml.XmlMapper
import tools.jackson.integtest.BaseTest
import tools.jackson.module.kotlin.kotlinModule
import org.junit.jupiter.api.Assertions;

class Jackson212MissingConstructorTest : BaseTest()
{
    /**
     * Succeeds in Jackson 2.11.x, but fails in Jackson 2.12.0
     * See https://github.com/FasterXML/jackson-module-kotlin/issues/396
     */
    fun testMissingConstructor()
    {
        val mapper = XmlMapper.builder().addModule(kotlinModule()).build();

        val xml = "<product><stuff></stuff></product>"
        val product: Product = mapper.readValue(xml, Product::class.java)

        // 20-Jan-2023, tatu: With Properties-based Creator, this was wrong:
        // Assertions.assertEquals(Product(null), product)
        Assertions.assertEquals(Product(Stuff(null)), product)
    }

    data class Stuff(val str: String?)
    data class Product(val stuff: Stuff?)
}