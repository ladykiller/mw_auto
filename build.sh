#!/bin/sh
PROJECT_NAME=mw_auto
gradle clean
gradle installDist -x Test
cp -r conf/ build/install/"$PROJECT_NAME"/bin/conf/
cd build/install/
tar cvzf  "$PROJECT_NAME".tar.gz "$PROJECT_NAME"/
mv -f "$PROJECT_NAME".tar.gz ../../
