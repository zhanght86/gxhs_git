@echo off
call Config.bat


net stop %ServiceName%

if %JavaProcess%==0 goto NotStopEXE
echo "kill exe..."
taskkill /F /IM %ProcessName%.exe
:NotStopEXE

pause
exit