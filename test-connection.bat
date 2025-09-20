@echo off
echo ================================
echo SYOS Database Connection Setup
echo ================================
echo.

REM Create lib directory for dependencies
if not exist "lib" mkdir lib

REM Check for MySQL Connector/J (support both old and new naming)
if exist "lib\mysql-connector-j-9.4.0.jar" (
    echo Using existing MySQL Connector: mysql-connector-j-9.4.0.jar
    goto compile
) else if exist "lib\mysql-connector-java-8.0.33.jar" (
    echo Using existing MySQL Connector: mysql-connector-java-8.0.33.jar
    goto compile_old
) else (
    echo MySQL Connector not found. Please download from:
    echo https://dev.mysql.com/downloads/connector/j/
    echo and place it in the lib folder
    pause
    exit /b 1
)

:compile
echo.
echo Compiling Java classes...

REM Compile the test class with new connector
javac -cp "lib\mysql-connector-j-9.4.0.jar" -d . src\main\java\com\syos\test\SimpleDatabaseTest.java

if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!
echo.
echo Running database connection test...
echo ================================

REM Run the test with new connector
java -cp ".;lib\mysql-connector-j-9.4.0.jar" com.syos.test.SimpleDatabaseTest
goto end

:compile_old
echo.
echo Compiling Java classes...

REM Compile the test class with old connector
javac -cp "lib\mysql-connector-java-8.0.33.jar" -d . src\main\java\com\syos\test\SimpleDatabaseTest.java

if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!
echo.
echo Running database connection test...
echo ================================

REM Run the test with old connector
java -cp ".;lib\mysql-connector-java-8.0.33.jar" com.syos.test.SimpleDatabaseTest
goto end

:end

echo.
echo ================================
echo Test completed!
pause
