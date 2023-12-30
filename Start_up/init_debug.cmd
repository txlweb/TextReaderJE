:; if [ ! "linux" ] ; then
@echo off
goto :windows_bat_code
exit /b 0
fi

function linux_bash_code
{
    echo "<DEBUG ACCEPT KEY>just this,enjoy!" > "debug.lock"
    clear
    if test -e "./TeipReaderJavaEdition-DEBUG.jar"; then
        java -jar TeipReaderJavaEdition-DEBUG.jar
    else
        java -jar TeipReaderJavaEdition.jar
    fi
} && linux_bash_code && exit

:windows_bat_code
setlocal
echo "<DEBUG ACCEPT KEY>just this,enjoy!" > "debug.lock"
set file_path=./TeipReaderJavaEdition-DEBUG.jar
if exist %file_path% (
    java -jar TeipReaderJavaEdition-DEBUG.jar
) else (
    java -jar TeipReaderJavaEdition.jar
)
endlocal && exit /b 0



