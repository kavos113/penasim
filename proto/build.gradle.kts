import com.google.protobuf.gradle.id

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.protobuf)
}

dependencies {
    implementation(libs.protobuf.kotlin)
}

protobuf {
    protoc {
        artifact = libs.protoc.get().toString()
    }

    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.builtins {
                id("kotlin")
            }
        }
    }
}