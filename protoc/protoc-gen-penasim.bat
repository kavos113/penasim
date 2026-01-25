@echo off
set NAME=protoc
set VERSION=1.0-SNAPSHOT

set JAR_PATH=%~dp0build\libs\%NAME%-%VERSION%-all.jar
java -jar "%JAR_PATH%" %*