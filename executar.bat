@echo off
setlocal
cd /d "%~dp0"
if not exist "dist\cadastro-fortnite-java.jar" (
    echo Compile primeiro usando compilar.bat ou Maven.
    pause
    exit /b 1
)
set "JAVA_RUN=java"
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" set "JAVA_RUN=%JAVA_HOME%\bin\java.exe"
"%JAVA_RUN%" -cp "dist\cadastro-fortnite-java.jar;lib\*" view.JFFornite
if errorlevel 1 pause
