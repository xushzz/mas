#!/bin/sh

# @echo off
# rem chcp 65001

JRE_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_111.jdk/Contents/Home/jre
ORIGIN=$PATH
PATH=$JRE_HOME/bin
CLASS_PATH=$JRE_HOME/lib/rt.jar

cd /Users/ky/Documents/mas

storage=/Users/ky/Documents/mas
config=/Users/ky/Documents/mas/dev.properties
where=/Users/ky/Documents/farm/gitplace/mas
repo=/Users/ky/Documents/zoo/m2repo

CP=“”
CP=$CP:$where/sirap-common/target/classes
CP=$CP:$where/sirap-basic/target/classes
CP=$CP:$where/sirap-geek/target/classes
CP=$CP:$where/sirap-bible/target/classes
CP=$CP:$where/sirap-security/target/classes

CP=$CP:$repo/com/itextpdf/itextpdf/5.5.8/itextpdf-5.5.8.jar
CP=$CP:$repo/com/itextpdf/itext-asian/5.2.0/itext-asian-5.2.0.jar
CP=$CP:$repo/com/google/zxing/core/3.2.1/core-3.2.1.jar
CP=$CP:$repo/com/google/zxing/javase/2.2/javase-2.2.jar
CP=$CP:$repo/javax/mail/mail/1.4/mail-1.4.jar
CP=$CP:$repo/mysql/mysql-connector-java/5.1.31/mysql-connector-java-5.1.31.jar
CP=$CP:$repo/com/jcraft/jsch/0.1.53/jsch-0.1.53.jar
CP=$CP:$repo/com/oracle/ojdbc14/10.2.0.4.0/ojdbc14-10.2.0.4.0.jar
CP=$CP:$repo/org/apache/pdfbox/fontbox/2.0.3/fontbox-2.0.3.jar
CP=$CP:$repo/org/apache/pdfbox/pdfbox/2.0.3/pdfbox-2.0.3.jar
CP=$CP:$repo/commons-logging/commons-logging/1.2/commons-logging-1.2.jar
CP=$CP:$repo/org/bouncycastle/bcprov-jdk15on/1.55/bcprov-jdk15on-1.55.jar
CP=$CP:$repo/org/jaudiotagger/2.0.3/jaudiotagger-2.0.3.jar
CP=$CP:$repo/org/apache/poi/poi/3.7/poi-3.7.jar

APP=com.sirap.common.entry.AppMas

java -cp "$CP" "$APP" "$storage" "$config"

PATH=$ORIGIN