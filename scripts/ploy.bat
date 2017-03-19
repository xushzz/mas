@echo off
rem by KY, 21:06 2014/7/9
title I am deploying

set target=D:\Github\SIRAP\mas-app
set jars=F:\BK\Mas\latestjars
set deps=F:\BK\Mas\latestdeps
set script=D:\Github\SIRAP\mas\scripts

if exist %target% (goto deploy) else (goto create)

:create
md %target%
md %target%\jars
md %target%\jars\deps

:deploy

dir %base%
xcopy /y %jars%\*.* %target%\jars
xcopy /y %deps%\*.* %target%\jars\deps
copy %script%\nomad.properties %target%
copy %script%\nomad.bat %target%

dir %target%
:end
pause