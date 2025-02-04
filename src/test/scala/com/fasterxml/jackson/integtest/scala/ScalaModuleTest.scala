package com.fasterxml.jackson.integtest.scala

import java.lang.reflect.{ParameterizedType, Type}

import org.junit.jupiter.api.Test

import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.integtest.BaseTest
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import org.junit.jupiter.api.Assertions

class ScalaModuleTest extends BaseTest {

  val mapper = new ObjectMapper().registerModule(DefaultScalaModule)

  @Test
  def testBasicCaseClass(): Unit = {
    val instance = ConstructorTestCaseClass(12, "text")
    val json = mapper.writeValueAsString(instance)
    Assertions.assertTrue(json.contains(""""intValue":12"""), "json text includes intValue")
    Assertions.assertTrue(json.contains(""""stringValue":"text""""), "json text includes stringValue")
    val deserializedInstance = deserialize[ConstructorTestCaseClass](json)
    Assertions.assertEquals(instance, deserializedInstance)
  }

  private def deserialize[T: Manifest](value: String) : T = mapper.readValue(value, typeReference[T])

  private def typeReference[T: Manifest]: TypeReference[T] = new TypeReference[T] {
    override def getType: Type = typeFromManifest(manifest[T])
  }

  private def typeFromManifest(m: Manifest[_]): Type = {
    if (m.typeArguments.isEmpty) { m.runtimeClass }
    else new ParameterizedType {
      override def getRawType: Class[_] = m.runtimeClass

      override def getActualTypeArguments: Array[Type] = m.typeArguments.map(typeFromManifest).toArray

      override def getOwnerType: Null = null
    }
  }
}
