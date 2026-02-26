@ECHO OFF
SET DIRNAME=%~dp0
IF EXIST "%DIRNAME%\gradle\wrapper\gradle-wrapper.jar" (
  java -Dorg.gradle.appname=gradlew -classpath "%DIRNAME%\gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
  EXIT /B %ERRORLEVEL%
)
ECHO gradle-wrapper.jar is missing. Use a local Gradle installation or run on Unix with ./gradlew test offline mode.
EXIT /B 1
