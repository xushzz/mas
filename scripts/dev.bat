@echo off
rem 23:47 2016/1/28
rem piratewithoutsea@163.com
rem https://github.com/piratesea/mas
rem https://github.com/piratesea/mas-app

rem chcp 65001

set jre=C:\java\jre8
set where=D:\Github\SIRAP\mas
set repo=D:\M2REPO
set slogan=Dev Talks

set /p privatekey=<E:\Mas\privatekey.txt
set params=storage=E:\Mas
set params=%params%,userConfig=D:\Github\SIRAP\mas\scripts\dev.properties
set params=%params%,passcode=%privatekey%

if not exist "%jre%" goto nojre

set ORIGIN=%PATH%
set PATH=%ORIGIN%;%jre%\bin
set CLASS_PATH=%JRE_HOME%\lib\rt.jar

set CP=""
set CP=%CP%;%repo%\com\itextpdf\itextpdf\5.5.8\itextpdf-5.5.8.jar
set CP=%CP%;%repo%\com\itextpdf\itext-asian\5.2.0\itext-asian-5.2.0.jar
set CP=%CP%;%repo%\com\google\zxing\core\3.2.1\core-3.2.1.jar
set CP=%CP%;%repo%\com\google\zxing\javase\2.2\javase-2.2.jar
set CP=%CP%;%repo%\javax\mail\mail\1.4\mail-1.4.jar
set CP=%CP%;%repo%\mysql\mysql-connector-java\5.1.31\mysql-connector-java-5.1.31.jar
set CP=%CP%;%repo%\com\jcraft\jsch\0.1.53\jsch-0.1.53.jar
set CP=%CP%;%repo%\com\oracle\ojdbc14\10.2.0.4.0\ojdbc14-10.2.0.4.0.jar
rem set CP=%CP%;%repo%\com\sirap\sirap-security\1.0\sirap-security-1.0.jar
set CP=%CP%;%repo%\org\apache\pdfbox\fontbox\2.0.3\fontbox-2.0.3.jar
set CP=%CP%;%repo%\org\apache\pdfbox\pdfbox\2.0.3\pdfbox-2.0.3.jar
set CP=%CP%;%repo%\commons-logging\commons-logging\1.2\commons-logging-1.2.jar
set CP=%CP%;%repo%\org\bouncycastle\bcprov-jdk15on\1.55\bcprov-jdk15on-1.55.jar
set CP=%CP%;%repo%\org\jaudiotagger\2.0.3\jaudiotagger-2.0.3.jar
set CP=%CP%;%repo%\org\apache\poi\poi\3.7\poi-3.7.jar

set CP=%CP%;%where%\sirap-basic\target\classes
set CP=%CP%;%where%\sirap-common\target\classes
set CP=%CP%;%where%\sirap-db\target\classes
set CP=%CP%;%where%\sirap-extractor\target\classes
set CP=%CP%;%where%\sirap-executor\target\classes
set CP=%CP%;%where%\sirap-ldap\target\classes
set CP=%CP%;%where%\sirap-geek\target\classes
set CP=%CP%;%where%\sirap-security\target\classes
set CP=%CP%;%where%\sirap-bible\target\classes

set APP=com.sirap.common.entry.AppMas

title %slogan%. %CD%
java -cp "%CP%" "%APP%" "%params%"
set PATH=%ORIGIN%
title %cd%

goto end

:nojre
echo JRE [%jre%] doesn't exist, please check.

:end
pause