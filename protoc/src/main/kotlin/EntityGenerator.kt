package com.example.penasim.protoc

import com.example.penasim.Options
import com.google.protobuf.DescriptorProtos
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Type.*
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import kotlin.reflect.KClass

object EntityGenerator {
  fun gen(message: DescriptorProtos.DescriptorProto, packageName: String): FileSpec =
    FileSpec.builder(packageName, "${message.name}Entity")
      .addType(
        TypeSpec.classBuilder("${message.name}Entity")
          .addModifiers(KModifier.DATA)
          .addProperties(generateProperties(message.fieldList))
          .primaryConstructor(
            FunSpec.constructorBuilder()
              .addParameters(generateParameters(message.fieldList))
              .build()
          )
          .addAnnotation(AnnotationBuilder.gen(message.options))
          .build()
      )
      .build()

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

  private fun generateParameters(fields: List<DescriptorProtos.FieldDescriptorProto>): List<ParameterSpec> =
    fields.map { ParameterSpec.builder(it.name, protoTypeToPoetType(it.type)).build() }

  private fun generateProperties(fields: List<DescriptorProtos.FieldDescriptorProto>): List<PropertySpec> = fields.map {
    PropertySpec.builder(it.name, protoTypeToPoetType(it.type))
      .initializer(it.name)
      .build()
  }

  object AnnotationBuilder {
    fun gen(options: DescriptorProtos.MessageOptions): AnnotationSpec {
      return AnnotationSpec.builder(ClassName("androidx.room", "Entity"))
        .addTableOptionsIfExists(options)
        .build()
    }

    private fun AnnotationSpec.Builder.addTableOptionsIfExists(options: DescriptorProtos.MessageOptions): AnnotationSpec.Builder {
      if (options.hasExtension(Options.tableOptions)) {
        val tableName = options.getExtension(Options.tableOptions).tableName
        return addMember("tableName = %S", tableName)
      } else {
        return this
      }
    }
  }
}