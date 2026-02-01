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
  fun gen(message: DescriptorProtos.DescriptorProto, packageName: String): FileSpec {
    val builder = EntityAnnotationBuilder()

    builder.setTableOption(message.options)
    message.fieldList.forEach { field ->
      builder.addFieldOption(field.options, field.name)
    }

    val fileBuilder = with(builder) {
      FileSpec.builder(packageName, "${message.name}Entity")
        .addType(
          TypeSpec.classBuilder("${message.name}Entity")
            .addModifiers(KModifier.DATA)
            .addProperties(generateProperties(message.fieldList, this))
            .primaryConstructor(
              FunSpec.constructorBuilder()
                .addParameters(generateParameters(message.fieldList))
                .build()
            )
            .addAnnotation(generateClassAnnotation())
            .build()
        )
    }

    return fileBuilder.build()
  }

  private fun protoTypeToPoetType(type: DescriptorProtos.FieldDescriptorProto.Type): KClass<*> = when (type) {
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

  private fun generateParameters(fields: List<DescriptorProtos.FieldDescriptorProto>): List<ParameterSpec> =
    fields.map { ParameterSpec.builder(it.name, protoTypeToPoetType(it.type)).build() }

  private fun generateProperties(fields: List<DescriptorProtos.FieldDescriptorProto>, builder: EntityAnnotationBuilder): List<PropertySpec> = fields.map {
    with(builder) {
      PropertySpec.builder(it.name, protoTypeToPoetType(it.type))
        .initializer(it.name)
        .addPrimaryKeyAnnotationIfSingle(it.name)
        .build()
    }
  }

  class EntityAnnotationBuilder {
    var tableOptions: Options.TableOptions? = null
    val fieldOptions: MutableMap<String, Options.FieldOptions> = mutableMapOf()

    val isSinglePrimaryKey: Boolean
      get() = fieldOptions.values.count { it.hasIsPrimaryKey() && it.isPrimaryKey } == 1

    fun setTableOption(options: DescriptorProtos.MessageOptions) {
      if (options.hasExtension(Options.tableOptions)) {
        tableOptions = options.getExtension(Options.tableOptions)
      }
    }

    fun addFieldOption(options: DescriptorProtos.FieldOptions, fieldName: String) {
      if (options.hasExtension(Options.fieldOptions)) {
        fieldOptions[fieldName] = options.getExtension(Options.fieldOptions)
      }
    }

    fun PropertySpec.Builder.addPrimaryKeyAnnotationIfSingle(fieldName: String): PropertySpec.Builder {
      val option = fieldOptions[fieldName]
      if (isSinglePrimaryKey && option != null && option.hasIsPrimaryKey() && option.isPrimaryKey) {
        return addAnnotation(ClassName("androidx.room", "PrimaryKey"))
      }
      return this
    }

    fun generateClassAnnotation(): AnnotationSpec {
      return AnnotationSpec.builder(ClassName("androidx.room", "Entity"))
        .addTableName()
        .addMultiplePrimaryKeys()
        .addForeignKeys()
        .addIndices()
        .build()
    }

    private fun AnnotationSpec.Builder.addTableName(): AnnotationSpec.Builder {
      tableOptions?.tableName?.let {
        return this.addMember("tableName = %S", it)
      }
      return this
    }

    private fun AnnotationSpec.Builder.addMultiplePrimaryKeys(): AnnotationSpec.Builder {
      if (!isSinglePrimaryKey) {
        val primaryKeys = fieldOptions.filter { it.value.hasIsPrimaryKey() && it.value.isPrimaryKey }
          .keys.joinToString(", ") { "\"$it\"" }

        return this.addMember("primaryKeys = [%L]", primaryKeys)
      }
      return this
    }

    private fun AnnotationSpec.Builder.addForeignKeys(): AnnotationSpec.Builder {
      val foreignKeys = fieldOptions.filter { it.value.hasForeignKey() }
        .toList()
        .map {
          AnnotationSpec.builder(ClassName("androidx.room", "ForeignKey"))
            .addMember("entity = ${it.second.foreignKey.parentTable}Entity::class")
            .addMember("parentColumns = [%S]", it.second.foreignKey.parent)
            .addMember("childColumns = [%S]", it.first)
            .addMember("onDelete = ForeignKey.CASCADE")
            .build()
        }

      if (foreignKeys.isNotEmpty()) {
        val foreignKeysArray = foreignKeys.joinToString(", ") { "%L" }
        return this.addMember("foreignKeys = [$foreignKeysArray]", *foreignKeys.toTypedArray())
      } else {
        return this
      }
    }

    private fun AnnotationSpec.Builder.addIndices(): AnnotationSpec.Builder {
      val indices = fieldOptions.filter { it.value.hasIsIndex() && it.value.isIndex }
        .toList()
        .map {
          AnnotationSpec.builder(ClassName("androidx.room", "Index"))
            .addMember("value = [%S]", it.first)
            .build()
        }

      if (indices.isNotEmpty()) {
        val indicesArray = indices.joinToString(", ") { "%L" }
        return this.addMember("indices = [%L]", indicesArray, *indices.toTypedArray())
      } else {
        return this
      }
    }
  }
}