#!/bin/sh

java -classpath lib/*:bin/. edu.upenn.cis.cis455.webserver.HttpServer 8080 www conf/web.xml &
sleep 2
curl http://localhost:8080/control
curl http://localhost:8080/shutdown
