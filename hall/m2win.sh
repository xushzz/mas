JRE_HOME=/c/java/jre8
ORIGIN=$PATH
PATH=$JRE_HOME/bin
CLASS_PATH=$JRE_HOME/lib/rt.jar

params=storage=/e/mas
params=$params:,userConfig=/d/Gitstuff/SIRAP/mas/scripts/masWin.properties
params=$params:passcode=Obamacare

jars=/d/Gitstuff/SIRAP/masrun/jars
deps=$jars/deps

export PATH

version=3.5-SNAPSHOT
alpha_version=1.0-SNAPSHOT

CP=""
CP=$CP:$jars/sirap-basic-$version.jar
CP=$CP:$jars/sirap-common-$version.jar
CP=$CP:$jars/sirap-extractor-1.0-SNAPSHOT.jar
CP=$CP:$jars/sirap-executor-1.0-SNAPSHOT.jar
CP=$CP:$jars/sirap-ldap-1.0-SNAPSHOT.jar
CP=$CP:$jars/sirap-db-1.0-SNAPSHOT.jar
CP=$CP:$jars/sirap-security-1.0.jar
CP=$CP:$jars/sirap-geek-1.0-SNAPSHOT.jar
CP=$CP:$jars/sirap-bible-1.0-SNAPSHOT.jar

CP=$CP:$deps/mail-1.4.jar
CP=$CP:$deps/mysql-connector-java-5.1.31.jar
CP=$CP:$deps/fontbox-2.0.3.jar
CP=$CP:$deps/jaudiotagger-2.0.3.jar
CP=$CP:$deps/pdfbox-2.0.3.jar
CP=$CP:$deps/commons-logging-1.2.jar
CP=$CP:$deps/itext-asian-5.2.0.jar
CP=$CP:$deps/javase-2.2.jar
CP=$CP:$deps/poi-3.7.jar
CP=$CP:$deps/core-3.2.1.jar
CP=$CP:$deps/itextpdf-5.5.8.jar

APP=com.sirap.common.entry.AppMas

java -cp "$CP" "$APP" "$params"

PATH=$ORIGIN

export PATH