@echo off
call Config.bat
net start %ServiceName%
pause
exit