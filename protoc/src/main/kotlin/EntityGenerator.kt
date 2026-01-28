package com.example.penasim.protoc

import com.google.protobuf.DescriptorProtos
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Type.*
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import kotlin.reflect.KClass

object EntityGenerator {
  fun gen(message: DescriptorProtos.DescriptorProto, packageName: String): FileSpec {
    val fileBuilder = FileSpec.builder(packageName, "${message.name}Entity")
    var classBuilder = TypeSpec.classBuilder("${message.name}Entity")
      .addModifiers(KModifier.DATA)

    var constructorBuilder = FunSpec.constructorBuilder()
    for (field in message.fieldList) {
      println("${field.name}: ${field.type}")
      constructorBuilder = constructorBuilder.addParameter(field.name, protoTypeToPoetType(field.type))
      classBuilder = classBuilder.addProperty(
        PropertySpec.builder(field.name, protoTypeToPoetType(field.type))
          .initializer(field.name)
          .build()
      )
    }

    return fileBuilder
      .addType(
        classBuilder
          .primaryConstructor(constructorBuilder.build())
          .build()
      )
      .build()
  }

  private fun protoTypeToPoetType(type: DescriptorProtos.FieldDescriptorProto.Type): KClass<*> {
    return when (type) {
      TYPE_DOUBLE -> Double::class
      TYPE_FLOAT -> Float::class
      TYPE_INT64 -> Long::class
      TYPE_INT32 -> Int::class
      TYPE_UINT64 -> ULong::class
      TYPE_UINT32 -> UInt::class
      TYPE_FIXED64 -> ULong::class
      TYPE_FIXED32 -> UInt::class
      TYPE_BOOL -> Boolean::class
      TYPE_STRING -> String::class
      TYPE_SFIXED32 -> Int::class
      TYPE_SFIXED64 -> Long::class
      TYPE_SINT32 -> Int::class
      TYPE_SINT64 -> Long::class
      else -> throw IllegalArgumentException("need to deserialize nested parameter")
    }
  }
}