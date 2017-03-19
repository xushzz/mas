#!/bin/sh

# @echo off
# rem chcp 65001

JRE_HOME=/app/gitrepo/test/ky/software/jdk1.8.0_111/jre
ORIGIN=$PATH
PATH=$JRE_HOME/bin
CLASS_PATH=$JRE_HOME/lib/rt.jar

export PATH
base=$(pwd)

folder=$base/jars

version=3.4-SNAPSHOT
db_version=1.0-SNAPSHOT
echo $folder
CP=$folder/sirap-common-$version.jar:$folder/sirap-basic-$version.jar
CP=$CP:$folder/itextpdf-5.5.8.jar
CP=$CP:$folder/itext-asian-5.2.0.jar
CP=$CP:$folder/core-3.2.1.jar
CP=$CP:$folder/javase-2.2.jar
CP=$CP:$folder/mail-1.4.jar
CP=$CP:$folder/dom4j-1.6.1.jar
CP=$CP:$folder/jaxen-1.1.6.jar
CP=$CP:$folder/ojdbc14_g-10.2.0.4.0.jar
CP=$CP:$folder/pdfbox-2.0.3.jar
CP=$CP:$folder/commons-logging-1.2.jar
CP=$CP:$folder/fontbox-2.0.3.jar
CP=$CP:$folder/mysql-connector-java-5.1.31.jar:$folder/sirap-db-$db_version.jar
CP=$CP:$folder/jsch-0.1.53.jar:$folder/sirap-executor-$db_version.jar
CP=$CP:$folder/sirap-security-1.0.jar

APP=com.sirap.common.entry.AppMas

storage=$base
userConfig=$base/ship.properties
java -cp "$CP" "$APP" "$storage" "$userConfig"
PATH=$ORIGIN

export PATH
