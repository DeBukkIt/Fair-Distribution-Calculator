@echo off
jlink --module-path $JAVA_HOME/jmods --add-modules java.base,java.desktop --strip-debug --no-header-files --no-man-pages --output runtime
jpackage --type app-image --name FairDistributionCalculator --input target --main-jar FairStudentDistribution-0.0.1-SNAPSHOT-jar-with-dependencies.jar --main-class eu.ajg.fairdistribution.Main --runtime-image runtime --dest output
pause
