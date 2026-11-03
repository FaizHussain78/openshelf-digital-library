@echo off
setlocal
cd /d "%~dp0"
where javac >nul 2>nul
if errorlevel 1 (
  echo JDK 17 or newer is required. Install a JDK and add its bin folder to PATH.
  pause
  exit /b 1
)
if not exist out mkdir out
if exist out\sources.txt del out\sources.txt
for /r src\main\java %%f in (*.java) do echo %%f>>out\sources.txt
javac --add-modules jdk.httpserver -encoding UTF-8 -d out @out\sources.txt
if errorlevel 1 (
  echo Java compilation failed.
  pause
  exit /b 1
)
xcopy /e /i /y "src\main\resources\*" "out\" >nul
java --add-modules jdk.httpserver -cp out library.LibraryServer
pause
