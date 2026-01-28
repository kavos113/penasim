package com.example.penasim.protoc

import com.google.protobuf.compiler.PluginProtos

object Generator {
  fun gen(request: PluginProtos.CodeGeneratorRequest): String {
    val fileToGenerate = request.fileToGenerateList.toSet()

    for (file in request.protoFileList) {
      if (fileToGenerate.contains(file.name)) {
        for (msg in file.messageTypeList) {
          val genFile = EntityGenerator.gen(msg, "com.example.penasim")
          val out = StringBuilder()
          genFile.writeTo(out)
          println(reduceExplicitVisibility(out.toString()))
        }
      }
    }

    return ""
  }

  private fun reduceExplicitVisibility(code: String): String =
    code.replace("""import kotlin.(String|Int|Long|UInt|ULong|Double|Float|Boolean)""".toRegex(), "")
      .replace("""public (class|fun|val|var|data|object|interface)""".toRegex(), "$1")
      .replace("""\n(\n)+""".toRegex(), "\n\n")
}