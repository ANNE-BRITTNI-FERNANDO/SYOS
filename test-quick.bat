@echo off
echo Quick Database Connection Test
echo.

echo Make sure XAMPP MySQL is running on port 3306
echo Database: syos_db
echo Username: syos_user
echo Password: temp1234
echo.

REM Compile and run the simple connection test
echo Compiling simple test...
javac -d out src\main\java\com\syos\test\SimpleConnectionTest.java src\main\java\com\syos\infrastructure\database\*.java

if %ERRORLEVEL% neq 0 (
    echo Compilation failed! Make sure you have the MySQL connector JAR file.
    pause
    exit /b 1
)

echo Running connection test...
java -cp "out;lib\*" com.syos.test.SimpleConnectionTest

pause