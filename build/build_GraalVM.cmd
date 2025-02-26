@echo off


set BUILD_JDK="C:\Program Files\Java\graalvm-jdk-17.0.11+7.1\bin\"
set WRK_SPASE="I:\JavaIDE\TextReaderJE\out\artifacts\c__build\"
set SEVEN_ZIP_PATH="C:\Program Files (x86)\7-Zip\"



echo.
echo Build EXE file by GraalVM (PLEASE SEE YOUR CONFIG)
echo.
echo TOOL PATH = %BUILD_JDK%\native-image.cmd
echo.
cd %WRK_SPASE%
echo WORK SPASE =
cd
echo.

echo.
echo IF CONFIGS ARE RIGHT , PRESS ANY KEY CONTINUE ...
pause
echo.
echo BUILD START...
echo.
%SEVEN_ZIP_PATH%\7z.exe x c++build.jar -o"%WRK_SPASE%"
%BUILD_JDK%\native-image.cmd com.teipreader.Main.Main -cp "c++build.jar;lib\annotations-13.0.jar;lib\epublib-core-4.0-complete.jar;lib\hamcrest-core-1.3.jar;lib\junit-4.12.jar;lib\kotlin-stdlib-1.3.40.jar;lib\kotlin-stdlib-common-1.3.40.jar;lib\mockwebserver-4.0.1.jar;lib\okhttp-4.0.1.jar;lib\okio-2.2.2.jar;lib\pdfbox-app-2.0.30.jar;lib\textreader-libcomic.jar"


echo.
echo complete.
echo.
pause