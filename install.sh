#!/bin/sh

firstInstall=[ ! -f /etc/systemd/system/measurement-server.service ]

sudo cp measurement-server.service /etc/systemd/system/measurement-server.service

sudo systemctl deamon-reload

if [ ${firstInstall} ]
then
    sudo systemctl enable measurement-server.service
    sudo systemctl start measurement-server.service
else
    sudo systemctl restart measurement-server.service
fi
