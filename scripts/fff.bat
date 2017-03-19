@echo off

echo %TOMCAT_HOME%
set TARGET=%TOMCAT_HOME%\work\Catalina
echo About to remove stuff under: %TARGET%\localhost
rd /s/q %TARGET%\localhost
echo Finished.
dir %TARGET%