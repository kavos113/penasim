@echo off
set JAR_PATH=%~dp0build\libs\protoc-1.0-SNAPSHOT-all.jar
java -jar "%JAR_PATH%" %*