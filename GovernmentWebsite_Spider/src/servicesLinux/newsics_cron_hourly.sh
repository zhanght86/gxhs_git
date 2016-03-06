#!/bin/sh
#for centos, root authority
syspath=/home/huhb/FI-2100ND/NEWSICS2
if ! ps aux|grep -q com.meiah.webCrawlers.TaskStartAuto|grep -v grep ;
then 
cd "$syspath"
nohup java -cp .:./lib/*  com.meiah.webCrawlers.TaskStartAuto  2>./log/log.txt &
else echo 'newsicsdis is runnings';
fi