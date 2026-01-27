package com.example.penasim.protoc

import com.google.protobuf.compiler.PluginProtos
import java.io.File
import java.nio.file.Paths

fun main() {
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
//    System.err.println("called")
//
//    responseBuilder.build().writeTo(System.out)

    val request = PluginProtos.CodeGeneratorRequest.parseFrom(File("input.bin").readBytes())
    println(Generator.gen(request))
}