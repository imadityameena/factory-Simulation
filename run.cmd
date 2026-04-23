@echo off
setlocal EnableDelayedExpansion

pushd "%~dp0"

if not exist out mkdir out

set "SOURCES="
for /r "src\main\java" %%f in (*.java) do set "SOURCES=!SOURCES! "%%f""

javac -d out !SOURCES!
if errorlevel 1 (
  echo Build failed.
  popd
  exit /b 1
)

if /I "%~1"=="--build-only" (
  echo Build successful.
  popd
  exit /b 0
)

java -cp out com.factorysimulation.App
set EXIT_CODE=%ERRORLEVEL%

popd
exit /b %EXIT_CODE%