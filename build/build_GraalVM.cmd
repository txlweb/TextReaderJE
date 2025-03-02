@echo off

call "C:\Program Files\Microsoft Visual Studio\2022\Community\VC\Auxiliary\Build\vcvars64.bat"

call ".\env.cmd"


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

echo enable > exeLock
%BUILD_JDK%\native-image.cmd -Dfile.encoding=UTF-8 --initialize-at-build-time=sun.awt.resources.awt_zh_CN,java.awt.Desktop,sun.awt.resources.awt,sun.awt.WeakIdentityHashMap --initialize-at-run-time=sun.java2d.Disposer,sun.awt.windows.WToolkit,java.awt.Toolkit --trace-object-instantiation=java.lang.Thread -Djava.awt.headless=true -jar "%WRK_SPASE%\setup.jar"


echo.
echo complete.
echo.
pause