@echo off
cd
call "C:\Program Files\Microsoft Visual Studio\2022\Community\VC\Auxiliary\Build\vcvars64.bat"

call ".\env.cmd"
set WRK_SPASE="I:\JavaIDE\TextReaderJE\out\artifacts\c__build_installer"

echo.
echo Build EXE file by GraalVM (Please see your env.cmd!)
echo.
echo TOOL PATH = %BUILD_JDK%\native-image.cmd
echo.

cd %WRK_SPASE%
echo WORK SPASE =

cd
echo.

echo.
rem echo if you sure is all right, press any key to build.
rem pause
echo.
echo BUILD START...
echo.


%BUILD_JDK%\native-image.cmd -Dfile.encoding=GBK --initialize-at-build-time=sun.awt.resources.awt_zh_CN,java.awt.Desktop,sun.awt.resources.awt,sun.awt.WeakIdentityHashMap,java.awt,javax.swing,java.desktop --initialize-at-run-time=java.desktop,sun.java2d.Disposer,java.awt.Toolkit,sun.awt.windows.WToolkit,java.awt.Toolkit,java.awt,javax.swing,sun.awt --trace-object-instantiation=java.lang.Thread -Djava.awt.headless=true -jar "%WRK_SPASE%\setup.jar"

copy /b ..\c__build\7zSD.sfx + archive.7z out.exe

%SEVEN_ZIP_PATH%\7z.exe a -mx9 archive.7z * -x!7zSD.sfx

%SEVEN_ZIP_PATH%\7z.exe a -sfx7zSD.sfx ..\out.exe .\* -mx9


rem for %f in (*.*) do if /i not "%f"=="out.exe" del "%f"

echo.
echo complete.
echo.
pause