plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.shadow)
  alias(libs.plugins.protobuf)

  application
}

group = "com.example.penasim.protoc"
version = "1.0-SNAPSHOT"

dependencies {
  implementation(libs.protobuf.java)
  implementation(libs.kotlinpoet)

  implementation(project(":proto"))
}

application {
  mainClass = "com.example.penasim.protoc.MainKt"
}

tasks.build {
  dependsOn(tasks.shadowJar)
}

protobuf {
  protoc {
    artifact = libs.protoc.toString()
  }
}