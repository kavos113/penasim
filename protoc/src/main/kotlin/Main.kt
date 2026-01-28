package com.example.penasim.protoc

import com.example.penasim.Options
import com.google.protobuf.ExtensionRegistry
import com.google.protobuf.compiler.PluginProtos
import java.io.File
import java.nio.file.Paths

fun main() {
//    System.err.println("protoc called")
//
//    val bytes = System.`in`.readBytes()
//    File("input.bin").writeBytes(bytes)

//    val request = PluginProtos.CodeGeneratorRequest.parseFrom(bytes)
//    val responseBuilder = PluginProtos.CodeGeneratorResponse.newBuilder()
//
//    for (file in request.protoFileList) {
//        for (msg in file.messageTypeList) {
//            responseBuilder.addFile(
//                PluginProtos.CodeGeneratorResponse.File.newBuilder()
//                    .setName("testgen/${msg.name}.kt")
//                    .setContent(msg.fieldCount.toString())
//                    .build()
//            )
//        }
//    }1
//
//    responseBuilder.build().writeTo(System.out)

  val registry = ExtensionRegistry.newInstance()
  Options.registerAllExtensions(registry)

  val request = PluginProtos.CodeGeneratorRequest.parseFrom(File("input.bin").readBytes(), registry)
  println(Generator.gen(request))
}