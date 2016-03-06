call Config.bat

echo "validate java_home..."
if exist "%JAVA_HOME%\bin\java.exe" goto OutConfigJAVAHOME
echo error: please install jdk firstly and config JAVA_HOME
pause
exit
:OutConfigJAVAHOME

if 1==0 goto NotCopyJavaService
echo "copy JavaService.exe..."
copy /y %SysPath%\services\JavaService\JavaService32.exe %SysPath%\services\%ServiceName%.exe
FOR /F "tokens=1" %%A IN ('%JAVA_HOME%\bin\java.exe -version 2^>^&1 ^| FIND /I "64-Bit"') DO (
	copy /y %SysPath%\services\JavaService\JavaService64.exe %SysPath%\services\%ServiceName%.exe
)
:NotCopyJavaService

if %JavaProcess%==0 goto NotCopyJava
echo "copy java.exe..."
copy /y "%JAVA_HOME%\bin\java.exe" %SysPath%\%ProcessName%.exe
:NotCopyJava

SET jvmdll=%JAVA_HOME%\jre\bin\server\jvm.dll
SET dtjar=%JAVA_HOME%\lib\dt.jar
set classpath="%dtjar%";"%toolsjar%";
setlocal enabledelayedexpansion
for /f %%a in ('dir %SysPath%\lib /a-d /b ^| find /i ".jar"') do (set classpath=!classpath!"%SysPath%\lib\%%a";)
set classpath=%classpath%%SysPath%
echo %classpath



%ServiceName% -install %ServiceName% "%jvmdll%" -Djava.class.path=%classpath% -start  com.meiah.webCrawlers.TaskStartAuto -out %SysPath%\log\log.txt -err %SysPath%\log\log.txt -current %SysPath%
pause
exit

