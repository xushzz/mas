rem build verification
@echo off

rem chcp 65001

set JRE_HOME=C:\Java\jre8
set ORIGIN=%PATH%
set PATH=%ORIGIN%;%JRE_HOME%\bin
set CLASS_PATH=%JRE_HOME%\lib\rt.jar
set proj=F:\BK\Mas
if  not "%1"=="" goto setvalue

:accept
dir %proj%
set /p name=select a version:
if "%name%"=="q" goto end
goto app

:setvalue
set name=%1

:app
set base=%proj%\%name%
if "%base:~-1%"=="\" set base=%base:~0,-1%

set folder=%base%\jars
set version=3.3-SNAPSHOT
set repo=D:\M2REPO

set CP=%folder%\sirap-common-%version%.jar;%folder%\sirap-basic-%version%.jar

set CP=%CP%;%repo%\com\itextpdf\itextpdf\5.5.8\itextpdf-5.5.8.jar
set CP=%CP%;%repo%\com\itextpdf\itext-asian\5.2.0\itext-asian-5.2.0.jar
set CP=%CP%;%repo%\com\google\zxing\core\3.2.1\core-3.2.1.jar
set CP=%CP%;%repo%\com\google\zxing\javase\2.2\javase-2.2.jar
set CP=%CP%;%repo%\javax\mail\mail\1.4\mail-1.4.jar
set CP=%CP%;%repo%\dom4j\dom4j\1.6.1\dom4j-1.6.1.jar
set CP=%CP%;%repo%\jaxen\jaxen\1.1.6\jaxen-1.1.6.jar

set APP=com.sirap.common.entry.AppMas

title Sirap, build verification. %CD%
set storage=%base%
set userConfig=%base%\ship.properties
java -cp "%CP%" "%APP%" "%storage%" "%userConfig%"
set PATH=%ORIGIN%

:end
pause