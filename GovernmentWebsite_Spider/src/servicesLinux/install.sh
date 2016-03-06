#!/bin/sh
#for centos, root authority
chkconfig --add crond
service crond start
cp newsics_cron_hourly.sh /etc/cron.hourly/newsics_cron_hourly.sh