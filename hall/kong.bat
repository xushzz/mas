@echo off
rem 23:47 2016/1/28
rem piratewithoutsea@163.com
rem https://github.com/piratesea/mas
rem https://github.com/piratesea/mas-app

rem chcp 65001

set jre=C:\java\jre8
set where=D:\Gitstuff\SIRAP\mas
set slogan=Kong Talks

set /p privatekey=<E:\Mas\privatekey.txt
set params=storage=E:\Mas
set params=%params%,userConfig=D:\Gitstuff\SIRAP\mas\hall\kong.properties
set params=%params%,passcode=%privatekey%

if not exist "%jre%" goto nojre

set ORIGIN=%PATH%
set PATH=%ORIGIN%;%jre%\bin
set CLASS_PATH=%JRE_HOME%\lib\rt.jar

rem start of maven deps
set CP=""
set CP=%CP%;D:\M2REPO\javax\mail\mail\1.4\mail-1.4.jar
set CP=%CP%;D:\M2REPO\javax\activation\activation\1.1\activation-1.1.jar
set CP=%CP%;D:\M2REPO\com\itextpdf\itextpdf\5.5.8\itextpdf-5.5.8.jar
set CP=%CP%;D:\M2REPO\com\itextpdf\itext-asian\5.2.0\itext-asian-5.2.0.jar
set CP=%CP%;D:\M2REPO\com\sirap\sirap-security\1.0\sirap-security-1.0.jar
set CP=%CP%;D:\M2REPO\org\apache\pdfbox\pdfbox\2.0.3\pdfbox-2.0.3.jar
set CP=%CP%;D:\M2REPO\org\apache\pdfbox\fontbox\2.0.3\fontbox-2.0.3.jar
set CP=%CP%;D:\M2REPO\org\bouncycastle\bcprov-jdk15on\1.55\bcprov-jdk15on-1.55.jar
set CP=%CP%;D:\M2REPO\org\jaudiotagger\2.0.3\jaudiotagger-2.0.3.jar
set CP=%CP%;D:\M2REPO\org\apache\poi\poi\3.10-FINAL\poi-3.10-FINAL.jar
set CP=%CP%;D:\M2REPO\commons-codec\commons-codec\1.5\commons-codec-1.5.jar
set CP=%CP%;D:\M2REPO\org\apache\poi\poi-ooxml\3.10-FINAL\poi-ooxml-3.10-FINAL.jar
set CP=%CP%;D:\M2REPO\org\apache\poi\poi-ooxml-schemas\3.10-FINAL\poi-ooxml-schemas-3.10-FINAL.jar
set CP=%CP%;D:\M2REPO\org\apache\xmlbeans\xmlbeans\2.3.0\xmlbeans-2.3.0.jar
set CP=%CP%;D:\M2REPO\stax\stax-api\1.0.1\stax-api-1.0.1.jar
set CP=%CP%;D:\M2REPO\org\springframework\spring-webmvc\4.2.2.RELEASE\spring-webmvc-4.2.2.RELEASE.jar
set CP=%CP%;D:\M2REPO\org\springframework\spring-beans\4.2.2.RELEASE\spring-beans-4.2.2.RELEASE.jar
set CP=%CP%;D:\M2REPO\org\springframework\spring-context\4.2.2.RELEASE\spring-context-4.2.2.RELEASE.jar
set CP=%CP%;D:\M2REPO\org\springframework\spring-aop\4.2.2.RELEASE\spring-aop-4.2.2.RELEASE.jar
set CP=%CP%;D:\M2REPO\aopalliance\aopalliance\1.0\aopalliance-1.0.jar
set CP=%CP%;D:\M2REPO\org\springframework\spring-core\4.2.2.RELEASE\spring-core-4.2.2.RELEASE.jar
set CP=%CP%;D:\M2REPO\commons-logging\commons-logging\1.2\commons-logging-1.2.jar
set CP=%CP%;D:\M2REPO\org\springframework\spring-expression\4.2.2.RELEASE\spring-expression-4.2.2.RELEASE.jar
set CP=%CP%;D:\M2REPO\org\springframework\spring-web\4.2.2.RELEASE\spring-web-4.2.2.RELEASE.jar
set CP=%CP%;D:\M2REPO\org\springframework\spring-orm\4.2.2.RELEASE\spring-orm-4.2.2.RELEASE.jar
set CP=%CP%;D:\M2REPO\org\springframework\spring-jdbc\4.2.2.RELEASE\spring-jdbc-4.2.2.RELEASE.jar
set CP=%CP%;D:\M2REPO\org\springframework\spring-tx\4.2.2.RELEASE\spring-tx-4.2.2.RELEASE.jar
set CP=%CP%;D:\M2REPO\org\springframework\spring-test\4.2.2.RELEASE\spring-test-4.2.2.RELEASE.jar
set CP=%CP%;D:\M2REPO\cglib\cglib\2.2\cglib-2.2.jar
set CP=%CP%;D:\M2REPO\asm\asm\3.1\asm-3.1.jar
set CP=%CP%;D:\M2REPO\com\fasterxml\jackson\core\jackson-core\2.6.3\jackson-core-2.6.3.jar
set CP=%CP%;D:\M2REPO\com\fasterxml\jackson\core\jackson-databind\2.6.3\jackson-databind-2.6.3.jar
set CP=%CP%;D:\M2REPO\com\fasterxml\jackson\core\jackson-annotations\2.6.0\jackson-annotations-2.6.0.jar
set CP=%CP%;D:\M2REPO\mysql\mysql-connector-java\5.1.9\mysql-connector-java-5.1.9.jar
set CP=%CP%;D:\M2REPO\org\hibernate\hibernate-core\5.2.10.Final\hibernate-core-5.2.10.Final.jar
set CP=%CP%;D:\M2REPO\org\jboss\logging\jboss-logging\3.3.0.Final\jboss-logging-3.3.0.Final.jar
set CP=%CP%;D:\M2REPO\org\hibernate\javax\persistence\hibernate-jpa-2.1-api\1.0.0.Final\hibernate-jpa-2.1-api-1.0.0.Final.jar
set CP=%CP%;D:\M2REPO\org\javassist\javassist\3.20.0-GA\javassist-3.20.0-GA.jar
set CP=%CP%;D:\M2REPO\org\jboss\spec\javax\transaction\jboss-transaction-api_1.2_spec\1.0.1.Final\jboss-transaction-api_1.2_spec-1.0.1.Final.jar
set CP=%CP%;D:\M2REPO\org\jboss\jandex\2.0.3.Final\jandex-2.0.3.Final.jar
set CP=%CP%;D:\M2REPO\com\fasterxml\classmate\1.3.0\classmate-1.3.0.jar
set CP=%CP%;D:\M2REPO\org\hibernate\common\hibernate-commons-annotations\5.0.1.Final\hibernate-commons-annotations-5.0.1.Final.jar
set CP=%CP%;D:\M2REPO\org\mybatis\mybatis\3.4.4\mybatis-3.4.4.jar
set CP=%CP%;D:\M2REPO\org\mybatis\mybatis-spring\1.3.1\mybatis-spring-1.3.1.jar
set CP=%CP%;D:\M2REPO\dom4j\dom4j\1.6.1\dom4j-1.6.1.jar
set CP=%CP%;D:\M2REPO\xml-apis\xml-apis\1.0.b2\xml-apis-1.0.b2.jar
set CP=%CP%;D:\M2REPO\antlr\antlr\2.7.7\antlr-2.7.7.jar
set CP=%CP%;D:\M2REPO\javax\servlet\jstl\1.2\jstl-1.2.jar
set CP=%CP%;D:\M2REPO\org\slf4j\jcl-over-slf4j\1.7.12\jcl-over-slf4j-1.7.12.jar
set CP=%CP%;D:\M2REPO\org\slf4j\slf4j-api\1.7.12\slf4j-api-1.7.12.jar
set CP=%CP%;D:\M2REPO\ch\qos\logback\logback-classic\1.1.3\logback-classic-1.1.3.jar
set CP=%CP%;D:\M2REPO\ch\qos\logback\logback-core\1.1.3\logback-core-1.1.3.jar
set CP=%CP%;D:\M2REPO\javax\servlet\javax.servlet-api\3.1.0\javax.servlet-api-3.1.0.jar
set CP=%CP%;D:\M2REPO\org\testng\testng\6.9.10\testng-6.9.10.jar
set CP=%CP%;D:\M2REPO\com\beust\jcommander\1.48\jcommander-1.48.jar
set CP=%CP%;D:\M2REPO\org\beanshell\bsh\2.0b4\bsh-2.0b4.jar
set CP=%CP%;D:\M2REPO\junit\junit\4.12\junit-4.12.jar
set CP=%CP%;D:\M2REPO\org\hamcrest\hamcrest-core\1.3\hamcrest-core-1.3.jar
set CP=%CP%;D:\M2REPO\org\easymock\easymock\3.2\easymock-3.2.jar
set CP=%CP%;D:\M2REPO\cglib\cglib-nodep\2.2.2\cglib-nodep-2.2.2.jar
set CP=%CP%;D:\M2REPO\org\objenesis\objenesis\1.3\objenesis-1.3.jar
set CP=%CP%;D:\M2REPO\com\google\guava\guava\20.0\guava-20.0.jar
rem end of maven deps

set CP=%CP%;%where%\sirap-basic\target\classes
set CP=%CP%;%where%\sirap-common\target\classes
set CP=%CP%;%where%\sirap-db\target\classes
set CP=%CP%;%where%\sirap-extractor\target\classes
set CP=%CP%;%where%\sirap-executor\target\classes
set CP=%CP%;%where%\sirap-ldap\target\classes
set CP=%CP%;%where%\sirap-geek\target\classes
set CP=%CP%;%where%\sirap-security\target\classes
set CP=%CP%;%where%\sirap-titus\target\classes
set CP=%CP%;D:\Gitstuff\SIRAP\park\spring-core\target\classes

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