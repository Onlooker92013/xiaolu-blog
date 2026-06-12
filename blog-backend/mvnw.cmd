@REM ----------------------------------------------------------------------------
@REM Maven Wrapper startup script for Windows
@REM ----------------------------------------------------------------------------

@if "%DEBUG%" == "" @echo off
@setlocal

set MAVEN_PROJECTBASEDIR=%CD%
set MAVEN_USER_HOME=%USERPROFILE%\.m2

if not defined MAVEN_HOME (
    if exist "%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.6" (
        for /f "tokens=*" %%i in ('dir /b /ad "%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.6\*" 2^>nul') do (
            set "MVN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.6\%%i"
        )
    )
)

set "JAVA_EXE=javac.exe"
set "JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot"

if exist "%JAVA_HOME%\bin\java.exe" (
    set "JAVACMD=%JAVA_HOME%\bin\java.exe"
) else (
    set "JAVACMD=java"
)

if exist "%MVN_HOME%\bin\mvn.cmd" (
    set "MAVEN_CMD=%MVN_HOME%\bin\mvn.cmd"
) else (
    set "MAVEN_CMD=mvn"
)

%MAVEN_CMD% %*
