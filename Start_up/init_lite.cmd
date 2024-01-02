:; if [ ! "linux" ] ; then
@echo off
goto :windows_bat_code
exit /b 0
fi

function linux_bash_code
{
    clear
    java -jar TeipReaderJavaEdition-lite.jar
} && linux_bash_code && exit

:windows_bat_code
setlocal
java -jar TeipReaderJavaEdition-lite.jar
endlocal && exit /b 0



