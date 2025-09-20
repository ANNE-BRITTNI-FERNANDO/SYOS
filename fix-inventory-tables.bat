@echo off
echo ================================
echo SYOS Simple Inventory Creator
echo ================================
echo.

REM Check for MySQL Connector/J
if exist "lib\mysql-connector-j-9.4.0.jar" (
    echo Using MySQL Connector: mysql-connector-j-9.4.0.jar
    set CONNECTOR=lib\mysql-connector-j-9.4.0.jar
    goto compile
) else if exist "lib\mysql-connector-java-8.0.33.jar" (
    echo Using MySQL Connector: mysql-connector-java-8.0.33.jar
    set CONNECTOR=lib\mysql-connector-java-8.0.33.jar
    goto compile
) else (
    echo MySQL Connector not found. Please download from:
    echo https://dev.mysql.com/downloads/connector/j/
    echo and place it in the lib folder
    pause
    exit /b 1
)

:compile
echo.
echo Compiling SimpleInventoryCreator...

REM Compile the simple inventory creator
javac -cp "%CONNECTOR%" -d . src\main\java\com\syos\test\SimpleInventoryCreator.java

if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!
echo.
echo Creating inventory tables step by step...
echo ================================

REM Run the simple inventory creator
java -cp ".;%CONNECTOR%" com.syos.test.SimpleInventoryCreator

echo.
echo ================================
echo.
if errorlevel 0 (
    echo SUCCESS! All inventory tables have been created.
    echo Your POS system is now fully operational!
    echo.
    echo Next steps:
    echo 1. Run your Main.java application
    echo 2. Use the POS system with full inventory management
    echo 3. All features should now work properly
) else (
    echo There was an issue creating the tables.
    echo Please check the error messages above.
)
echo.
pause