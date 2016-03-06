if ! ps aux|grep -q com.meiah.webCrawlers.TaskStartAuto|grep -v grep ;
then 
cd ..
nohup java -cp .:./lib/*  com.meiah.webCrawlers.TaskStartAuto  2>./log/log.txt &
else echo 'newsicsdis is runnings';
fi