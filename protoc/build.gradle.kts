plugins {
    alias(libs.plugins.shadow)

    application
}

group = "com.example.penasim.protoc"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(libs.protobuf)
    implementation(libs.kotlinpoet)
}

application {
    mainClass = "com.example.penasim.protoc.MainKt"
}

tasks.build {
    dependsOn(tasks.shadowJar)
}