#!/bin/sh

cp measurement-server-${project.version}.jar /opt/measurement-server/measurement-server.jar
cp measurement-server.service /etc/systemd/system/

systemctl daemon-reload

if [ ! -f /etc/systemd/system/measurement-server.service ]
then
    systemctl enable measurement-server.service
    systemctl start measurement-server.service
else
    systemctl restart measurement-server.service
fi
