@echo off
rem piratewithoutsea@163.com
rem https://github.com/acesfullmike/mas
rem https://github.com/acesfullmike/masrun
rem chcp 65001

set jre=C:\java\jre8
set jars=%cd%\jars
set deps=%cd%\jars\deps
set slogan=We are building ship.

set params=storage=%cd%
set params=%params%,userConfig=%cd%\nomad.properties

if not exist "%jre%" goto nojre

set ORIGIN=%PATH%
set PATH=%ORIGIN%;%jre%\bin
set CLASS_PATH=%JRE_HOME%\lib\rt.jar

set version=3.5-SNAPSHOT
set alpha_version=1.0-SNAPSHOT

set CP=""
set CP=%CP%;%jars%\sirap-basic-%version%.jar
set CP=%CP%;%jars%\sirap-common-%version%.jar
set CP=%CP%;%jars%\sirap-extractor-%alpha_version%.jar
set CP=%CP%;%jars%\sirap-executor-%alpha_version%.jar
set CP=%CP%;%jars%\sirap-ldap-%alpha_version%.jar
set CP=%CP%;%jars%\sirap-db-%alpha_version%.jar
set CP=%CP%;%jars%\sirap-security-1.0.jar
set CP=%CP%;%jars%\sirap-geek-%alpha_version%.jar
set CP=%CP%;%jars%\sirap-bible-%alpha_version%.jar

set CP=%CP%;%deps%\mail-1.4.jar
set CP=%CP%;%deps%\itextpdf-5.5.8.jar
set CP=%CP%;%deps%\itext-asian-5.2.0.jar
set CP=%CP%;%deps%\core-3.2.1.jar
set CP=%CP%;%deps%\javase-2.2.jar
set CP=%CP%;%deps%\pdfbox-2.0.3.jar
set CP=%CP%;%deps%\commons-logging-1.2.jar
set CP=%CP%;%deps%\fontbox-2.0.3.jar
set CP=%CP%;%deps%\mysql-connector-java-5.1.31.jar
set CP=%CP%;%deps%\jaudiotagger-2.0.3.jar
set CP=%CP%;%deps%\poi-3.7.jar

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